package bg.sofia.uni.fmi.mjt.steganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

//this class takes images from the buffer, embeds them and stores them in the output directory
// or extracts the image
//this is parent class that takes care of the threads
public abstract class Consumer implements Runnable {
    protected final SharedBuffer buffer;
    protected final String outputDirectory;

    protected static final int RGB = 0xFF;
    protected static final int TWO_BYTES = 8;
    protected static final int THREE_BYTES = 16;
    protected static final int ALPHA_MASK = 0xFF000000;
    protected static final int TOTAL_BITS = 12;
    protected static final int COUNT_BITS_FOR_STEP = 4;
    protected static final int THREE_PIXELS = 3;

    public Consumer(SharedBuffer buffer, String outputDirectory) {
        this.buffer = buffer;
        this.outputDirectory = outputDirectory;
    }

    //function given in the task
    public void saveImage(BufferedImage image, String outputDir, String name) {
        try {
            ImageIO.write(image, "png", new File(outputDir, name));
            System.out.println("Saved " + name + " to " + outputDir);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("While saving image %s", name), e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                //waiting until there are images in the buffer
                SteganographyImage image = buffer.get();

                //checks if all the producer threads are finished
                if (image == null) {
                    break;
                }

                BufferedImage result = processTask(image);
                saveImage(result, outputDirectory, image.fileName());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected int getBit(int value, int position, int totalBits) {
        return (value >> (totalBits - 1 - position)) & 1;
    }

    protected void advanceCursor(BufferedImage image, int[] coordinates) {
        coordinates[0]++;
        if (coordinates[0] >= image.getWidth()) {
            coordinates[0] = 0;
            coordinates[1]++;
        }
    }

    protected abstract BufferedImage processTask(SteganographyImage image);
}
