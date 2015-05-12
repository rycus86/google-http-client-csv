package hu.rycus.google.parser.csv.reflect;

import java.lang.reflect.Field;

class StringParser extends TypeParser<String> {

    StringParser(final Field field) {
        super(field);
    }

    @Override
    public String parse(final String source) throws ParseException {
        return source;
    }

}
