package bg.sofia.uni.fmi.mjt.newsfeed.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NewsModelTest {

    @Test
    void testSourceRecord() {
        Source source = new Source("cnn", "CNN News");

        assertEquals("cnn", source.getId());
        assertEquals("CNN News", source.getName());
    }

    @Test
    void testArticleRecordAndAccessors() {
        Source source = new Source("1", "Source Name");
        Article article = new Article(source, "Author", "Title",
                    "Desc", "url", "img",
                    "date", "content");

        assertEquals(source, article.getSource());
        assertEquals("Author", article.getAuthor());
        assertEquals("Title", article.getTitle());
        assertEquals("Desc", article.getDescription());
        assertEquals("url", article.getUrl());
        assertEquals("img", article.getUrlToImage());
        assertEquals("date", article.getPublishedAt());
        assertEquals("content", article.getContent());
    }
}
