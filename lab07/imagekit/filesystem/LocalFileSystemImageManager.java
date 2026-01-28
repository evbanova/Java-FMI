package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LocalFileSystemImageManager implements FileSystemImageManager {

    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Arrays.asList("png", "jpg", "bmp"));

    public LocalFileSystemImageManager() {}

    private static boolean supported(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
           return false;
        }

        return SUPPORTED_EXTENSIONS.contains(fileName.substring(fileName.lastIndexOf('.') + 1));
    }

    public BufferedImage loadImage(File imageFile) throws IOException{
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file is null");
        }

        if (!imageFile.exists() || !imageFile.isFile() || !supported(imageFile.getName())) {
            throw new IOException("Image file does not exists, is not a file, or is not one of the supported formats.");
        }

        return ImageIO.read(imageFile);
    }

    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException{
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Images directory is null");
        }

        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("Images directory does not exists or is not a file");
        }

        File[] files = imagesDirectory.listFiles();
        List<BufferedImage> images = new ArrayList<BufferedImage>();
        for (File file : files) {
            if (!supported(file.getName())) {
                throw new IOException("Image file is not one of the supported formats.");
            }
            images.add(ImageIO.read(file));
        }

        return images;
    }

    public void saveImage(BufferedImage image, File imageFile) throws IOException{
        if (image == null || imageFile == null) {
            throw new IllegalArgumentException("Image or file is null");
        }

        if (imageFile.exists() || !imageFile.getParentFile().exists()) {
            throw new IOException("File already exists or its parent directory does not exists");
        }

        ImageIO.write(image, "png", imageFile);
    }
}
