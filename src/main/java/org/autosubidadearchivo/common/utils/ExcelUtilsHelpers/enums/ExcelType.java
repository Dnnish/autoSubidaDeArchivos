package org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.enums;

public enum ExcelType {
    UNKNOWN(".unknown"),
    XLS(".xls"),
    XLSX(".xlsx"),
    CSV(".csv");

    private final String extension;

    private ExcelType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}

