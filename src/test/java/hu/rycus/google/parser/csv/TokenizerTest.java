package hu.rycus.google.parser.csv;

import hu.rycus.google.parser.csv.reflect.ParseException;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TokenizerTest {

    private final CsvParser parser = new CsvParser();

    @Test
    public void testSimpleCase() throws ParseException {
        final String[] tokens = parser.getTokens("a,b,c,d");
        assertEquals(4, tokens.length);
        assertArrayEquals(new String[] { "a", "b", "c", "d" }, tokens);
    }

    @Test
    public void testDelimited() throws ParseException {
        final String[] tokens = parser.getTokens("a,b,\"c,c\",d");
        assertEquals(4, tokens.length);
        assertArrayEquals(new String[] { "a", "b", "c,c", "d" }, tokens);
    }

    @Test
    public void testDelimiterInToken() throws ParseException {
        final String[] tokens = parser.getTokens("a,b\"\"b,c,d");
        assertEquals(4, tokens.length);
        assertArrayEquals(new String[] { "a", "b\"b", "c", "d" }, tokens);
    }

    @Test
    public void testMultipleDelimiterInToken() throws ParseException {
        final String[] tokens = parser.getTokens("a,b\"\"b\"\"b,c,d");
        assertEquals(4, tokens.length);
        assertArrayEquals(new String[] { "a", "b\"b\"b", "c", "d" }, tokens);
    }

    @Test
    public void testDelimiterInDelimitedValue() throws ParseException {
        final String[] tokens = parser.getTokens("a,\"b\"\"b\",c,d");
        assertEquals(4, tokens.length);
        assertArrayEquals(new String[] { "a", "b\"b", "c", "d" }, tokens);
    }

    @Test
    public void testManyDelimiters() throws ParseException {
        final String[] tokens = parser.getTokens("a,\"\"\"\"\"\",c,d");
        assertEquals(4, tokens.length);
        assertArrayEquals(new String[] { "a", "\"\"", "c", "d" }, tokens);
    }

}