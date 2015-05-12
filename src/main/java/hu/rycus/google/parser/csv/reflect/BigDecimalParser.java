package hu.rycus.google.parser.csv.reflect;

import java.lang.reflect.Field;
import java.math.BigDecimal;

class BigDecimalParser extends TypeParser<BigDecimal> {

    BigDecimalParser(final Field field) {
        super(field);
    }

    @Override
    public BigDecimal parse(final String source) throws ParseException {
        try {
            return new BigDecimal(source);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
