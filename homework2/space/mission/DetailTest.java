package bg.sofia.uni.fmi.mjt.space.mission;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DetailTest {

    @Test
    void testDetailParseStandard() {
        String rawDetail = "Falcon 9 Block 5 | Starlink V1 L9";
        Detail detail = Detail.parse(rawDetail);

        assertEquals("Falcon 9 Block 5", detail.rocketName());
        assertEquals("Starlink V1 L9", detail.payload());
    }

    @Test
    void testDetailParseWithExtraWhitespace() {
        String rawDetail = "  Ariane 5  |   Eutelsat 7C  ";
        Detail detail = Detail.parse(rawDetail);

        assertEquals("Ariane 5", detail.rocketName());
        assertEquals("Eutelsat 7C", detail.payload());
    }
}