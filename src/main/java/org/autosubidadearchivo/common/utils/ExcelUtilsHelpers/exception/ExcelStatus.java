package org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.exception;

import org.omg.CORBA.UNKNOWN;

public enum ExcelStatus {
    UNKNOWN(-1, "UNKNOWN"),
    WORKBOOK_INPUT_STREAM_NULL_ERROR(100, "InputStream is null"),
    EXCEL_TYPE_NOT_SUPPORTED_ERROR(101, "excel type not supported"),
    WORKBOOK_XLSX_ERROR(102, "workbook preparation error for .xlsx"),
    WORKBOOK_XLS_ERROR(103, "workbook preparation error for .xls"),
    ROW_START_INDEX_OUT_OF_RANGE(104, "row start index is out of range"),
    ROW_END_INDEX_OUT_OF_RANGE(105, "row end index is out of range"),
    ROW_INDEX_OUT_OF_RANGE(106, "row index is out of range"),
    COL_INDEX_OUT_OF_RANGE(107, "column index is out of range"),
    COL_START_INDEX_OUT_OF_RANGE(108, "column start index is out of range"),
    COL_END_INDEX_OUT_OF_RANGE(109, "column end index is out of range");


    private final Integer code;
    private final String defaultMessage;

    private ExcelStatus(Integer code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }
}
