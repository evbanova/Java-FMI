package bg.sofia.uni.fmi.mjt.space.algorithm;

import org.junit.jupiter.api.Test;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class RijndaelTest {
    @Test
    void testEncryptDecryptCycle() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        Rijndael rijndael = new Rijndael(secretKey);

        String original = "Falcon 9";
        ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        rijndael.encrypt(new ByteArrayInputStream(original.getBytes(StandardCharsets.UTF_8)), encryptedOut);

        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();
        rijndael.decrypt(new ByteArrayInputStream(encryptedOut.toByteArray()), decryptedOut);

        assertEquals(original, decryptedOut.toString(StandardCharsets.UTF_8));
    }

    @Test
    void testConstructorThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> new Rijndael(null));
    }
}
