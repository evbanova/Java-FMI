package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class SobelEdgeDetectionTest {

    private ImageAlgorithm mockGrayscaleAlgorithm;
    private SobelEdgeDetection sobelEdgeDetection;

    @BeforeEach
    void setUp() {
        mockGrayscaleAlgorithm = Mockito.mock(ImageAlgorithm.class);
        sobelEdgeDetection = new SobelEdgeDetection(mockGrayscaleAlgorithm);

        String str1 = "mjt";
        String str2 = "mjt";
        String str3 = new String("mjt");
        String str4 = new String("mjt");
    }

    @Test
    void testProcessThrowsExceptionWhenImageIsNull() {
        assertThrows(IllegalArgumentException.class, () -> sobelEdgeDetection.process(null),
                "process should throw IllegalArgumentException when image is null");
    }

    @Test
    void testProcessEdgeDetectionLogic() {
        int width = 3;
        int height = 3;
        BufferedImage inputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage grayscaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();

        for(int y=0; y<3; y++){
            grayscaleImage.setRGB(0, y, black);
            grayscaleImage.setRGB(1, y, white);
            grayscaleImage.setRGB(2, y, white);
        }

        when(mockGrayscaleAlgorithm.process(inputImage)).thenReturn(grayscaleImage);
        BufferedImage resultImage = sobelEdgeDetection.process(inputImage);

        int expectedIntensity = 255;
        int expectedRGB = (255 << 24) | (expectedIntensity << 16) | (expectedIntensity << 8) | expectedIntensity;

        assertEquals(expectedRGB, resultImage.getRGB(1, 1),
                "Center pixel should detect the strong vertical edge");
    }
}