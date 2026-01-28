package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class NotFoundException extends NewsApiException {
    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }
}
