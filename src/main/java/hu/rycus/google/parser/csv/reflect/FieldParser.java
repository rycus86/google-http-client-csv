package hu.rycus.google.parser.csv.reflect;

import com.google.api.client.util.Key;
import hu.rycus.google.parser.csv.extra.ExtraFields;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

class FieldParser {

    private static final Collection<? extends Class> supportedTypes =
            Collections.unmodifiableList(Arrays.asList(
                    String.class, Calendar.class, ExtraFields.class,
                    int.class, Integer.class, long.class, Long.class, BigDecimal.class));

    private final Field field;
    private final Class<?> fieldType;
    private final Key annotation;
    private final boolean extraFields;

    FieldParser(final Field field) {
        this.field = field;
        this.fieldType = field.getType();
        this.annotation = field.getAnnotation(Key.class);
        this.extraFields = ExtraFields.class.isAssignableFrom(fieldType);
    }

    public boolean isValid() {
        return annotation != null && supportedTypes.contains(fieldType);
    }

    public boolean isExtraFields() {
        return extraFields;
    }

    public String getFieldId() {
        final String annotatedName = annotation.value();
        final String rawName;
        if ("##default".equals(annotatedName)) {
            rawName = field.getName();
        } else {
            rawName = annotatedName;
        }

        return toId(rawName);
    }

    public TypeParser<?> getTypeParser() {
        if (String.class.equals(fieldType)) {
            return new StringParser(field);
        } else if (int.class.equals(fieldType)) {
            return new IntParser(field);
        } else if (Integer.class.equals(fieldType)) {
            return new IntParser(field);
        } else if (long.class.equals(fieldType)) {
            return new LongParser(field);
        } else if (Long.class.equals(fieldType)) {
            return new LongParser(field);
        } else if (BigDecimal.class.equals(fieldType)) {
            return new BigDecimalParser(field);
        } else if (Calendar.class.equals(fieldType)) {
            return new CalendarParser(field);
        } else if (ExtraFields.class.equals(fieldType)) {
            return new ExtraFieldsParser(field);
        }

        throw new IllegalArgumentException(String.format("Cannot parse %s", fieldType));
    }

    public static String toId(final String source) {
        return source.replace(" ", "").toLowerCase();
    }

}
