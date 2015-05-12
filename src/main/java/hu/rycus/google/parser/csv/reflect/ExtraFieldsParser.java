package hu.rycus.google.parser.csv.reflect;

import hu.rycus.google.parser.csv.extra.ExtraFields;

import java.lang.reflect.Field;

class ExtraFieldsParser extends StringParser {

    ExtraFieldsParser(final Field field) {
        super(field);
    }

    @Override
    public void writeField(final Object instance, final String source) throws ParseException {
        throw new UnsupportedOperationException("ExtraFields need the field name and the value to be written");
    }

    void writeField(final Object instance, final String name, final String value) throws ParseException {
        try {
            final ExtraFields existing = getFieldValue(instance);
            final ExtraFields targetInstance;
            if (existing != null) {
                targetInstance = existing;
            } else {
                targetInstance = new ExtraFields();
                writeFieldValue(instance, targetInstance);
            }

            targetInstance.put(name, value);
        } catch (Exception ex) {
            throw new ParseException(String.format("Failed to write extra fields into %s.%s",
                    field.getDeclaringClass(), field.getName()), ex);
        }
    }

    private ExtraFields getFieldValue(final Object instance) throws IllegalAccessException {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return (ExtraFields) field.get(instance);
        } finally {
            field.setAccessible(accessible);
        }
    }

}
