package hu.rycus.google.parser.csv.reflect;

import java.lang.reflect.Field;

class LongParser extends TypeParser<Long> {

    LongParser(final Field field) {
        super(field);
    }

    @Override
    public Long parse(final String source) throws ParseException {
        return Long.parseLong(source);
    }

}
