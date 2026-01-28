package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class UnauthorizedException extends NewsApiException {
    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
