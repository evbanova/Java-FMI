package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LuminosityGrayscaleTest {

    private LuminosityGrayscale grayscaleAlgorithm;

    @BeforeEach
    void setUp() {
        grayscaleAlgorithm = new LuminosityGrayscale();
    }

    @Test
    void testProcessThrowsExceptionWhenImageIsNull() {
        assertThrows(IllegalArgumentException.class, () -> grayscaleAlgorithm.process(null),
                "process should throw IllegalArgumentException when image is null");
    }

    @Test
    void testProcessConvertsToGrayscaleCorrectly() {
        int width = 1;
        int height = 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Color redColor = new Color(255, 0, 0);
        image.setRGB(0, 0, redColor.getRGB());

        // 0.21 * 255 + 0.72 * 0 + 0.07 * 0 = 53.55
        int expectedGrayValue = 54;
        int expectedRGB = (255 << 24) | (expectedGrayValue << 16) | (expectedGrayValue << 8) | expectedGrayValue;

        BufferedImage processedImage = grayscaleAlgorithm.process(image);
        int actualRGB = processedImage.getRGB(0, 0);
        assertEquals(expectedRGB, actualRGB, "The pixel should be converted to grayscale using luminosity formula");
    }

    @Test
    void testProcessDimensionsUnchanged() {
        BufferedImage image = new BufferedImage(10, 20, BufferedImage.TYPE_INT_RGB);
        BufferedImage processed = grayscaleAlgorithm.process(image);

        assertEquals(10, processed.getWidth());
        assertEquals(20, processed.getHeight());
    }
}