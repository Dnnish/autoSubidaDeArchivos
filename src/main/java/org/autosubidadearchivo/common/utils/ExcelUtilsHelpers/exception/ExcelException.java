package org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.exception;

public class ExcelException extends RuntimeException{
    private static final long serialVersionUID = 12314234234234234L;
    private Integer code;
    public Integer getCode() {
        return this.code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }

    public ExcelException(ExcelStatus status){
        super(status.getDefaultMessage());
        this.code = status.getCode();
    }

    public ExcelException(ExcelStatus status, String message){
        super(message);
        this.code = status.getCode();
    }

    public ExcelException(String message){
        super(message);
        this.code = ExcelStatus.UNKNOWN.getCode();
    }
}
