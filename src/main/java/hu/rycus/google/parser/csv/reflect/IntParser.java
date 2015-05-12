package hu.rycus.google.parser.csv.reflect;

import java.lang.reflect.Field;

class IntParser extends TypeParser<Integer> {

    IntParser(final Field field) {
        super(field);
    }

    @Override
    public Integer parse(final String source) throws ParseException {
        return Integer.parseInt(source);
    }

}
