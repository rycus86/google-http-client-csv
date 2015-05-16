package hu.rycus.google.parser.csv;

import com.google.api.client.util.ObjectParser;
import hu.rycus.google.parser.csv.reflect.ClassParser;
import hu.rycus.google.parser.csv.reflect.ParseException;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CsvParser implements ObjectParser {

    private static final ParserRegistry registry = new ParserRegistry();

    private final char separator;
    private final char delimiter;

    public CsvParser() {
        this(',', '"');
    }

    public CsvParser(final char separator, final char delimiter) {
        this.separator = separator;
        this.delimiter = delimiter;
    }

    @Override
    public <T> T parseAndClose(final InputStream inputStream, final Charset charset, final Class<T> aClass) throws IOException {
        try {
            return parseAndClose(new InputStreamReader(inputStream, charset), aClass);
        } finally {
            inputStream.close();
        }
    }

    @Override
    public Object parseAndClose(final InputStream inputStream, final Charset charset, final Type type) throws IOException {
        try {
            return parseAndClose(new InputStreamReader(inputStream, charset), type);
        } finally {
            inputStream.close();
        }
    }

    @Override
    public Object parseAndClose(final Reader reader, final Type type) throws IOException {
        try {
            if (!(type instanceof Class)) {
                throw new IOException("Class argument expected for Type");
            }

            return parseAndClose(reader, (Class) type);
        } finally {
            reader.close();
        }
    }

    @Override
    public <T> T parseAndClose(final Reader source, final Class<T> aClass) throws IOException {
        if (!aClass.isArray()) {
            throw new IOException(String.format("Array type required for parsing CSV files (%s found)", aClass));
        }

        try {
            final ClassParser parser;

            try {
                parser = registry.get(aClass.getComponentType());
            } catch (ParseException ex) {
                throw new IOException(ex);
            }

            final BufferedReader reader = new BufferedReader(source);
            final Session session = new Session();

            final String header = trimHeader(reader.readLine());
            try {
                session.init(header);
            } catch (ParseException ex) {
                throw new IOException("Failed to parse CSV header: " + header, ex);
            }

            final List<Object> items = new LinkedList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    final Object parsed = parser.parseObject(session.toValues(line));
                    items.add(parsed);
                } catch (ParseException ex) {
                    throw new IOException(ex);
                }
            }

            final int length = items.size();

            @SuppressWarnings("unchecked")
            final T array = (T) Array.newInstance(aClass.getComponentType(), length);

            for (int index = 0; index < items.size(); index++) {
                Array.set(array, index, items.get(index));
            }

            return array;
        } finally {
            source.close();
        }
    }

    private static String trimHeader(final String original) {
        if (original != null && original.charAt(0) == 0xFEFF) {
            return original.substring(1);
        } else {
            return original;
        }
    }

    class Session {

        private final StringBuilder buffer = new StringBuilder();

        private String[] headers;

        void init(final String header) throws ParseException {
            this.headers = getTokens(trimHeader(header));
        }

        Map<String, String> toValues(final String line) throws ParseException {
            final String[] params = getTokens(line);

            final Map<String, String> values = new HashMap<>();
            for (int idx = 0; idx < Math.min(headers.length, params.length); idx++) {
                values.put(headers[idx], params[idx]);
            }

            return values;
        }

        String[] getTokens(final String source) throws ParseException {
            final List<String> tokens = new LinkedList<>();

            buffer.setLength(0);

            final int length = source.length();

            boolean inString = false;
            boolean wasDelimiter = false;

            for (int index = 0; index < length; index++) {
                final char ch = source.charAt(index);

                if (ch == delimiter) {
                    if (wasDelimiter && !inString) {
                        buffer.append(ch);
                    }

                    inString = !inString;
                    wasDelimiter = true;
                } else {
                    if (ch == separator) {
                        if (inString) {
                            buffer.append(ch);
                        } else {
                            tokens.add(buffer.toString());
                            buffer.setLength(0);
                        }
                    } else {
                        if (wasDelimiter && !inString) {
                            buffer.append(delimiter);
                        }

                        buffer.append(ch);
                    }

                    wasDelimiter = false;
                }
            }

            tokens.add(buffer.toString());
            buffer.setLength(0);

            return tokens.toArray(new String[tokens.size()]);
        }

    }

    private static class ParserRegistry {

        private final ConcurrentHashMap<Class<?>, ClassParser> classParsers = new ConcurrentHashMap<>();

        public ClassParser get(final Class<?> aClass) throws ParseException {
            final ClassParser parser = classParsers.get(aClass);
            if (parser != null) {
                return parser;
            } else {
                final ClassParser newParser = new ClassParser(aClass);
                newParser.parseMetadata();
                classParsers.putIfAbsent(aClass, newParser);
                return newParser;
            }
        }

    }

}
