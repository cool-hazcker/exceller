package com.digsolab;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.apache.commons.cli.ParseException;


public class ConverterOptionsTest {
    @Test
    public void testPatternStringWithUnexpectedDataTypeFailure() {
        try {
            String[] args = {"-columns", "Lyyyy-mm-ddW5000", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
        }
        catch (IllegalArgumentException illArgEx) {
            assertThat(illArgEx.getMessage(), is("The columns pattern string is in invalid format"));
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testPatternStringWithNotIntegerWidthFailure() {
        try {
            String[] args = {"-columns", "Dyyyy-mm-ddW5.5", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
        }
        catch (IllegalArgumentException illArgEx) {
            assertThat(illArgEx.getMessage(), is("The columns pattern string is in invalid format"));
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testNoCsvSourceProvidedFailure() {
        try {
            String[] args = {};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
        }
        catch (IllegalArgumentException illArgEx) {
            assertThat(illArgEx.getMessage(), is("You haven't provided the CSV source"));
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testPatternStringNoMaskAndNoWidthProvidedSuccess() {
        try {
            String[] args = {"-columns", "D", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.DATE, co.getFormat(0).getType());
            assertEquals(null, co.getFormat(0).getMask());
            assertEquals(-1, co.getFormat(0).getWidth());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testPatternStringNoMaskWidthProvidedSuccess() {
        try {
            String[] args = {"-columns", "Dyyyy-mm-dd", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.DATE, co.getFormat(0).getType());
            assertEquals("yyyy-mm-dd", co.getFormat(0).getMask());
            assertEquals(-1, co.getFormat(0).getWidth());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testPatternStringNoMaskProvidedSuccess() {
        try {
            String[] args = {"-columns", "DW5000", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.DATE, co.getFormat(0).getType());
            assertEquals(null, co.getFormat(0).getMask());
            assertEquals(5000, co.getFormat(0).getWidth());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testSingleCsvSourcesProvided() {
        try {
            String[] args = {"test1.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(1, co.getSource().length);
            assertEquals("test1.csv", co.getSource()[0]);
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testMultipleCsvSourcesProvided() {
        try {
            String[] args = {"test1.csv;test2.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(2, co.getSource().length);
            assertEquals("test1.csv", co.getSource()[0]);
            assertEquals("test2.csv", co.getSource()[1]);
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testDestinationGetsParsed() {
        try {
            String[] args = {"test.csv", "-output", "/tmp/test.xlsx"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals("/tmp/test.xlsx", co.getDestination());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testHeaderNamesGetParsed() {
        try {
            String[] args = {"-h", "Name;Age;Rank", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(3, co.getHeaders().length);
            assertEquals("Name", co.getHeaders()[0]);
            assertEquals("Age", co.getHeaders()[1]);
            assertEquals("Rank", co.getHeaders()[2]);
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testPatternStringGetsParsed() {
        try {
            String[] args = {"-columns", "D;H;T;B;N", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.DATE, co.getFormat(0).getType());
            assertEquals(null, co.getFormat(0).getMask());
            assertEquals(-1, co.getFormat(0).getWidth());
            assertEquals(Type.HYPERLINK, co.getFormat(1).getType());
            assertEquals(null, co.getFormat(1).getMask());
            assertEquals(-1, co.getFormat(1).getWidth());
            assertEquals(Type.TEXT, co.getFormat(2).getType());
            assertEquals(null, co.getFormat(2).getMask());
            assertEquals(-1, co.getFormat(2).getWidth());
            assertEquals(Type.BOOLEAN, co.getFormat(3).getType());
            assertEquals(null, co.getFormat(3).getMask());
            assertEquals(-1, co.getFormat(3).getWidth());
            assertEquals(Type.NUMBER, co.getFormat(4).getType());
            assertEquals(null, co.getFormat(4).getMask());
            assertEquals(-1, co.getFormat(4).getWidth());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testGetFormatReturnsTextFormatIfIndexIsLargerThanLengthOfPatternString() {
        try {
            String[] args = {"-columns", "D;N", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.TEXT, co.getFormat(2).getType());
            assertEquals(null, co.getFormat(2).getMask());
            assertEquals(-1, co.getFormat(2).getWidth());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testGetFormatReturnsLAstFormatIfIterateLastOptionIsSet() {
        try {
            String[] args = {"-columns", "D;N", "-il", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.NUMBER, co.getFormat(2).getType());
            assertEquals(null, co.getFormat(2).getMask());
            assertEquals(-1, co.getFormat(2).getWidth());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testGetFormatReturnsTextFormatIfNoPatternStringProvided() {
        try {
            String[] args = {"test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.TEXT, co.getFormat(0).getType());
            assertEquals(null, co.getFormat(0).getMask());
            assertEquals(-1, co.getFormat(0).getWidth());
        }
        catch (ParseException ex) {

        }
    }

    @Test
    public void testAllArgumentsProvided() {
        try {
            String[] args = {"-columns", "Dyyyy-mm-dd;N", "-il", "-h", "Name;Age;Rank", "-H", "-n", "3",
                    "-output", "result.xlsx", "test.csv"};
            ConverterOptions co = new ConverterOptions();
            co.parseOptions(args);
            assertEquals(Type.DATE, co.getFormat(0).getType());
            assertEquals("yyyy-mm-dd", co.getFormat(0).getMask());
            assertEquals(-1, co.getFormat(0).getWidth());
            assertEquals(Type.NUMBER, co.getFormat(1).getType());
            assertEquals(null, co.getFormat(1).getMask());
            assertEquals(-1, co.getFormat(1).getWidth());
            assertEquals(Type.NUMBER, co.getFormat(2).getType());
            assertEquals(null, co.getFormat(2).getMask());
            assertEquals(-1, co.getFormat(2).getWidth());
            assertEquals(3, co.getHeaders().length);
            assertEquals("Name", co.getHeaders()[0]);
            assertEquals("Age", co.getHeaders()[1]);
            assertEquals("Rank", co.getHeaders()[2]);
            assertEquals(true, co.shouldReadHeaderString());
            assertEquals(3, co.getColCount());
            assertEquals(1, co.getSource().length);
            assertEquals("test.csv", co.getSource()[0]);
            assertEquals("result.xlsx", co.getDestination());
        }
        catch (ParseException ex) {

        }
    }
}
