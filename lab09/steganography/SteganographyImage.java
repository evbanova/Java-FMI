package bg.sofia.uni.fmi.mjt.steganography;

import java.awt.image.BufferedImage;

public record SteganographyImage(BufferedImage cover, BufferedImage secret, String fileName) {

}
