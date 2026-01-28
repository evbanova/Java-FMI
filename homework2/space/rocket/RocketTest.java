package bg.sofia.uni.fmi.mjt.space.rocket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class RocketTest {

    @Test
    void testRocketOfValidLine() {
        String csvLine = "0,\"Falcon 9\",https://en.wikipedia.org/wiki/Falcon_9,70.0 m";
        Rocket rocket = Rocket.of(csvLine);

        assertEquals("0", rocket.id());
        assertEquals("Falcon 9", rocket.name());
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Falcon_9"), rocket.wiki());
        assertEquals(Optional.of(70.0), rocket.height());
    }

    @Test
    void testRocketOfMissingWikiAndHeight() {
        String csvLine = "50,\"Vanguard\",,";
        Rocket rocket = Rocket.of(csvLine);

        assertEquals("Vanguard", rocket.name());
        assertTrue(rocket.wiki().isEmpty());
        assertTrue(rocket.height().isEmpty());
    }

    @Test
    void testRocketOfLineWithQuotesInName() {
        String csvLine = "12,\"Long March 2D\",https://wiki.com,41.06 m";
        Rocket rocket = Rocket.of(csvLine);

        assertEquals("Long March 2D", rocket.name());
        assertEquals(41.06, rocket.height().get());
    }
}
