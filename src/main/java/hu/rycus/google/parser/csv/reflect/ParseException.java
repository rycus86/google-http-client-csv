package hu.rycus.google.parser.csv.reflect;

public class ParseException extends Exception {

    public ParseException(final String message) {
        super(message);
    }

    public ParseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParseException(final Throwable cause) {
        super(cause);
    }
}
