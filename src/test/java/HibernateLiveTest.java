
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.autosubidadearchivo.common.utils.ExcelUtils;
import org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.enums.ExcelType;
import org.autosubidadearchivo.common.utils.HibernateUtil;
import org.autosubidadearchivo.service.IFileService;
import org.autosubidadearchivo.service.impl.FtpService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Disabled
public class HibernateLiveTest {

    private static final Logger log = LogManager.getLogger(HibernateLiveTest.class);

    @Test
    void insertTest() {
        //Arrange
        String sql = "INSERT INTO RDP_TRAFICO_PARTICULARES_NSMIT\n" +
                "(ZONA, COD_CONCES, CONCESIONARIO, COD_EXPO, EXPOSICION, COD_VENDEDOR, VENDEDOR, MODELO, COD_VERSION, VERSION, FECHA_OFERTA, CONTACTO, ORIGEN, TIPO_NEGOCIACIÓN, DINSERT)\n"
                +
                "VALUES(:ZONA, :COD_CONCES, :CONCESIONARIO, :COD_EXPO, :EXPOSICION, :COD_VENDEDOR,:VENDEDOR, :MODELO, :COD_VERSION, :VERSION, :FECHA_OFERTA, :CONTACTO, :ORIGEN, :TIPO_NEGOCIACIÓN, :DINSERT)";

        Session session = null;
        //Act

        try {

            session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            Query query = session.createSQLQuery(sql);
            query.setParameter("ZONA", "test");
            query.setParameter("COD_CONCES", "test");
            query.setParameter("CONCESIONARIO", "test");
            query.setParameter("COD_EXPO", "test");
            query.setParameter("EXPOSICION", "test");
            query.setParameter("COD_VENDEDOR", "test");
            query.setParameter("VENDEDOR", "test");
            query.setParameter("MODELO", "test");
            query.setParameter("COD_VERSION", "test");
            query.setParameter("VERSION", "test");
            query.setParameter("FECHA_OFERTA", new Date());
            query.setParameter("CONTACTO", "test");
            query.setParameter("ORIGEN", "test");
            query.setParameter("TIPO_NEGOCIACIÓN", "test");
            query.setParameter("DINSERT", new Date());
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {

        } finally {
            if (session != null) {
                session.close();
            }
            HibernateUtil.shutdown();
        }
        //Assert
    }
    @Test
    void UploadData() {
        // FtpService init
        String[][] dataMatrix = null;
        int batchSize = 50;
        boolean insertionResult = insertData(dataMatrix,batchSize);
    }
    private boolean insertData(String[][] dataMatrix, int batchSize) {
        String sql = "INSERT INTO RDP_TRAFICO_PARTICULARES_NSMIT\n" +
                "(ZONA, COD_CONCES, CONCESIONARIO, COD_EXPO, EXPOSICION, COD_VENDEDOR, VENDEDOR, MODELO, COD_VERSION, VERSION, FECHA_OFERTA, CONTACTO, ORIGEN, TIPO_NEGOCIACIÓN, DINSERT)\n"
                +
                "VALUES(:ZONA, :COD_CONCES, :CONCESIONARIO, :COD_EXPO, :EXPOSICION, :COD_VENDEDOR,:VENDEDOR, :MODELO, :COD_VERSION, :VERSION, :FECHA_OFERTA, :CONTACTO, :ORIGEN, :TIPO_NEGOCIACIÓN, :DINSERT)";

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            String[][] datos = Archive_to_Array_test(dataMatrix);
            List<String[][]> matrixList = splitMatrix(datos, batchSize);

            int currentMatrixNum = 0;
            for (String[][] matrix : matrixList) {
                currentMatrixNum++;
                log.info("current set num: " + currentMatrixNum + "/ " + matrixList.size());
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
                    query.setParameter("FECHA_OFERTA", StringToDate(matrix[row][10]));
                    query.setParameter("CONTACTO", matrix[row][11]);
                    query.setParameter("ORIGEN", matrix[row][12]);
                    query.setParameter("TIPO_NEGOCIACIÓN", matrix[row][13]);
                    query.setParameter("DINSERT", LocalDateTime.now());
                    query.executeUpdate();
                }
                log.info("insertion complete: " + currentMatrixNum + "/ " + matrixList.size());
            }
            transaction.commit();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (session != null) {
                session.close();
            }
            HibernateUtil.shutdown();
        }
    }
    @Test
    void splitMatrixTest() {
        //Arrange
        int maxSize = 2;
        String[][] matrixEntry = {{"a", "b", "c"}, {"d", "e", "f"}, {"g", "h", "i"}, {"j", "k", "l"}};

        //Act
        List<String[][]> result = splitMatrix(matrixEntry, maxSize);

        //Assert
        Assertions.assertEquals(result.size(), maxSize);

        for (String[][] matrix : result) {
            printCells(matrix);
            System.out.println(" ");
        }
    }

    private List<String[][]> splitMatrix(String[][] matrix, int maxSize) {
        List<String[][]> matrixList = new ArrayList<>();
        for (int i = 0; i < matrix.length; i += maxSize) {
            int max = Math.min(i + maxSize, matrix.length);
            String[][] subMatrix = Arrays.copyOfRange(matrix, i, max);
            matrixList.add(subMatrix);
        }
        return matrixList;
    }


    public Date StringToDate(String fechaString) {
        Date fecha = new Date();

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        try {
            fecha = formato.parse(fechaString);
        } catch (ParseException e) {
            // manejo de excepción
        }

        return fecha;
    }

    String matrixToString(String[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                sb.append(matrix[i][j]);
                if (j < matrix[i].length - 1) {
                    sb.append(",");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Test
    String[][] Archive_to_Array_test(String[][] datosArray) throws IOException {
        IFileService fileService = FtpService.builder().host("10.0.0.22").port(21).username("dmkt").password("zl<M(+Ek74").build();
        // read content as byte[]
        String datos = matrixToString(datosArray);
        byte[] contentByteArr = fileService.getFileContent(datos);
        InputStream inputStream = new ByteArrayInputStream(contentByteArr);
        // load it as Workbook
        Workbook workbook = ExcelUtils.initWorkbook(inputStream, ExcelType.XLSX);
        // Get Cell[][]
        Sheet sheet = workbook.getSheetAt(0);

        return readSheetContent(sheet, 10);
    }


    public String[][] readSheetContent(Sheet sheet, int rowIndex) {
        // Obtener el número total de filas y columnas en la hoja de cálculo
        int totalRows = sheet.getLastRowNum() + 1;
        int validRows = totalRows - rowIndex;
        int totalColumns = sheet.getRow(rowIndex).getLastCellNum();
        // Crear una matriz de Strings con el tamaño del número total de filas y columnas
        String[][] data = new String[validRows][totalColumns];
        // Recorrer todas las filas y columnas de la hoja de cálculo y agregar los datos a la matriz
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
        // Devolver la matriz de Strings
        return data;
    }

    private void printCells(String[][] cellsArr) {
        for (int x = 0; x < cellsArr.length; x++) {
            for (int y = 0; y < cellsArr[x].length; y++) {
                System.out.print(cellsArr[x][y]);
                System.out.print(", ");
            }
            System.out.println();
        }
    }

//    private void InsertionData(String[][] data) {
//        String sql = "INSERT INTO RDP_TRAFICO_PARTICULARES_NSMIT\n" +
//                "(ZONA, COD_CONCES, CONCESIONARIO, COD_EXPO, EXPOSICION, COD_VENDEDOR, VENDEDOR, MODELO, COD_VERSION, VERSION, FECHA_OFERTA, CONTACTO, ORIGEN, TIPO_NEGOCIACIÓN, DINSERT)\n"
//                +
//                "VALUES(:ZONA, :COD_CONCES, :CONCESIONARIO, :COD_EXPO, :EXPOSICION, :COD_VENDEDOR,:VENDEDOR, :MODELO, :COD_VERSION, :VERSION, :FECHA_OFERTA, :CONTACTO, :ORIGEN, :TIPO_NEGOCIACIÓN, :DINSERT)";
//
//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            Transaction transaction = session.beginTransaction();
//
//            String[][] dato = Archive_to_Array_test(data);
//
//            List<String[][]> matrixList = splitMatrix(dato, 100);
//
//            int currentMatrixNum = 0;
//            for (String[][] matrix : matrixList) {
//                currentMatrixNum++;
//                log.info("current set num: " + currentMatrixNum + "/ " + matrixList.size());
//                for (int row = 0; row < matrix.length; row++) {
//                    Query query = session.createSQLQuery(sql);
//                    query.setParameter("ZONA", matrix[row][0]);
//                    query.setParameter("COD_CONCES", matrix[row][1]);
//                    query.setParameter("CONCESIONARIO", matrix[row][2]);
//                    query.setParameter("COD_EXPO", matrix[row][3]);
//                    query.setParameter("EXPOSICION", matrix[row][4]);
//                    query.setParameter("COD_VENDEDOR", matrix[row][5]);
//                    query.setParameter("VENDEDOR", matrix[row][6]);
//                    query.setParameter("MODELO", matrix[row][7]);
//                    query.setParameter("COD_VERSION", matrix[row][8]);
//                    query.setParameter("VERSION", matrix[row][9]);
//                    query.setParameter("FECHA_OFERTA", StringToDate(matrix[row][10]));
//                    query.setParameter("CONTACTO", matrix[row][11]);
//                    query.setParameter("ORIGEN", matrix[row][12]);
//                    query.setParameter("TIPO_NEGOCIACIÓN", matrix[row][13]);
//                    query.setParameter("DINSERT", LocalDateTime.now());
//                    query.executeUpdate();
//                }
//                log.info("insertion complete: " + currentMatrixNum + "/ " + matrixList.size());
//            }
//            transaction.commit();
//        } catch (Exception e) {
//            System.out.println(e);
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//            HibernateUtil.shutdown();
//        }
//    }


}
