package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.InputStream;
import java.io.OutputStream;

public class Rijndael implements SymmetricBlockCipher {
    private static final String ALGORITHM = "AES";
    private static final int BUFFER_SIZE = 1024;
    private final SecretKey secretKey;

    /**
     * Encrypts/decrypts data using AES (Rijndael) algorithm with the provided secret key.
     *
     * @param secretKey the encryption/decryption key
     * @throws IllegalArgumentException if secretKey is null
     */
    public Rijndael(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key can't be null");
        }

        this.secretKey = secretKey;
    }

    /**
     * Encrypts the data from inputStream and puts it into outputStream
     *
     * @param inputStream  the input stream where the data is read from
     * @param outputStream the output stream where the encrypted result is written into
     * @throws CipherException if the encrypt/decrypt operation cannot be completed successfully
     */
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        processData(inputStream, outputStream, Cipher.ENCRYPT_MODE);
    }

    /**
     * Decrypts the data from inputStream and puts it into outputStream
     *
     * @param inputStream  the input stream where the data is read from
     * @param outputStream the output stream where the decrypted result is written into
     * @throws CipherException if the encrypt/decrypt operation cannot be completed successfully
     */
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        processData(inputStream, outputStream, Cipher.DECRYPT_MODE);
    }

    /**
     * Helper method to handle both encryption and decryption to avoid code duplication.
     */
    private void processData(InputStream inputStream, OutputStream outputStream, int cipherMode)
            throws CipherException {
        //using try-cath block so we are sure the CipherOutputStream (OutputStream) calls close()
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(cipherMode, secretKey);

            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                throw new CipherException("Error during " +
                        (cipherMode == Cipher.ENCRYPT_MODE ? "encryption" : "decryption"), e);
            }
        } catch (Exception e) {
            throw new CipherException("Error during " +
                    (cipherMode == Cipher.ENCRYPT_MODE ? "encryption" : "decryption"), e);
        }
    }
}
