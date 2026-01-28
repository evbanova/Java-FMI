package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class InternalServerErrorException extends NewsApiException {
    public InternalServerErrorException() {
        super();
    }

    public InternalServerErrorException(String message) {
        super(message);
    }
}
