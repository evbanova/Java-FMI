package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class BadRequestException extends NewsApiException {
    public BadRequestException() {
        super();
    }

    public BadRequestException(String message) {
        super(message);
    }
}
