package bg.sofia.uni.fmi.mjt.steganography;

import java.awt.image.BufferedImage;

public class ExtractConsumer extends Consumer {
    public ExtractConsumer(SharedBuffer buffer, String outputDirectory) {
        super(buffer, outputDirectory);
    }

    @Override
    protected BufferedImage processTask(SteganographyImage image) {
        return extractSecret(image.cover());
    }

    private BufferedImage extractSecret(BufferedImage encoded) {
        int[] coords = {0, 0};
        int width = extractValue(encoded, coords);
        int height = extractValue(encoded, coords);

        BufferedImage secret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        extractSecretPixels(encoded, secret, coords);
        return secret;
    }

    private int getLSB(int colorComponent) {
        return colorComponent & 1;
    }

    private int extractValue(BufferedImage encoded, int[] coords) {
        int value = 0;
        for (int i = 0; i < COUNT_BITS_FOR_STEP; i++) {
            int pixel = encoded.getRGB(coords[0], coords[1]);

            int bitR = getLSB((pixel >> THREE_BYTES) & RGB);
            int bitG = getLSB((pixel >> TWO_BYTES) & RGB);
            int bitB = getLSB(pixel & RGB);

            value = (value << 1) | bitR;
            value = (value << 1) | bitG;
            value = (value << 1) | bitB;

            advanceCursor(encoded, coords);
        }
        return value;
    }

    private void extractSecretPixels(BufferedImage encoded, BufferedImage secret, int[] coords) {
        int sWidth = secret.getWidth();
        int sHeight = secret.getHeight();

        for (int y = 0; y < sHeight; y++) {
            for (int x = 0; x < sWidth; x++) {
                int pixel = encoded.getRGB(coords[0], coords[1]);

                int bit1 = getLSB((pixel >> THREE_BYTES) & RGB);
                int bit2 = getLSB((pixel >> TWO_BYTES) & RGB);
                int bit3 = getLSB(pixel & RGB);

                final int mostImportantBits = 7;
                int grayValue = (bit1 << mostImportantBits) | (bit2 << (mostImportantBits - 1))
                                                           | (bit3 << (mostImportantBits - 2));

                int sPixel = ALPHA_MASK | (grayValue << THREE_BYTES) | (grayValue << TWO_BYTES) | grayValue;

                secret.setRGB(x, y, sPixel);
                advanceCursor(encoded, coords);
            }
        }
    }
}
