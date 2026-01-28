package bg.sofia.uni.fmi.mjt.newsfeed.parser;

import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NewsResponseParserTest {
    @Test
    void testParseValidJson() {
        String json = """
        {
          "status": "ok",
          "totalResults": 1,
          "articles": []
        }
        """;

        NewsResponseParser parser = new NewsResponseParser();
        NewsResponse response = parser.parse(json);

        assertEquals("ok", response.getStatus());
        assertEquals(1, response.getTotalResults());
        assertNotNull(response.getArticles());
    }

}
