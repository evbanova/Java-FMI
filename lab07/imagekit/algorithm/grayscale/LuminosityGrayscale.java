package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm{

    public LuminosityGrayscale(){}

    private int changePixelColor(int pixel){
        Color c = new Color(pixel);
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        int pixelColor = (int)Math.round(0.21*red + 0.72*green + 0.07*blue);
        //making it in the rgb format
        return (255 << 24) | (pixelColor << 16) | (pixelColor << 8) | pixelColor;
    }

    public BufferedImage process(BufferedImage image){
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null.");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                newImage.setRGB(x, y, changePixelColor(pixel));
            }
        }

        return newImage;
    }
}
