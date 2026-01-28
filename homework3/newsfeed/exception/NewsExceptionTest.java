package bg.sofia.uni.fmi.mjt.newsfeed.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NewsExceptionTest {

    @Test
    void testExceptionInheritanceAndMessages() {
        String msg = "error message";

        // Testing various exceptions from the hierarchy
        NewsApiException badRequest = new BadRequestException(msg);
        NewsApiException unauthorized = new UnauthorizedException(msg);
        NewsApiException notFound = new NotFoundException(msg);
        NewsApiException timeout = new RequestTimeoutException(msg);
        NewsApiException serverError = new InternalServerErrorException(msg);
        NewsApiException notImplemented = new NotImplementedException(msg);
        NewsApiException serviceUnavailable = new ServiceUnavailableException(msg);

        // Verify messages
        assertEquals(msg, badRequest.getMessage());
        assertEquals(msg, unauthorized.getMessage());
        assertEquals(msg, notFound.getMessage());
        assertEquals(msg, timeout.getMessage());
        assertEquals(msg, serverError.getMessage());
        assertEquals(msg, notImplemented.getMessage());
        assertEquals(msg, serviceUnavailable.getMessage());
    }

    @Test
    void testDefaultConstructors() {
        assertNull(new BadRequestException().getMessage());
        assertNull(new NewsApiException().getMessage());
    }
}