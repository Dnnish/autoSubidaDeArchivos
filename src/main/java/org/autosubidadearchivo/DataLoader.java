package org.autosubidadearchivo;

import lombok.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.autosubidadearchivo.common.utils.ExcelUtils;
import org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.enums.ExcelType;
import org.autosubidadearchivo.common.utils.GenericUtils;
import org.autosubidadearchivo.common.utils.HibernateUtil;
import org.autosubidadearchivo.service.IFileService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;


@Builder
public class DataLoader {
    private static final Logger log = LogManager.getLogger(DataLoader.class);
    private final IFileService fileService;
    private final String filePath;
    private final String backUpPath;
    private final int batchSize;

    public DataLoader(IFileService fileService, String filePath, String backUpPath, int batchSize) {
        this.fileService = fileService;
        this.filePath = filePath;
        this.batchSize = batchSize;
        this.backUpPath = backUpPath;
    }
    /**
     * Processes the data insertion and post-insertion tasks.
     * @throws IOException if an I/O error occurs.
     */
    public void process() throws IOException {
        //get all files
        List<String> Filelist = fileService.getFileList(filePath);

        //File treatment
        log.info("1. Data insertion process: Init");
        for (String filePath : Filelist) {
            if (filePath.endsWith(".xlsx")) {
                byte[] currentContent = fileService.getFileContent(filePath);
                boolean success = insertData(batchSize, currentContent);
                String currentFilename = GenericUtils.extractFilenameFromPath(filePath);
                if (success) {
                    log.info("Data successfully inserted into table RPD_TRAFICO_PARTICULARES_NSMIT.");
                    //Move file to /loaded {upload to loaded dir & remove all loaded files}
                    if (fileService.uploadFile(backUpPath, currentFilename, fileService.getFileContent(filePath))) {
                        fileService.deleteFile(filePath);
                        log.info("loaded file back up done!");
                    }
                } else {
                    log.error("There was an error inserting the data into the table RPD_TRAFICO_PARTICULARES_NSMIT.");
                    System.exit(1);
                }
            }
        }
        log.info("1. Data insertion process: End");

        //Post insertion process {sql request}
        log.info("2. Post insertion process: Init");
        boolean success2 = Insert_rdp();
        if (success2) {
            log.info("Data successfully inserted into table rpd_trafico_particulares.");
        } else {
            log.error("There was an error inserting the data into the table rpd_trafico_particulares.");
            System.exit(1);
        }
        log.info("2. Post insertion process: End");
        //Close
        HibernateUtil.shutdown();
    }

    /**
     * Inserts data into the "RDP_TRAFICO_PARTICULARES_NSMIT" table.
     * @param batchSize The size of each batch for data insertion.
     * @param contentArr The byte array containing the file content.
     * @return {@code true} if the data insertion is successful, {@code false} otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean insertData(int batchSize, byte[] contentArr) throws IOException {
        String sql = "INSERT INTO RDP_TRAFICO_PARTICULARES_NSMIT\n" +
                "(ZONA, COD_CONCES, CONCESIONARIO, COD_EXPO, EXPOSICION, COD_VENDEDOR, VENDEDOR, MODELO, COD_VERSION, VERSION, FECHA_OFERTA, CONTACTO, ORIGEN, TIPO_NEGOCIACIÓN, DINSERT)\n"
                +
                "VALUES(:ZONA, :COD_CONCES, :CONCESIONARIO, :COD_EXPO, :EXPOSICION, :COD_VENDEDOR,:VENDEDOR, :MODELO, :COD_VERSION, :VERSION, :FECHA_OFERTA, :CONTACTO, :ORIGEN, :TIPO_NEGOCIACIÓN, :DINSERT)";
        String[][] dataMatrix = getFileContentAsMatrix(contentArr, 10);
        Session session = null;
        Transaction transaction = null;
        int Totalsheet= 0;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            List<String[][]> matrixList = GenericUtils.splitMatrix(dataMatrix, batchSize);

            int currentMatrixNum = 0;
            for (String[][] matrix : matrixList) {
                currentMatrixNum++;
                log.info("(batch) current set num: " + currentMatrixNum + "/ " + matrixList.size());
                for (int row = 0; row < matrix.length; row++) {
                    Query query = session.createSQLQuery(sql);
                    query.setParameter("ZONA", matrix[row][0]);
                    query.setParameter("COD_CONCES", matrix[row][1]);
                    query.setParameter("CONCESIONARIO", matrix[row][2]);
                    query.setParameter("COD_EXPO", matrix[row][3]);
                    query.setParameter("EXPOSICION", matrix[row][4]);
                    query.setParameter("COD_VENDEDOR", matrix[row][5]);
                    query.setParameter("VENDEDOR", matrix[row][6]);
                    query.setParameter("MODELO", matrix[row][7]);
                    query.setParameter("COD_VERSION", matrix[row][8]);
                    query.setParameter("VERSION", matrix[row][9]);
                    query.setParameter("FECHA_OFERTA", GenericUtils.toDate(matrix[row][10], "dd-MM-yyyy"));
                    query.setParameter("CONTACTO", matrix[row][11]);
                    query.setParameter("ORIGEN", matrix[row][12]);
                    query.setParameter("TIPO_NEGOCIACIÓN", matrix[row][13]);
                    query.setParameter("DINSERT", LocalDateTime.now());
                    query.executeUpdate();
                    Totalsheet++;
                }
                log.info("(batch) insertion complete: " + currentMatrixNum + "/ " + matrixList.size());
            }
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) {
                log.info("rollback done!");
                transaction.rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        log.info("Total row inserted: "+Totalsheet);
        return true;
    }

    /**
     * Inserts data into the "rdp_trafico_particulares" table.
     * @return {@code true} if the data insertion is successful, {@code false} otherwise.
     */
    public Boolean Insert_rdp() {
        String sql = "INSERT INTO rdp_trafico_particulares\n" +
                "            (codigo_concesionario, modelo_oferta, version_oferta,\n" +
                "             fecha_oferta, total_trafico)\n" +
                "   (SELECT   te.cod_conces, te.modelo, te.VERSION, te.fecha_oferta, COUNT (*)\n" +
                "        FROM rdp_trafico_particulares_nsmit te\n" +
                "       WHERE TRUNC (dinsert) = TRUNC (SYSDATE)\n" +
                "    GROUP BY te.cod_conces, te.modelo, te.VERSION, te.fecha_oferta)";
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Query query = session.createSQLQuery(sql);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (transaction != null) {
                transaction.rollback();
                log.info("rollback done!");
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return true;
    }

    /**
     * Retrieves the content of a file as a matrix of strings.
     * @param contentByteArr The byte array representing the content of the file.
     * @param contentRow The index of the row from which to start reading the content.
     * @return A 2-dimensional array of strings representing the content of the file.
     * @throws IOException if there is an error reading the file content.
     */
    public String[][] getFileContentAsMatrix(byte[] contentByteArr, int contentRow) throws IOException {

        // read content as byte[]
        InputStream inputStream = new ByteArrayInputStream(contentByteArr);

        // load it as Workbook
        Workbook workbook = ExcelUtils.initWorkbook(inputStream, ExcelType.XLSX);
        // Get Cell[][]
        Sheet sheet = workbook.getSheetAt(0);

        String[][] content = readSheetContent(sheet, contentRow);

        return content;
    }

    /**
     * Reads the content of a given sheet starting from the specified row index.
     * @param sheet The sheet from which to read the content.
     * @param rowIndex The index of the row from which to start reading.
     * @return Array 2-dimensional array of Strings containing the data from the sheet.
     */
    public String[][] readSheetContent(Sheet sheet, int rowIndex) {
        // Get the total number of rows and columns in the spreadsheet
        int totalRows = sheet.getLastRowNum() + 1;
        int validRows = totalRows - rowIndex;
        int totalColumns = sheet.getRow(rowIndex).getLastCellNum();
        // Create an array of Strings with the size of the total number of rows and columns
        String[][] data = new String[validRows][totalColumns];
        // Loop through all the rows and columns of the spreadsheet and add the data to the matrix
        for (int i = rowIndex; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < totalColumns; j++) {
                Cell cell = row.getCell(j);
                if (cell != null) {
                    data[i - rowIndex][j] = cell.toString();
                } else {
                    data[i - rowIndex][j] = "";
                }
            }
        }
        // Return the array of Strings
        return data;
    }
}
