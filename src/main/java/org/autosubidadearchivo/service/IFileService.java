package org.autosubidadearchivo.service;

import java.io.IOException;
import java.util.List;

public interface IFileService {
    List<String> getFileList(String dir) throws IOException;

    // recoge un byte[] y lo convierte en String[][]
    Boolean uploadFile(String dir, String filename, byte[] fileContent);

    Boolean uploadFile(String dir, String filename, byte[] fileContent,boolean isBinary);

    Boolean deleteFile(String relativePath) throws IOException;
    // recoge un file y convierte su contenido en byte[]
    byte[] getFileContent(String filePath) throws IOException;
}
