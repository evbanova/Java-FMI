package bg.sofia.uni.fmi.mjt.steganography;

import java.awt.image.BufferedImage;

public class EmbedConsumer extends Consumer {
    public EmbedConsumer(SharedBuffer buffer, String outputDirectory) {
        super(buffer, outputDirectory);
    }

    @Override
    protected BufferedImage processTask(SteganographyImage image) {
        return embedSecret(image.cover(), image.secret());
    }

    private BufferedImage embedSecret(BufferedImage cover, BufferedImage secret) {
        int[] coverCoordinates = {0, 0};

        embedDimensions(cover, secret.getWidth(), coverCoordinates);
        embedDimensions(cover, secret.getHeight(), coverCoordinates);
        embedSecretPixels(cover, secret, coverCoordinates);

        return cover;
    }

    private void embedDimensions(BufferedImage cover, int secret, int [] coordinates) {
        for (int i = 0; i < COUNT_BITS_FOR_STEP; i++) {
            int pixel = cover.getRGB(coordinates[0], coordinates[1]);

            int bitR = getBit(secret, i * THREE_PIXELS, TOTAL_BITS);
            int bitG = getBit(secret, i * THREE_PIXELS + 1, TOTAL_BITS);
            int bitB = getBit(secret, i * THREE_PIXELS + 2, TOTAL_BITS);

            int newPixel = updatePixelLSB(pixel, bitR, bitG, bitB);
            cover.setRGB(coordinates[0], coordinates[1], newPixel);
            advanceCursor(cover, coordinates);
        }
    }

    private void embedSecretPixels(BufferedImage cover, BufferedImage secret, int[] coordinates) {
        int sWidth = secret.getWidth();
        int sHeight = secret.getHeight();

        for (int y = 0; y < sHeight; y++) {
            for (int x = 0; x < sWidth; x++) {
                int sPixel = secret.getRGB(x, y);
                int grayAvg = calculateGrayscale(sPixel);

                int bit1 = getBit(grayAvg, 0, TWO_BYTES);
                int bit2 = getBit(grayAvg, 1, TWO_BYTES);
                int bit3 = getBit(grayAvg, 2, TWO_BYTES);

                int cPixel = cover.getRGB(coordinates[0], coordinates[1]);
                int newCPixel = updatePixelLSB(cPixel, bit1, bit2, bit3);
                cover.setRGB(coordinates[0], coordinates[1], newCPixel);

                advanceCursor(cover, coordinates);
            }
        }
    }

    private int calculateGrayscale(int pixel) {
        int r = (pixel >> THREE_BYTES) & RGB;
        int g = (pixel >> TWO_BYTES) & RGB;
        int b = pixel & RGB;

        return (r + g + b) / THREE_PIXELS;
    }

    private int updatePixelLSB(int pixel, int bitR, int bitG, int bitB) {
        int r = (pixel >> THREE_BYTES) & RGB;
        int g = (pixel >> TWO_BYTES) & RGB;
        int b = pixel & RGB;

        r = (r & (RGB - 1)) | bitR;
        g = (g & (RGB - 1)) | bitG;
        b = (b & (RGB - 1)) | bitB;

        return (pixel & ALPHA_MASK) | (r << THREE_BYTES) | (g << TWO_BYTES) | b;
    }
}
