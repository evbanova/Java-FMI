package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class ServiceUnavailableException extends NewsApiException {
    public ServiceUnavailableException() {
        super();
    }

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
