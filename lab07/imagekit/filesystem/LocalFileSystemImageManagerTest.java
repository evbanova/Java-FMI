package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileSystemImageManagerTest {

    private LocalFileSystemImageManager fileSystemImageManager;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileSystemImageManager = new LocalFileSystemImageManager();
    }

    @Test
    void testLoadImageThrowsExceptionWhenFileIsNull() {
        assertThrows(IllegalArgumentException.class, () -> fileSystemImageManager.loadImage(null),
                "loadImage should throw IllegalArgumentException when file is null");
    }

    @Test
    void testLoadImageThrowsExceptionWhenFileDoesNotExist() {
        File nonExistentFile = tempDir.resolve("nonExistent.png").toFile();
        assertThrows(IOException.class, () -> fileSystemImageManager.loadImage(nonExistentFile),
                "loadImage should throw IOException when file does not exist");
    }

    @Test
    void testLoadImageThrowsExceptionWhenFileIsDirectory() {
        File directory = tempDir.toFile();
        assertThrows(IOException.class, () -> fileSystemImageManager.loadImage(directory),
                "loadImage should throw IOException when file is a directory");
    }

    @Test
    void testLoadImageThrowsExceptionWhenFormatIsNotSupported() throws IOException {
        File textFile = tempDir.resolve("image.txt").toFile();
        assertTrue(textFile.createNewFile(), "Test setup failed: could not create text file");

        assertThrows(IOException.class, () -> fileSystemImageManager.loadImage(textFile),
                "loadImage should throw IOException when file extension is not supported");
    }

    @Test
    void testLoadImageSuccess() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        File validFile = tempDir.resolve("test.png").toFile();
        ImageIO.write(image, "png", validFile);

        BufferedImage loadedImage = fileSystemImageManager.loadImage(validFile);

        assertNotNull(loadedImage, "Loaded image should not be null");
        assertEquals(image.getWidth(), loadedImage.getWidth(), "Width matches");
        assertEquals(image.getHeight(), loadedImage.getHeight(), "Height matches");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsExceptionWhenDirIsNull() {
        assertThrows(IllegalArgumentException.class, () -> fileSystemImageManager.loadImagesFromDirectory(null),
                "loadImagesFromDirectory should throw IllegalArgumentException when directory is null");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsExceptionWhenDirDoesNotExist() {
        File nonExistentDir = tempDir.resolve("nonExistentDir").toFile();
        assertThrows(IOException.class, () -> fileSystemImageManager.loadImagesFromDirectory(nonExistentDir),
                "loadImagesFromDirectory should throw IOException when directory does not exist");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsExceptionWhenInputIsFile() throws IOException {
        File file = tempDir.resolve("test.png").toFile();
        file.createNewFile();
        assertThrows(IOException.class, () -> fileSystemImageManager.loadImagesFromDirectory(file),
                "loadImagesFromDirectory should throw IOException when input is a regular file");
    }

    @Test
    void testLoadImagesFromDirectorySuccess() throws IOException {
        // Create two dummy images
        BufferedImage image1 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        File file1 = tempDir.resolve("img1.png").toFile();
        File file2 = tempDir.resolve("img2.jpg").toFile();

        ImageIO.write(image1, "png", file1);
        ImageIO.write(image2, "jpg", file2);

        List<BufferedImage> images = fileSystemImageManager.loadImagesFromDirectory(tempDir.toFile());

        assertEquals(2, images.size(), "Should load exactly 2 images");
    }

    @Test
    void testSaveImageThrowsExceptionWhenArgumentsAreNull() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        File file = new File("test.png");

        assertThrows(IllegalArgumentException.class, () -> fileSystemImageManager.saveImage(null, file),
                "saveImage should throw IllegalArgumentException when image is null");
        assertThrows(IllegalArgumentException.class, () -> fileSystemImageManager.saveImage(image, null),
                "saveImage should throw IllegalArgumentException when file is null");
    }

    @Test
    void testSaveImageThrowsExceptionWhenFileAlreadyExists() throws IOException {
        File existingFile = tempDir.resolve("existing.png").toFile();
        existingFile.createNewFile();
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        assertThrows(IOException.class, () -> fileSystemImageManager.saveImage(image, existingFile),
                "saveImage should throw IOException if file already exists");
    }

    @Test
    void testSaveImageThrowsExceptionWhenParentDirDoesNotExist() {
        File nonExistentParent = tempDir.resolve("missing/image.png").toFile();
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        assertThrows(IOException.class, () -> fileSystemImageManager.saveImage(image, nonExistentParent),
                "saveImage should throw IOException if parent directory does not exist");
    }

    @Test
    void testSaveImageSuccess() throws IOException {
        File targetFile = tempDir.resolve("saved.png").toFile();
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        fileSystemImageManager.saveImage(image, targetFile);

        assertTrue(targetFile.exists(), "File should be created");
        assertTrue(targetFile.length() > 0, "File should not be empty");
    }
}