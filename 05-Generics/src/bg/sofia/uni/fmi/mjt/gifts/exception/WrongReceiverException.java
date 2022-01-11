package bg.sofia.uni.fmi.mjt.gifts.exception;

public class WrongReceiverException extends IllegalArgumentException {

    public WrongReceiverException() {
    }

    public WrongReceiverException(String s) {
        super(s);
    }

    public WrongReceiverException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongReceiverException(Throwable cause) {
        super(cause);
    }

}
