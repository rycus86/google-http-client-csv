package hu.rycus.google.parser.csv.reflect;

import hu.rycus.google.parser.csv.extra.DateType;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class CalendarParser extends TypeParser<Calendar> {

    private String pattern;

    CalendarParser(final Field field) {
        super(field);

        final DateType dateType = field.getAnnotation(DateType.class);
        if (dateType != null) {
            this.pattern = dateType.value();
        }
    }

    @Override
    public Calendar parse(final String source) throws ParseException {
        if (pattern == null) {
            throw new ParseException("No date/time pattern defined for " + field);
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        try {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(source));
            return calendar;
        } catch (java.text.ParseException ex) {
            throw new ParseException("Failed to parse date: " + source, ex);
        }
    }

}
