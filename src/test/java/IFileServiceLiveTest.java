
import org.autosubidadearchivo.service.impl.FtpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Disabled
public class IFileServiceLiveTest {



    @Test
    public void getFileList_test() throws IOException {
        //Arrange
        List<String> expectedResult = Arrays.asList("prueba/pruebaExcel.xlsx","prueba/test.txt", "prueba/test2.txt");
        FtpService ftpService = FtpService.builder().host("10.0.0.22").port(21).username("dmkt").password("zl<M(+Ek74").build();
        String relativePath = "prueba";

        //Act
        List<String> resultList = ftpService.getFileList(relativePath);

        //Assert
        Assertions.assertEquals(expectedResult,resultList);
    }

    @Test
    public void uploadFile() throws IOException{
        //Arrange
        boolean expectedResult = true;
        FtpService ftpService = FtpService.builder().host("10.0.0.22").port(21).username("dmkt").password("zl<M(+Ek74").build();
        byte[] content = "hola mundo".getBytes();
        String dir = "prueba/";
        String filename = "test.txt";

        //Act
        Boolean resultBoolean = ftpService.uploadFile(dir, filename, content);
        //Assert
        Assertions.assertEquals(expectedResult, resultBoolean);
    }

    @Test
    public void deleteFile()throws IOException{
        //Arrange
       boolean expectedResult = true;
       FtpService ftpService = FtpService.builder().host("10.0.0.22").port(21).username("dmkt").password("zl<M(+Ek74").build();
       String dir = "prueba/";
       String filename = "test.txt";
       String relativePath = dir+ filename;
       //Act
       Boolean resultBoolean = ftpService.deleteFile(relativePath);
       //Assert
       Assertions.assertEquals(expectedResult,resultBoolean);
   }

}
