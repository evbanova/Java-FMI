package bg.sofia.uni.fmi.mjt.newsfeed.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NewsRequestTest {

    @Test
    void testBuilderThrowsExceptionWhenKeywordsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> new NewsRequestBuilder(null),
                "Keywords are required");
    }

    @Test
    void testBuilderSetsOptionalFieldsCorrectly() {
        NewsRequest request = new NewsRequestBuilder("tech")
                            .category("business").country("us").page(2)
                            .pageSize(10).build();

        assertEquals("tech", request.getKeywords());
        assertEquals("business", request.getCategory());
        assertEquals("us", request.getCountry());
        assertEquals(2, request.getPage());
        assertEquals(10, request.getPageSize());
    }

    @Test
    void testBuilderThrowsExceptionForInvalidPage() {
        NewsRequestBuilder builder = new NewsRequestBuilder("test");
        assertThrows(IllegalArgumentException.class, () -> builder.page(0),
                "Pages must be >= 1");
    }
}
