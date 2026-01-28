package bg.sofia.uni.fmi.mjt.space.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TimeFrameMismatchExceptionTest {
    @Test
    void testTimeFrameMismatchException() {
        assertNull(new TimeFrameMismatchException().getMessage());
        assertEquals("time", new TimeFrameMismatchException("time").getMessage());
        assertEquals("cause", new TimeFrameMismatchException("msg", new RuntimeException("cause")).getCause().getMessage());

    }
}
