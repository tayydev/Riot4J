package tech.nathann.riot4j.exceptions;

public class RetryableException extends RuntimeException {

    private final Throwable enclosing;

    public RetryableException(Throwable enclosing) {
        super(enclosing);
        this.enclosing = enclosing;
    }

    @Override
    public String toString() {
        return "RetryableException{" +
                "enclosing=" + enclosing +
                "} " + super.toString();
    }
}
