package hu.rycus.google.parser.csv.reflect;

import java.lang.reflect.Field;

abstract class TypeParser<T> {

    protected final Field field;

    TypeParser(final Field field) {
        this.field = field;
    }

    public abstract T parse(final String source) throws ParseException;

    public void writeField(final Object instance, final String source) throws ParseException {
        if (source == null || source.isEmpty()) {
            return;
        }

        try {
            final T value = parse(source);
            writeFieldValue(instance, value);
        } catch (Exception ex) {
            throw new ParseException(String.format("Failed to parse %s into %s.%s [type: %s]",
                    source, field.getDeclaringClass(), field.getName(), field.getType()), ex);
        }
    }

    void writeFieldValue(final Object instance, final Object value) throws IllegalAccessException {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } finally {
            field.setAccessible(accessible);
        }
    }

}
