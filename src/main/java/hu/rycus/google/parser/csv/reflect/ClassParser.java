package hu.rycus.google.parser.csv.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassParser {

    private final Class<?> theClass;
    private final Map<String, TypeParser<?>> typeParsers = new HashMap<>();
    private ExtraFieldsParser extraFieldsParser;

    public ClassParser(final Class<?> theClass) {
        this.theClass = theClass;
    }

    public void parseMetadata() throws ParseException {
        final List<FieldParser> collector = new LinkedList<>();
        for (final FieldParser fieldParser : collectFieldParsers(theClass, collector)) {
            if (fieldParser.isExtraFields()) {
                setExtraFieldsParser(fieldParser);
            } else {
                typeParsers.put(fieldParser.getFieldId(), fieldParser.getTypeParser());
            }
        }
    }

    private void setExtraFieldsParser(final FieldParser fieldParser) throws ParseException {
        if (extraFieldsParser != null) {
            throw new ParseException("Multiple ExtraFields defined in " + theClass);
        } else {
            extraFieldsParser = (ExtraFieldsParser) fieldParser.getTypeParser();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T parseObject(final Map<String, String> values) throws ParseException {
        final T instance;

        try {
            final Constructor<?> constructor = theClass.getDeclaredConstructor();
            final boolean accessible = constructor.isAccessible();
            try {
                constructor.setAccessible(true);
                instance = (T) constructor.newInstance();
            } finally {
                constructor.setAccessible(accessible);
            }
        } catch (Exception ex) {
            throw new ParseException(String.format("Failed to instantiate %s", theClass), ex);
        }

        for (final Map.Entry<String, String> entry : values.entrySet()) {
            final String name = entry.getKey();
            final String value = entry.getValue();

            final String id = FieldParser.toId(name);
            final TypeParser<?> parser = typeParsers.get(id);

            if (parser != null) {
                parser.writeField(instance, value);
            } else if (extraFieldsParser != null) {
                extraFieldsParser.writeField(instance, name, value);
            }
        }

        return instance;
    }

    private List<FieldParser> collectFieldParsers(final Class<?> aClass, final List<FieldParser> parsers) {
        for (final Field field : aClass.getDeclaredFields()) {
            final FieldParser parser = new FieldParser(field);
            if (parser.isValid()) {
                parsers.add(parser);
            }
        }

        final Class<?> superClass = aClass.getSuperclass();
        if (superClass != null) {
            collectFieldParsers(superClass, parsers);
        }

        return parsers;
    }

}
