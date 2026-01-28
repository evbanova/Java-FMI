package bg.sofia.uni.fmi.mjt.newsfeed.validator;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.*;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsResponseValidatorTest {
    private final NewsResponseValidator validator = new NewsResponseValidator();

    @Test
    void testValidateThrowsUnauthorizedFor401() {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(401);

        assertThrows(UnauthorizedException.class, () -> validator.validate(response),
                "Status 401 should throw UnauthorizedException");
    }

    @Test
    void testValidateAfterParsingThrowsExceptionForErrorStatus() {
        NewsResponse errorResponse = mock(NewsResponse.class);
        when(errorResponse.getStatus()).thenReturn("error");
        when(errorResponse.getCode()).thenReturn("rateLimited");
        when(errorResponse.getMessage()).thenReturn("Too many requests");

        assertThrows(RequestTimeoutException.class, () -> validator.validateAfterParsing(errorResponse),
                "rateLimited code should map to RequestTimeoutException");
    }
}