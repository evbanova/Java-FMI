package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        FileSystemImageManager fsImageManager = new LocalFileSystemImageManager();

        BufferedImage image = null;
        try {
            image = fsImageManager.loadImage(new File("C:\\Users\\User\\Documents\\uni\\Java\\src\\bg\\sofia\\uni\\fmi\\mjt\\imagekit\\kitten.png"));
        } catch (IOException e) {
            System.err.println("File I/O Error: " + e.getMessage());
        }

        ImageAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);

        ImageAlgorithm sobelEdgeDetection = new SobelEdgeDetection(grayscaleAlgorithm);
        BufferedImage edgeDetectedImage = sobelEdgeDetection.process(image);

        try {
            File f1 = new File("C:\\Users\\User\\Documents\\uni\\Java\\src\\bg\\sofia\\uni\\fmi\\mjt\\imagekit\\kitten-grayscale.png");
            fsImageManager.saveImage(grayscaleImage, f1);
        } catch (IOException e1) {
            System.err.println("File I/O Error: " + e1.getMessage());
        }
        try {
            File f2 =new File("C:\\Users\\User\\Documents\\uni\\Java\\src\\bg\\sofia\\uni\\fmi\\mjt\\imagekit\\kitten-edge-detected.png");
            fsImageManager.saveImage(edgeDetectedImage, f2);
        } catch (IOException e2) {
            System.err.println("File I/O Error: " + e2.getMessage());
        }
    }
}
