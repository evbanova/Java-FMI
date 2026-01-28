package bg.sofia.uni.fmi.mjt.newsfeed.uri;

import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.request.NewsRequestBuilder;
import org.junit.jupiter.api.Test;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsUriTest {
    private final NewsUri newsUri = new NewsUri();

    @Test
    void testBuildUriFromRequestEncodesKeywords() {
        NewsRequest request = new NewsRequestBuilder("space exploration").build();
        URI uri = newsUri.buildUriFromRequest(request);

        String uriString = uri.toString();
        assertTrue(uriString.contains("q=space+exploration") || uriString.contains("q=space%20exploration"),
                "Keywords should be URLEncoded");
    }

    @Test
    void testBuildUriIncludesOptionalParams() {
        NewsRequest request = new NewsRequestBuilder("test")
                .category("health").country("bg").build();
        URI uri = newsUri.buildUriFromRequest(request);

        assertTrue(uri.toString().contains("category=health"));
        assertTrue(uri.toString().contains("country=bg"));
    }
}