package org.autosubidadearchivo.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class GenericUtils {
    /**
     * Splits a matrix into multiple sub-matrices of a specified maximum size.
     * @param matrix The original matrix to be split.
     * @param maxSize The maximum size of each sub-matrix.
     * @return A list of sub-matrices.
     */
    public static List<String[][]> splitMatrix(String[][] matrix, int maxSize) {
        List<String[][]> matrixList = new ArrayList<>();
        for (int i = 0; i < matrix.length; i += maxSize) {
            int max = Math.min(i + maxSize, matrix.length);
            String[][] subMatrix = Arrays.copyOfRange(matrix, i, max);
            matrixList.add(subMatrix);
        }
        return matrixList;
    }

    /**
     * Converts a date string to a Date object using the default date format "yyyy-MM-dd".
     * @param fechaString The date string to be converted.
     * @return The Date object representing the converted date.
     */
    public static Date toDate(String fechaString) {
        return toDate(fechaString, "yyyy-MM-dd");
    }

    /**
     * Converts a date string to a Date object using the specified date format.
     * @param fechaString The date string to be converted.
     * @param format The format of the date string.
     * @return The Date object representing the converted date.
     */
    public static Date toDate(String fechaString, String format) {
        Date fecha = new Date();
        SimpleDateFormat formato = new SimpleDateFormat(format);
        try {
            fecha = formato.parse(fechaString);
        } catch (ParseException e) {
            // manejo de excepción
        }
        return fecha;
    }

    /**
     * Converts a 2 bi-dimensional matrix of strings to a string representation.
     * @param matrix The matrix to be converted.
     * @return The string representation of the matrix.
     */
    public static String matrixToString(String[][] matrix) {
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

    /**
     * Print String bi-array content.
     * @param cellsArr bi-array of {@code String}
     */
    public static void printCells(String[][] cellsArr) {
        for (int x = 0; x < cellsArr.length; x++) {
            for (int y = 0; y < cellsArr[x].length; y++) {
                System.out.print(cellsArr[x][y]);
                System.out.print(", ");
            }
            System.out.println();
        }
    }

    /**
     * Checks if a string value is blank.
     * @param value The string value to check.
     * @return {@code true} if the value is null or consists of whitespace only, {@code false} otherwise.
     */
    public static boolean isBlank(String value) {
        return value == null || " ".equals(value);
    }

    /**
     * Extracts the filename from a given path.
     * @param path The path from which to extract the filename.
     * @return The extracted filename as a string, or {@code null} if the path is blank.
     */
    public static String extractFilenameFromPath(String path) {
        if (isBlank(path)) {
            return null;
        }
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

}
