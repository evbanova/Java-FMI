package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.image.BufferedImage;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm{

    private ImageAlgorithm grayscaleAlgorithm;
    private static final int[][] GX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static final int[][] GY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm){
        if (grayscaleAlgorithm != null) {
            this.grayscaleAlgorithm = grayscaleAlgorithm;
        }
    }

    private int[][] getNeighborhoodPixels(int x, int y, int width, int height, BufferedImage image){
        if (x < 0 || y < 0 || x >= width || y >= height) {
            throw new IllegalArgumentException("Cannot get neighborhood pixels from negavative index or out of bounds");
        }

        int[][] pixels = new int[3][3];
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                int checkX = x + i - 1;
                int checkY = y + j - 1;

                if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                    int rgb = image.getRGB(checkX, checkY);
                    //its grayscale, red, blue and green are the same, we are taking one of them
                    pixels[i][j]=rgb & 0xFF;
                } else {
                    pixels[i][j] = 0;
                }
            }
        }
        return pixels;
    }

    private int getGx(int[][] pixels){
        int sum = 0;
        for (int i =0; i<3; i++) {
            for (int j=0; j<3; j++) {
                sum += GX[j][i] * pixels[i][j];
            }
        }
        return sum;
    }

    private int getGy(int[][] pixels){
        int sum = 0;
        for (int i =0; i<3; i++) {
            for (int j=0; j<3; j++) {
                sum += GY[j][i] * pixels[i][j];
            }
        }
        return sum;
    }

    private void setPixelValue(int[][] pixels, BufferedImage image, int x, int y){
        double G = Math.sqrt(Math.pow(getGx(pixels),2) + Math.pow(getGy(pixels),2));

        int pixelValue = Math.min(255, (int) Math.round(G));
        int rgb =  (255 << 24) | (pixelValue << 16) | (pixelValue << 8) | pixelValue;

        image.setRGB(x, y, rgb);
    }

    public BufferedImage process(BufferedImage image){
        if (image == null) {
            throw new IllegalArgumentException("Image must not be null");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = grayscaleAlgorithm.process(image);
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[][] pixels = getNeighborhoodPixels(x, y, width, height, newImage);
                setPixelValue(pixels, finalImage, x, y);
            }
        }

        return finalImage;
    }
}
