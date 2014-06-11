package com.digsolab;


public class Exceller {

    public static void main(String[] args) {
        try {
            CsvToXlsConverter converter = new CsvToXlsConverter();
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            converter.convertToExcel(co);
        }
        catch (Exception ex) {
            System.out.printf("Error while performing conversion: %s\n", ex.getMessage());
        }
        catch (Throwable error) {
            System.out.printf("Fatal error while performing conversion: %s\n", error.getMessage() + error.toString());
            System.exit(1);
        }
    }

}
