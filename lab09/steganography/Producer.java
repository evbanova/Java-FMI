package bg.sofia.uni.fmi.mjt.steganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

//this class loads images from the memory and safes them in the SharedBuffer (producer for embedding)
// or as a producer for extracting -
//if the producer is extracting not embedding the secret picture is null
//using only this class to avoid repeating code
public class Producer implements Runnable {
    private final Path coverPath;
    private final Path secretPath;
    private final SharedBuffer buffer;

    public Producer(Path coverPath, Path secretPath, SharedBuffer buffer) {
        this.coverPath = coverPath;
        this.secretPath = secretPath;
        this.buffer = buffer;
    }

    public Producer(Path coverPath, SharedBuffer buffer) {
        this.coverPath = coverPath;
        this.secretPath = null;
        this.buffer = buffer;
    }

    //function given in the task
    public BufferedImage loadImage(Path imagePath) {
        try {
            return ImageIO.read(imagePath.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }

    private boolean validateSizes(BufferedImage cover, BufferedImage secret) {
        final int difference = 8;
        return cover.getWidth() * cover.getHeight() >=
                (secret.getWidth() * secret.getHeight() + difference);
    }

    //the images loaded from the memory are stored in a SteganographyImage,
    //which is then safed in the SharedBuffer
    @Override
    public void run() {
        try {
            BufferedImage cover = loadImage(coverPath);
            //if the producer is extracting not embedding the secret picture is null
            BufferedImage secret = (secretPath != null) ? loadImage(secretPath) : null;

            if (validateSizes(cover, secret)) {
                buffer.put(new SteganographyImage(cover, secret, coverPath.getFileName().toString()));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
