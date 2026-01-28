package bg.sofia.uni.fmi.mjt.space.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CipherExceptionTest {

    @Test
    void testCipherException() {
        assertNull(new CipherException().getMessage());
        assertEquals("msg", new CipherException("msg").getMessage());
        assertEquals("cause", new CipherException("msg", new RuntimeException("cause")).getCause().getMessage());
    }

}
