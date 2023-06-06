
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.autosubidadearchivo.service.impl.FtpService;
import org.junit.jupiter.api.Disabled;

@Disabled
public class MainTest {
    private static final Logger log = LogManager.getLogger(FtpService.class);
//    @Test
//    void mainProcessTest() throws IOException {
//        //1. Init services {File}
//        com.patterson.autofileloader.service.IFileService iFileService = FileService.builder().host("10.0.0.22").port(21).username("dmkt").password("zl<M(+Ek74").build();
//        String dir = "prueba";
//        int batchSize = 100;
//
//        //2. File capture {Get files, get file content}
//        List<String> Filelist = iFileService.getFileList(dir);
//
//        //3. Data insertion {read file content and request sql insertion}
//
//        for(String File : Filelist) {
//            if (File.toLowerCase().endsWith(".xlsx")) {
//                boolean success = insertData(batchSize, File);
//                if (success) {
//                    log.info("Data successfully inserted into table RPD_TRAFICO_PARTICULARES_NSMIT.");
//                    //4. Post insertion process {sql request}
//                    boolean success2 = Insert_rdp();
//                    if (success2) {
//                        log.info("Data successfully inserted into table RPD_TRAFICO_PARTICULARES_NSMIT.");
//                    } else {
//                        log.error("There was an error inserting the data into the table RPD_TRAFICO_PARTICULARES_NSMIT.");
//                    }
//                } else {
//                    log.error("There was an error inserting the data into the table RPD_TRAFICO_PARTICULARES_NSMIT.");
//                }
//            }
//        }
//        //5. Close
//        HibernateUtil.shutdown();
//    }

//    public static boolean insertData(int batchSize, String filePath) throws IOException {
//        com.patterson.autofileloader.service.IFileService iFileService = FileService.builder().host("10.0.0.22").port(21).username("dmkt").password("zl<M(+Ek74").build();
//        String sql = "INSERT INTO RDP_TRAFICO_PARTICULARES_NSMIT\n" +
//                "(ZONA, COD_CONCES, CONCESIONARIO, COD_EXPO, EXPOSICION, COD_VENDEDOR, VENDEDOR, MODELO, COD_VERSION, VERSION, FECHA_OFERTA, CONTACTO, ORIGEN, TIPO_NEGOCIACIÓN, DINSERT)\n"
//                +
//                "VALUES(:ZONA, :COD_CONCES, :CONCESIONARIO, :COD_EXPO, :EXPOSICION, :COD_VENDEDOR,:VENDEDOR, :MODELO, :COD_VERSION, :VERSION, :FECHA_OFERTA, :CONTACTO, :ORIGEN, :TIPO_NEGOCIACIÓN, :DINSERT)";
//        String[][] dataMatrix = iFileService.getFileContentAsMatrix(filePath);
//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            Transaction transaction = session.beginTransaction();
//
//            List<String[][]> matrixList = GenericUtils.splitMatrix(dataMatrix, batchSize);
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
//                    query.setParameter("FECHA_OFERTA", GenericUtils.toDate(matrix[row][10],"dd-MM-yyyy"));
//                    query.setParameter("CONTACTO", matrix[row][11]);
//                    query.setParameter("ORIGEN", matrix[row][12]);
//                    query.setParameter("TIPO_NEGOCIACIÓN", matrix[row][13]);
//                    query.setParameter("DINSERT", LocalDateTime.now());
//                    query.executeUpdate();
//                }
//                log.info("insertion complete: " + currentMatrixNum + "/ " + matrixList.size());
//            }
//
//            transaction.commit();
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//        return true;
//    }
//
//    public static Boolean Insert_rdp() {
//        String sql = "INSERT INTO rdp_trafico_particulares\n" +
//                "            (codigo_concesionario, modelo_oferta, version_oferta,\n" +
//                "             fecha_oferta, total_trafico)\n" +
//                "   (SELECT   te.cod_conces, te.modelo, te.VERSION, te.fecha_oferta, COUNT (*)\n" +
//                "        FROM rdp_trafico_particulares_nsmit te\n" +
//                "       WHERE TRUNC (dinsert) = TRUNC (SYSDATE)\n" +
//                "    GROUP BY te.cod_conces, te.modelo, te.VERSION, te.fecha_oferta)";
//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            Transaction transaction = session.beginTransaction();
//            Query query = session.createSQLQuery(sql);
//            query.executeUpdate();
//            transaction.commit();
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//        return true;
//    }
}
