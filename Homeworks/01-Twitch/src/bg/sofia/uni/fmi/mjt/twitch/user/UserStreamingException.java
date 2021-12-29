package bg.sofia.uni.fmi.mjt.twitch.user;

public class UserStreamingException extends Exception {
    public UserStreamingException() {
    }

    public UserStreamingException(String message) {
        super(message);
    }

    public UserStreamingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserStreamingException(Throwable cause) {
        super(cause);
    }
}
