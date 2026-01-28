package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class MissionTest {
    @Test
    void testMissionOfValidLine() {
        String csvLine = "0,\"SpaceX\",\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",\"Falcon 9 Block 5 | Starlink V1 L9 & BlackSky\",StatusActive,\"50.0\",Success";
        Mission mission = Mission.of(csvLine);

        assertEquals("0", mission.id());
        assertEquals("SpaceX", mission.company());
        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", mission.location());
        assertEquals(LocalDate.of(2020, 8, 7), mission.date());
        assertEquals("Falcon 9 Block 5", mission.detail().rocketName());
        assertEquals(RocketStatus.STATUS_ACTIVE, mission.rocketStatus());
        assertTrue(mission.cost().isPresent());
        assertEquals(50.0, mission.cost().get());
        assertEquals(MissionStatus.SUCCESS, mission.missionStatus());
    }

    @Test
    void testMissionOfWithMissingCost() {
        String csvLine = "1,\"CASC\",\"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China\",\"Thu Aug 06, 2020\",\"Long March 2D | Gaofen-9 04 & Q-SAT\",StatusActive,,Success";
        Mission mission = Mission.of(csvLine);
        assertTrue(mission.cost().isEmpty());
    }
}
