package org.autosubidadearchivo.common.utils;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.exception.ExcelException;
import org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.exception.ExcelStatus;
import org.autosubidadearchivo.common.utils.ExcelUtilsHelpers.enums.ExcelType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class ExcelUtils {

    private static final int DEFAULT_SHEET_INDEX = 0;
    private static final int DEFAULT_START_ROW_INDEX = 0;
    private static final int DEFAULT_START_COLUMN_INDEX = 0;
    private static final ExcelType DEFAULT_EXCEL_TYPE;

    public ExcelUtils() {
    }

    public static Workbook initWorkbook(InputStream inputStream) {
        return initWorkbook(inputStream, DEFAULT_EXCEL_TYPE);
    }

    public static Workbook initWorkbook(InputStream inputStream, ExcelType type) {
        if (inputStream == null) {
            throw new ExcelException(ExcelStatus.WORKBOOK_INPUT_STREAM_NULL_ERROR);
        } else {
            switch (type) {
                case XLS:
                    try {
                        return new HSSFWorkbook(inputStream);
                    } catch (IOException var4) {
                        throw new ExcelException(ExcelStatus.WORKBOOK_XLS_ERROR);
                    }
                case XLSX:
                    try {
                        return new XSSFWorkbook(inputStream);
                    } catch (IOException var3) {
                        throw new ExcelException(ExcelStatus.WORKBOOK_XLSX_ERROR);
                    }
                case CSV:
                    throw new NotImplementedFunctionException("CSV not supported");
                default:
                    throw new ExcelException(ExcelStatus.EXCEL_TYPE_NOT_SUPPORTED_ERROR);
            }
        }
    }

    public static Sheet getDefaultSheet(Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    public static String[] getHeaders(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        return getHeaders(headerRow);
    }

    public static String[] getHeaders(Row titleRow) {
        List<String> resultList = new ArrayList<>();
        Iterator<Cell> cellIt = titleRow.cellIterator();

        while (cellIt.hasNext()) {
            Cell currentCell = (Cell) cellIt.next();
            String cellStringValue = currentCell.getStringCellValue();
            resultList.add(cellStringValue);
        }
        return (String[]) resultList.toArray(new String[0]);
    }

    public static boolean validateHeaders(Sheet sheet, String[] sheetHeaders) {
        Row defaultTitleRow = sheet.getRow(0);
        return validateHeaders(defaultTitleRow, sheetHeaders);
    }

    public static boolean validateHeaders(Row titleRow, String[] sheetHeaders) {
        boolean areEquals = true;
        HashMap<Integer, Cell> cellHashMap = new HashMap();
        Iterator<Cell> cellIt = titleRow.cellIterator();

        for (int counter = 0; cellIt.hasNext(); ++counter) {
            Cell currentCell = (Cell) cellIt.next();
            cellHashMap.put(counter, currentCell);
        }

        for (int idx = 0; idx < sheetHeaders.length; ++idx) {
            String cellText;
            try {
                cellText = ((Cell) cellHashMap.get(idx)).getStringCellValue();
            } catch (Exception var9) {
                return false;
            }

            if (!sheetHeaders[idx].equals(cellText)) {
                areEquals = false;
                break;
            }
        }
        return areEquals;
    }

    public static List<Row> getContentRows(Sheet sheet) {
        return getContentRows(sheet, sheet.getFirstRowNum() + 1, sheet.getLastRowNum());
    }

    public static List<Row> getContentRows(Sheet sheet, int statIndex) {
        return getContentRows(sheet, statIndex, sheet.getLastRowNum());
    }

    public static List<Row> getContentRows(Sheet sheet, int startIndex, int endIndex) {
        if (startIndex <= sheet.getLastRowNum() && startIndex >= sheet.getFirstRowNum()) {
            if (endIndex <= sheet.getLastRowNum() && endIndex >= sheet.getFirstRowNum()) {
                List<Row> resultList = new ArrayList();
                IntStream.rangeClosed(startIndex, endIndex).forEach((x) -> {
                    resultList.add(sheet.getRow(x));
                });
                return resultList;
            } else {
                throw new ExcelException(ExcelStatus.ROW_END_INDEX_OUT_OF_RANGE);
            }
        } else {
            throw new ExcelException(ExcelStatus.ROW_START_INDEX_OUT_OF_RANGE);
        }
    }

    public static Cell[] getRowCells(Sheet sheet, int rowIndex) {
        if (rowIndex <= sheet.getLastRowNum() && rowIndex >= sheet.getFirstRowNum()) {
            Row row = sheet.getRow(rowIndex);
            return getCells(row);
        } else {
            throw new ExcelException(ExcelStatus.ROW_INDEX_OUT_OF_RANGE);
        }
    }

    public static Cell[] getCells(Row row) {
        int size = row.getPhysicalNumberOfCells();
        Cell[] resultArray = new Cell[size];
        IntStream.rangeClosed(row.getFirstCellNum(), size - 1).forEach((x) -> {
            resultArray[x] = row.getCell(x);
        });
        return resultArray;
    }

    public static Cell[] getColumnCells(Sheet sheet, int colIndex) {
        int startRowIndex = sheet.getFirstRowNum() + 1;
        int endRowIndex = sheet.getLastRowNum();
        return getColumnCells(sheet, colIndex, startRowIndex, endRowIndex);
    }

    public static Cell[] getColumnCells(Sheet sheet, int colIndex, int startRowIndex) {
        int endRowIndex = sheet.getLastRowNum();
        return getColumnCells(sheet, colIndex, startRowIndex, endRowIndex);
    }

    public static Cell[] getColumnCells(Sheet sheet, int colIndex, int startRowIndex, int endRowIndex) {
        int cellSize = sheet.getRow(sheet.getFirstRowNum()).getPhysicalNumberOfCells();
        if (colIndex <= cellSize && colIndex >= 0) {
            if (startRowIndex <= sheet.getLastRowNum() && startRowIndex >= sheet.getFirstRowNum()) {
                if (endRowIndex <= sheet.getLastRowNum() && endRowIndex >= sheet.getFirstRowNum()) {
                    List<Cell> resulCell = new ArrayList<>();
                    List<Row> rowList = getContentRows(sheet, startRowIndex, endRowIndex);
                    rowList.forEach((row) -> {
                        resulCell.add(row.getCell(colIndex));
                    });
                    return (Cell[]) resulCell.toArray(new Cell[0]);
                } else {
                    throw new ExcelException(ExcelStatus.ROW_END_INDEX_OUT_OF_RANGE);
                }
            } else {
                throw new ExcelException(ExcelStatus.ROW_START_INDEX_OUT_OF_RANGE);
            }
        } else {
            throw new ExcelException(ExcelStatus.COL_INDEX_OUT_OF_RANGE);
        }
    }

    public static Cell[][] getContentCellMatrix(Sheet sheet) {
        List<Row> rowList = getContentRows(sheet);
        Row firstRow = (Row) rowList.stream().findFirst().orElseThrow(() -> {
            return new ExcelException("no row found");
        });
        int contentRowsSize = rowList.size();
        int columnSize = getCells(firstRow).length;
        return getContentCellMatrix(sheet, 0, columnSize - 1, contentRowsSize);
    }

    public static Cell[][] getContentCellMatrix(Sheet sheet, int startColIndex, int endColIndex, int endRowIndex) {
        return getCellMatrix(sheet, startColIndex, endColIndex, 1, endRowIndex);
    }

    public static Cell[][] getCellMatrix(Sheet sheet, int startColIndex, int endColIndex, int startRowIndex, int endRowIndex) {
        int cellSize = sheet.getRow(sheet.getFirstRowNum()).getPhysicalNumberOfCells();
        if (startColIndex <= cellSize && startColIndex >= 0) {
            if (endColIndex <= cellSize && endColIndex >= 0) {
                if (startRowIndex <= sheet.getLastRowNum() && startRowIndex >= sheet.getFirstRowNum()) {
                    if (endRowIndex <= sheet.getLastRowNum() && endRowIndex >= sheet.getFirstRowNum()) {
                        int rowSize = endRowIndex - startRowIndex + 1;
                        int columnSize = endColIndex - startColIndex + 1;
                        Cell[][] matrix = new Cell[rowSize][columnSize];
                        List<Row> rowList = getContentRows(sheet, startRowIndex, endRowIndex);

                        for (int rowIdx = 0; rowIdx < rowSize; ++rowIdx) {
                            Row row = (Row) rowList.get(rowIdx);
                            int y = 0;

                            for (int colIdx = startColIndex; colIdx <= endColIndex; ++colIdx) {
                                matrix[rowIdx][y] = row.getCell(colIdx);
                                ++y;
                            }
                        }

                        return matrix;
                    } else {
                        throw new ExcelException(ExcelStatus.ROW_END_INDEX_OUT_OF_RANGE);
                    }
                } else {
                    throw new ExcelException(ExcelStatus.ROW_START_INDEX_OUT_OF_RANGE);
                }
            } else {
                throw new ExcelException(ExcelStatus.COL_END_INDEX_OUT_OF_RANGE);
            }
        } else {
            throw new ExcelException(ExcelStatus.COL_START_INDEX_OUT_OF_RANGE);
        }
    }

    public static byte[] createSimpleExcel(ExcelType type, String[] headers, String[][] contentArray) throws IOException {
        return createSimpleExcel(type, headers, contentArray, true);
    }

    public static byte[] createSimpleExcel(ExcelType type, String[] headers, String[][] contentArray, boolean withBOM) throws IOException {
        if (headers != null && contentArray != null) {
            byte[] excelInByte;
            switch (type) {
                case XLS:
                    Workbook xlsWorkbook = new HSSFWorkbook();
                    fillSimpleWorkBook(xlsWorkbook, headers, contentArray);
                    excelInByte = toByteArray(xlsWorkbook);
                    break;
                case XLSX:
                    Workbook xlsxWorkbook = new XSSFWorkbook();
                    fillSimpleWorkBook(xlsxWorkbook, headers, contentArray);
                    excelInByte = toByteArray(xlsxWorkbook);
                    break;
                case CSV:
                    byte[] excelArray = buildSimpleCsvExcel(headers, contentArray).getBytes(StandardCharsets.UTF_8);
                    if (!withBOM) {
                        excelInByte = excelArray;
                    } else {
                        byte[] bomArray = new byte[]{-17, -69, -65};
                        ByteBuffer buffer = ByteBuffer.allocate(bomArray.length + excelArray.length);
                        buffer.put(bomArray);
                        buffer.put(excelArray);
                        excelInByte = buffer.array();
                    }
                    break;
                default:
                    throw new ExcelException(ExcelStatus.EXCEL_TYPE_NOT_SUPPORTED_ERROR);
            }

            return excelInByte;
        } else {
            throw new NullArgumentException();
        }
    }

    private static void fillSimpleWorkBook(Workbook workbook, String[] headers, String[][] contentArray) {
        Sheet sheet = workbook.createSheet();
        Row headerRow = sheet.createRow(0);
        int idx = 0;
        String[] var6 = headers;
        int var7 = headers.length;

        int colIdx;
        for(colIdx = 0; colIdx < var7; ++colIdx) {
            String header = var6[colIdx];
            Cell tempCell = headerRow.createCell(idx);
            tempCell.setCellValue(header);
            ++idx;
        }

        for(int rowIdx = 1; rowIdx < contentArray.length + 1; ++rowIdx) {
            Row currentRow = sheet.createRow(rowIdx);

            for(colIdx = 0; colIdx < contentArray[rowIdx - 1].length; ++colIdx) {
                Cell currentCell = currentRow.createCell(colIdx);
                currentCell.setCellValue(contentArray[rowIdx - 1][colIdx]);
            }
        }

    }

    private static String buildSimpleCsvExcel(String[] headers, String[][] contentArray) {
        StringBuilder csvBuilder = new StringBuilder();

        for(int i = 0; i < headers.length; ++i){
            csvBuilder.append(headers[i]);
            if (i < headers.length - 1){
                csvBuilder.append(",");
            }else {
                csvBuilder.append("\r\n");
            }
        }

        String[][] var8 = contentArray;
        int var4 = contentArray.length;

        for(int var5 = 0; var5 < var4; ++var5){
            String[] content = var8[var5];

            for(int col = 0; col < content.length; ++col){
                csvBuilder.append(content[col]);
                if (col < content.length - 1){
                    csvBuilder.append(";");
                }else {
                    csvBuilder.append("\r\n");
                }
            }
        }
        return csvBuilder.toString();
    }

    private static byte[] toByteArray(Workbook workbook) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] resultByteArray = bos.toByteArray();
        IOUtils.closeQuietly(bos);
        return resultByteArray;
    }

    public static String toCsvString(String number) {
        return "\"=\"\"" + number + "\"\"\"";
    }

    static {
        DEFAULT_EXCEL_TYPE = ExcelType.XLSX;
    }

}
