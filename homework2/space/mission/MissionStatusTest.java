package bg.sofia.uni.fmi.mjt.space.mission;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MissionStatusTest {

    @Test
    void testOutOfValidValues() {
        assertEquals(MissionStatus.SUCCESS, MissionStatus.outOf("Success"));
        assertEquals(MissionStatus.FAILURE, MissionStatus.outOf("  failure  "));
        assertEquals(MissionStatus.PARTIAL_FAILURE, MissionStatus.outOf("Partial Failure"));
        assertEquals(MissionStatus.PRELAUNCH_FAILURE, MissionStatus.outOf("Prelaunch Failure"));
    }

    @Test
    void testOutOfInvalidValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> MissionStatus.outOf("Unknown Status"));
    }

    @Test
    void testToStringReturnsCorrectValueForEachStatus() {
        assertEquals("Success", MissionStatus.SUCCESS.toString());
        assertEquals("Failure", MissionStatus.FAILURE.toString());
        assertEquals("Partial Failure", MissionStatus.PARTIAL_FAILURE.toString());
        assertEquals("Prelaunch Failure", MissionStatus.PRELAUNCH_FAILURE.toString());
    }
}
