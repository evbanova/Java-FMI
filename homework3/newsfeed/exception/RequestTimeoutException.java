package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class RequestTimeoutException extends NewsApiException {
    public RequestTimeoutException() {
        super();
    }

    public RequestTimeoutException(String message) {
        super(message);
    }
}
