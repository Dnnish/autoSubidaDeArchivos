package org.autosubidadearchivo.service.impl;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.autosubidadearchivo.service.IFileService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


/**
 * FtpService.
 *
 * @Description FtpService
 * @Date 9/5/23 16:19
 * @Created by qinxiuwang
 */
@Builder
@Data
public class FtpService implements IFileService {

    private static final Logger log = LogManager.getLogger(FtpService.class);
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    /**
     * Retrieves a list of file names from the specified directory on the FTP server
     * @param dir The directory path.
     * @return A list of file names in the directory.
     * @throws IOException If an error occurs while retrieving the file list.
     */
    @Override
    public List<String> getFileList(String dir) throws IOException {
        FTPClient ftpClient = getConnection();
        List<String> listFileNames = null;
        try {
            String[] files = ftpClient.listNames(dir);
            listFileNames = Arrays.asList(files);

        } catch (IOException e) {
            log.error("Get fileList error: " + e);
        }
        return listFileNames;
    }

    /**
     * Retrieves the content of a file from the FTP server.
     * @param filename The name of the file to retrieve.
     * @return The content of the file as a byte array.
     * @throws IOException If an error occurs while retrieving the file content.
     */
    @Override
    public byte[] getFileContent(String filename) throws IOException {
        FTPClient ftpClient = getConnection();

        InputStream inputStream = ftpClient.retrieveFileStream(filename);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        byte[] fileContent = outputStream.toByteArray();

        outputStream.close();
        inputStream.close();
        disconnectFromFtpServer(ftpClient);

        return fileContent;
    }

    /**
     * Uploads a file to the specified directory on the FTP server.
     * @param dir The directory on the FTP server to upload the file to.
     * @param filename The name of the file to be uploaded.
     * @param fileContent The content of the file as a byte array.
     * @return True if the file was successfully uploaded, false otherwise.
     */
    @Override
    public Boolean uploadFile(String dir, String filename, byte[] fileContent) {
        try {
            //Arrange
            FTPClient ftpClient = getConnection();

            //Act
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
            ftpClient.storeFile(dir + filename, inputStream);

            disconnectFromFtpServer(ftpClient);
        } catch (IOException e) {
            log.error("error trying to insert, Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Uploads a file to the specified directory on the FTP server.
     * @param dir The directory on the FTP server to upload the file to.
     * @param filename The name of the file to be uploaded.
     * @param fileContent The content of the file as a byte array.
     * @param isBinary Indicates whether the file should be uploaded in binary mode.
     * @return True if the file was successfully uploaded, false otherwise.
     */
    @Override
    public Boolean uploadFile(String dir, String filename, byte[] fileContent,boolean isBinary) {
        try {
            //Arrange
            FTPClient ftpClient = getConnection();

            //Act
            if (isBinary){
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
            ftpClient.storeFile(dir + filename, inputStream);

            disconnectFromFtpServer(ftpClient);
        } catch (IOException e) {
            log.error("error trying to insert, Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Deletes a file from the FTP server.
     * @param relativePath The relative path of the file to be deleted.
     * @return {@code true} if the file is successfully deleted, {@code false} otherwise.
     * @throws IOException If an error occurs while deleting the file.
     */
    @Override
    public Boolean deleteFile(String relativePath) throws IOException {
        FTPClient client = getConnection();
        try {
            client.deleteFile(relativePath);
            return true;
        } catch (IOException e) {
            log.error("This file not found, Error: " + e.getMessage());
            return false;
        }
    }

    //region Functions
    //region Connection

    /**
     * Establishes a connection to the FTP server using the provided FTP service configuration.
     *
     * @return An FTPClient object representing the connection to the FTP server.
     * @throws IOException If an I/O error occurs during the connection.
     */
    private FTPClient getConnection() throws IOException {
        // crear un objeto cliente
        FtpService ftpService = FtpService.builder().host("10.0.0.22").port(21).username("dmkt").password("zl<M(+Ek74").build();

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpService.host, ftpService.port);
        ftpClient.login(ftpService.username, ftpService.password);

        // config settings
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
        ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();

        return ftpClient;
    }

    /**
     * Disconnects from the FTP server by logging out and closing the connection.
     *
     * @param ftpClient The FTPClient object representing the connection to the FTP server.
     * @throws IOException If an I/O error occurs during the disconnection.
     */
    public void disconnectFromFtpServer(FTPClient ftpClient) throws IOException {
        //cerrar sesion y conexion
        ftpClient.logout();
        ftpClient.disconnect();
    }
    //endregion
    //region helps in programming
    private void printCells(String[][] cellsArr) {
        for (int x = 0; x < cellsArr.length; x++) {
            for (int y = 0; y < cellsArr[x].length; y++) {
                System.out.print(cellsArr[x][y]);
                System.out.print(", ");
            }
            System.out.println();
        }
    }
    //endregion
    //endregion


}
