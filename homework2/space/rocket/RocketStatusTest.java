package bg.sofia.uni.fmi.mjt.space.rocket;

import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RocketStatusTest {

    @Test
    void testOutOfValidValues() {
        assertEquals(RocketStatus.STATUS_ACTIVE, RocketStatus.outOf("StatusActive"));
        assertEquals(RocketStatus.STATUS_RETIRED, RocketStatus.outOf("StatusRetired"));
    }

    @Test
    void testOutOfInvalidValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> RocketStatus.outOf("Exploded"));
    }

    @Test
    void testToStringReturnsCorrectValueForEachStatus() {
        assertEquals("StatusRetired", RocketStatus.STATUS_RETIRED.toString());
        assertEquals("StatusActive", RocketStatus.STATUS_ACTIVE.toString());
    }
}
