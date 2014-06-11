package com.digsolab;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.ParseException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.JCommander;

public class ConverterOptions {

    @Parameters(commandDescription = "A csv to excel conversion tool")
    private class JCommanderParams {

        @Parameter(description = "Input csv files (separated by semicolon) and destination folder (optional)")
        public List<String> files = new ArrayList<>();

        @Parameter(names = "-h", description = "String containing header names separated by semicolon")
        public String headerNames;

        @Parameter(names = "-H", description = "Read headers from csv")
        public boolean shouldReadHeaderString;

        @Parameter(names = "-columns", description = "String containing column patterns separated by semicolon")
        public String columnPatterns;

        @Parameter(names = "-il", description = "Iterate last pattern. Apply last column pattern " +
                "to the rest of the columns in the table")
        public boolean shouldRepeatLastFormat = false;

        @Parameter(names = "-n", description = "Perform a slice. Retrieve only n columns from csv")
        public int colCount;

        @Parameter(names = "-output", description = "Output path")
        public String outPath;

        @Parameter(names = "-help", help = true, hidden = true)
        public boolean help;

    }

    private JCommanderParams jcp = null;
    private static final String COLUMNS_DELIMETER = ";";
    private static final String FORMAT_STRING_PATTERN = "^([BDHNT])([^W]*)(W([\\d]+))?$";

    private Format[] formats = null;
    private String[] sourceFiles = null;
    private Pattern formatPattern = null;

    private void getCLIArgs(String[] args) throws ParseException {
        jcp = new JCommanderParams();
        JCommander jcommander = new JCommander(jcp, args);
        if (jcp.help) {
            System.out.printf("A csv to excel conversion tool\n\n");
            jcommander.usage();
            System.exit(0);
        }
    }

    private void parseConverterOptions() {
        parseFilenames();
        parseFormats();
    }

    private void parseFilenames() {
        List<String> args = jcp.files;
        if (args.size() > 0) {
            this.sourceFiles = args.get(0).split(COLUMNS_DELIMETER);
        }
        else {
            throw new IllegalArgumentException("You haven't provided the CSV source");
        }
    }

    private void parseFormats()  {
        String columnsParamsString = jcp.columnPatterns;
        if (columnsParamsString != null) {
            String[] columnParams = columnsParamsString.split(COLUMNS_DELIMETER);
            formats = new Format[columnParams.length];
            String currentFormat;
            formatPattern = Pattern.compile(FORMAT_STRING_PATTERN);
            for (int i = 0; i < columnParams.length; i++) {
                currentFormat = columnParams[i];
                Format format = createFormat(currentFormat);
                if (format == null) {
                    throw new IllegalArgumentException("The columns pattern string is in invalid format");
                }
                formats[i] = format;
            }
        }
    }

    private Format createFormat(String formatOptionsString) {
        Matcher mtchr = formatPattern.matcher(formatOptionsString);
        Format format = null;
        if (mtchr.matches()) {
            String typeKey =  (mtchr.group(1) != null) ? mtchr.group(1) : null;
            String mask = (mtchr.group(2) == null || mtchr.group(2).isEmpty()) ? null : mtchr.group(2);
            String strWidth = (mtchr.group(4) != null) ? mtchr.group(4) : null;
            int width = (strWidth != null) ? Integer.parseInt(strWidth): -1;
            switch (typeKey) {
                case "B":
                    format = new Format(Type.BOOLEAN, mask, width);
                    break;
                case "D":
                    format = new Format(Type.DATE,  mask, width);
                    break;
                case "H":
                    format = new Format(Type.HYPERLINK,  mask, width);
                    break;
                case "N":
                    format = new Format(Type.NUMBER, mask, width);
                    break;
                case "T":
                    format = new Format(Type.TEXT,  mask, width);
                    break;
                default:break;
            }
        }
        return format;
    }

    public void parseOptions(String[] args) throws ParseException {
        getCLIArgs(args);
        parseConverterOptions();
    }

    public Format getFormat(int index) {
        if (this.formats == null) {
            return new Format(Type.TEXT, null, -1);
        }
        else if (index < formats.length) {
            return formats[index];
        }
        else if (jcp.shouldRepeatLastFormat) {
            return formats[formats.length - 1];
        }
        else {
            return new Format(Type.TEXT, null, -1);
        }
    }

    public String[] getSource() {
        return this.sourceFiles;
    }

    public String getDestination() {
        return jcp.outPath;
    }

    public boolean shouldReadHeaderString() {
        return jcp.shouldReadHeaderString;
    }

    public String[] getHeaders() {
        if (jcp.headerNames != null) {
            return jcp.headerNames.split(COLUMNS_DELIMETER);
        }
        return null;
    }

    public int getColCount() {
        return jcp.colCount;
    }
}
