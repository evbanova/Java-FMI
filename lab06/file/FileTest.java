package bg.sofia.uni.fmi.mjt.file;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileTest {

   // Test for null arguments
   @Test
    void testFileConstructorThrowsOnNullContent() {
        String content = null;
    
        assertThrows(IllegalArgumentException.class, () -> new File(content),
                "Should throw IllegalArgumentException if file-s content is null");
    }
   
    @Test
    void testFileCreationAndContentAccess() {
        String content = "Test Content";
        File file = new File(content);

        assertEquals(content, file.getContent());

        String newContent = "New Content";
        file.setContent(newContent);
        assertEquals(newContent, file.getContent());
    }

    @Test
    void testFileEquality() {
        File f1 = new File("same");
        File f2 = new File("same");

        assertEquals(f1.getContent(), f2.getContent());
    }
}