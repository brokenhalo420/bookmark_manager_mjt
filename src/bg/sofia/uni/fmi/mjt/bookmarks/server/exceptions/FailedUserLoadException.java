package bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions;

public class FailedUserLoadException extends RuntimeException {
    public FailedUserLoadException(String message) {
        super(message);
    }

    public FailedUserLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
