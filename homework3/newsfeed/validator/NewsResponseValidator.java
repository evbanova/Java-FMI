package bg.sofia.uni.fmi.mjt.newsfeed.validator;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.BadRequestException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.UnauthorizedException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NotFoundException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RequestTimeoutException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.InternalServerErrorException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NotImplementedException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.ServiceUnavailableException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsApiException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;

import java.net.http.HttpResponse;

public class NewsResponseValidator {
    private static final int OK = 200;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int NOT_IMPLEMENTED = 501;
    private static final int SERVICE_UNAVAILABLE = 503;

    public void validate(HttpResponse<String> response) throws NewsApiException {
        //HTTP validation
        int status = response.statusCode();

        switch (status) {
            case OK -> {
                return;
            }
            case BAD_REQUEST -> throw new BadRequestException();
            case UNAUTHORIZED -> throw new UnauthorizedException();
            case NOT_FOUND -> throw new NotFoundException();
            case REQUEST_TIMEOUT -> throw new RequestTimeoutException();
            case INTERNAL_SERVER_ERROR -> throw new InternalServerErrorException();
            case NOT_IMPLEMENTED -> throw new NotImplementedException();
            case SERVICE_UNAVAILABLE -> throw new ServiceUnavailableException();

            default -> throw new NewsApiException("Unexpected response status: " + status);
        }
    }

    public void validateAfterParsing(NewsResponse response) throws NewsApiException {
        //JSON validation
        if ("error".equals(response.getStatus())) {
            throw mapErrorCodeToException(response.getCode(), response.getMessage());
        }
    }

    private NewsApiException mapErrorCodeToException(String code, String message) {
        //error codes as they are in the NewsApi site
        return switch (code) {
            case "apiKeyInvalid", "apiKeyMissing" -> new UnauthorizedException(message);
            case "rateLimited" -> new RequestTimeoutException(message);
            case "parameterInvalid", "parametersMissing" -> new BadRequestException(message);
            case "sourceDoesNotExist" -> new NotFoundException(message);

            default -> new NewsApiException("Unexpected JSON response status: " + message);
        };
    }

}
