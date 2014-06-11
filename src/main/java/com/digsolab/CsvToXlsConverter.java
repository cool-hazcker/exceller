package com.digsolab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

public class CsvToXlsConverter {

    private static final String EXCEL_EXTENSION = ".xlsx";
    private static final String DATE_JAVA_PATTERN = "yyyy-mm-dd hh:mm:ss";
    private static final int rowAccessWindowSize = 100;
    private static final Logger log = Logger.getLogger(CsvToXlsConverter.class);
    private ConverterOptions options = null;
    private ICsvListReader listReader = null;
    private SXSSFWorkbook wb = null;
    private ArrayList<CellStyle> cellStyles = null;
    private int colCount;

    public void convertToExcel(ConverterOptions options) throws ParseException, NumberFormatException,
            IOException {
        this.options = options;
        String strDestination = options.getDestination();
        String[] sourceFiles = options.getSource();
        initLogger();
        convertToXls(sourceFiles, strDestination);
    }

    private void convertToXls(String[] sourceFiles, String destination)
            throws ParseException, NumberFormatException, IOException {
        try {
            log.info("Starting conversion");
            log.info("Converting csv to excel...");
            convertToXls(sourceFiles);
            log.info("Conversion complete. Starting saving process");
            saveXls(destination != null ? destination : createResultFileName(sourceFiles[0]));
        }
        finally {
            if (wb != null) {
                wb.dispose();
            }
            if (listReader != null) {
                listReader.close();
            }
        }
        log.info("Successfully saved");
    }

    private String createResultFileName(String strSource) {
        File source = new File(strSource);
        String fileName = source.getName();
        return "./" + fileName.substring(0, fileName.lastIndexOf('.')) + EXCEL_EXTENSION;
    }

    private void initLogger() {
        Properties log4jProperties = new Properties();
        log4jProperties.setProperty("log4j.rootLogger", "INFO, myConsoleAppender");
        log4jProperties.setProperty("log4j.appender.myConsoleAppender", "org.apache.log4j.ConsoleAppender");
        log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout", "org.apache.log4j.PatternLayout");
        log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout.ConversionPattern", "%m%n");
        PropertyConfigurator.configure(log4jProperties);
    }

    private void openCSV(String strSource) throws IOException {
        File source = new File(strSource);
        if (!source.exists()) {
            throw new IllegalArgumentException("The source for the"
                    + ".csv file(s) cannot be found");
        }
        if (source.isDirectory()) {
            throw new IllegalArgumentException("The source path"
                    + " is a directory");
        }
        listReader = new CsvListReader(new FileReader(source), CsvPreference.STANDARD_PREFERENCE);
    }

    private void convertToXls(String[] sourceFiles) throws ParseException,
            NumberFormatException, IOException {
        wb = new SXSSFWorkbook(rowAccessWindowSize);
        String[] headers;
        for (String strSource : sourceFiles) {
            int rowIndex = 1;
            try {
                openCSV(strSource);
                Sheet sh = wb.createSheet();
                List<String> fieldsList;
                fieldsList = listReader.read();
                colCount = (options.getColCount() == 0) ? fieldsList.size()
                        : Math.min(fieldsList.size(), options.getColCount());
                cellStyles = getCellStyles();
                if ((headers = options.getHeaders()) != null) {
                    headers = extendWithDefaultHeaders(headers);
                }
                else if (options.shouldReadHeaderString()) {
                    headers = fieldsList.toArray(new String[colCount]);
                }
                else {
                    headers = extendWithDefaultHeaders(new String[] {});
                }
                createHeaders(sh, headers);
                if (!options.shouldReadHeaderString()) {
                    convertToXlsRow(sh, rowIndex++, fieldsList);
                }
                while ((fieldsList = listReader.read()) != null) {
                    convertToXlsRow(sh, rowIndex++, fieldsList);
                }
                applyColumnWidths(sh);
                applyAutoFilters(sh, rowIndex - 1);
            }
            catch (ParseException pEX) {
                throw new ParseException(String.format("Error while parsing Date field in row: %s", rowIndex), pEX.getErrorOffset());
            }
            catch (NumberFormatException nEX) {
                throw new NumberFormatException(String.format("Error while parsing Number field in row: %s", rowIndex));
            }
        }
    }

    private void applyColumnWidths(Sheet sh) {
        int currentWidth;
        for (int column = 0; column < colCount; column++) {
            Format cellFormat = options.getFormat(column);
            currentWidth = cellFormat.getWidth();
            if (currentWidth != -1) {
                sh.setColumnWidth(column, currentWidth);
            }
            else {
                sh.autoSizeColumn(column);
            }
        }
    }

    private void applyAutoFilters(Sheet sh, int lastRow) {
        CellRangeAddress cr = new CellRangeAddress(0, lastRow, 0, colCount - 1);
        sh.setAutoFilter(cr);
    }

    private void createHeaders(Sheet sh, String[] headers) {
        Row row = sh.createRow(0);
        Cell cell;
        XSSFCellStyle headerStyle = (XSSFCellStyle)wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerStyle.setFont(font);
        for (int cellnum = 0; cellnum < this.colCount; cellnum++) {
            cell = row.createCell(cellnum);
            cell.setCellValue(headers[cellnum]);
            cell.setCellStyle(headerStyle);
        }
    }

    private String[] extendWithDefaultHeaders(String[] headers) {
        String[] result = new String[this.colCount];
        int len = Math.min(this.colCount, headers.length);
        System.arraycopy(headers, 0, result, 0, len);
        for (int i = headers.length; i < colCount;i++) {
            result[i] = String.format("Column%d", i);
        }
        return result;
    }

    private void convertToXlsRow(Sheet sh, int rowIndex, List<String> csvRow) throws ParseException,
            NumberFormatException {
        Row row = sh.createRow(rowIndex);
        Cell cell;
        for (int cellnum = 0; cellnum < this.colCount; cellnum++) {
            cell = row.createCell(cellnum);
            applyFormatting(cell, (csvRow.get(cellnum) != null) ? csvRow.get(cellnum) : "", cellnum);
        }
    }

    private XSSFCellStyle getHyperlinkStyle () {
        XSSFCellStyle hlinkStyle = (XSSFCellStyle)wb.createCellStyle();
        XSSFFont hlink_font = (XSSFFont)wb.createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
        hlinkStyle.setFont(hlink_font);
        return hlinkStyle;
    }

    private ArrayList<CellStyle> getCellStyles() {
        ArrayList<CellStyle> styles = new ArrayList<>();
        for (int i = 0; i < this.colCount; i++) {
            Format cellFormat = options.getFormat(i);
            XSSFCellStyle cellStyle;
            if (cellFormat.getType() == Type.HYPERLINK) {
                cellStyle = getHyperlinkStyle();
            }
            else {
                 cellStyle = (XSSFCellStyle)wb.createCellStyle();
            }
            String mask = cellFormat.getMask();
            if (mask != null) {
                cellStyle.setDataFormat(
                        wb.getCreationHelper().createDataFormat().getFormat(mask));
            }
            cellStyle.setWrapText(true);
            styles.add(cellStyle);
        }
        return styles;
    }

    private void applyFormatting(Cell cell, String data, int cellIndex) throws ParseException,
            NumberFormatException {
        setCellValue(cell, data, cellIndex);
        setCellStyle(cell, cellIndex);
    }

    private void setCellValue(Cell cell, String data, int cellIndex) throws ParseException,
            NumberFormatException {
        Format cellFormat = options.getFormat(cellIndex);
        Type cellType = cellFormat.getType();
        try {
            switch (cellType) {
                case BOOLEAN:
                    boolean boolValue = Boolean.parseBoolean(data);
                    cell.setCellValue(boolValue);
                    break;
                case DATE:
                    Date dateValue = (new SimpleDateFormat(DATE_JAVA_PATTERN)).parse(data);
                    cell.setCellValue(dateValue);
                    break;
                case HYPERLINK:
                    cell.setCellValue(data);
                    Hyperlink link = wb.getCreationHelper().createHyperlink(Hyperlink.LINK_URL);
                    cell.setHyperlink(link);
                    link.setAddress(data);
                    break;
                case NUMBER:
                    double numValue = Double.parseDouble(data);
                    cell.setCellValue(numValue);
                    break;
                case TEXT:
                    cell.setCellValue(data);
                    break;
                default: break;
            }
        }
        catch (ParseException pEx) {
            cell.setCellValue(data);
        }
        catch (NumberFormatException nEx) {
            cell.setCellValue(data);
        }
    }

    private void setCellStyle(Cell cell, int cellIndex) {
        cell.setCellStyle(cellStyles.get(cellIndex));
    }

    private void saveXls(String destination) throws IOException {
        FileOutputStream fout = null;
        try {
            if (new File(destination).getParentFile() == null) {
                destination = "./" + destination;
            }
            File outFile = new File (destination);
            if (!outFile.exists()) {
                File parent = outFile.getParentFile();
                parent.mkdirs();
                outFile.createNewFile();
            }
            fout = new FileOutputStream(new File (destination));
            log.info("Saving Excel file...");
            wb.write(fout);
        }
        finally {
            if (fout != null) {
                fout.close();
            }
        }
    }
}
