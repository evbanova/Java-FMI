package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MJTSpaceScannerTest {
    private MJTSpaceScanner scanner;
    private SecretKey secretKey;

    private final static String missionsCsv = """
            Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket, Rocket,Status Mission
            0,SpaceX,"USA","Fri Aug 07, 2020",Falcon 9 | Starlink,StatusActive,50.0,Success
            1,SpaceX,"USA","Sat Aug 08, 2020",Falcon 9 | Starlink,StatusActive,50.0,Success
            2,Roscosmos,"Kazakhstan","Fri Aug 07, 2020",Soyuz | Progress,StatusRetired,30.0,Success
            3,NASA,"USA","Fri Aug 07, 2020",SLS | Artemis,StatusActive,100.0,Failure
            """;

    private final static String rocketsCsv = """
            "",Name,Wiki,Rocket Height
            0,Falcon 9,https://en.wikipedia.org/wiki/Falcon_9,70.0 m
            1,Soyuz,https://en.wikipedia.org/wiki/Soyuz,50.0 m
            2,SLS,,111.0 m
            """;

    @BeforeEach
    void setUp() throws Exception {
        secretKey = KeyGenerator.getInstance("AES").generateKey();
        scanner = new MJTSpaceScanner(new StringReader(missionsCsv), new StringReader(rocketsCsv), secretKey);
    }

    @Test
    void testGetAllMissions() {
        assertEquals(4, scanner.getAllMissions().size());
        assertEquals(3, scanner.getAllMissions(MissionStatus.SUCCESS).size());
    }

    @Test
    void testGetAllMissionsNullMissionStatus() {
        assertThrows(IllegalArgumentException.class, () -> scanner.getAllMissions(null));
    }

    @Test
    void testGetAllRockets() {
        assertEquals(3, scanner.getAllRockets().size());
    }


    @Test
    void testGetMissionsPerCountry() {
        Map<String, Collection<Mission>> perCountry = scanner.getMissionsPerCountry();
        assertTrue(perCountry.containsKey("USA"));
        assertEquals(3, perCountry.get("USA").size());
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() throws Exception {
        String company = scanner.getCompanyWithMostSuccessfulMissions(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 12, 31));

        assertTrue(company.equals("SpaceX") || company.equals("NASA"));
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        List<Mission> missions = scanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
        assertEquals(1, missions.size());
        assertEquals("SpaceX", missions.get(0).company());
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNullArguments() {
        assertThrows(IllegalArgumentException.class, () ->
                scanner.getTopNLeastExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));

        assertThrows(IllegalArgumentException.class, () ->
                scanner.getTopNLeastExpensiveMissions(1, null, RocketStatus.STATUS_ACTIVE));

        assertThrows(IllegalArgumentException.class, () ->
                scanner.getTopNLeastExpensiveMissions(1, MissionStatus.SUCCESS, null));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsThrowsException() {
        assertThrows(TimeFrameMismatchException.class, () ->
                scanner.getCompanyWithMostSuccessfulMissions(LocalDate.now(), LocalDate.now().minusDays(1)));

        assertThrows(IllegalArgumentException.class, () ->
                scanner.getCompanyWithMostSuccessfulMissions(null, null));
    }

    @Test
    void testGetTopNTallestRockets() {
        var rockets = scanner.getTopNTallestRockets(1);
        assertEquals(1, rockets.size());
        assertEquals("SLS", rockets.get(0).name());
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> locations = scanner.getMostDesiredLocationForMissionsPerCompany();
        assertEquals("USA", locations.get("NASA"));
    }

    @Test
    void testSaveMostReliableRocket() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);

        scanner.saveMostReliableRocket(out, from, to);
        assertTrue(out.toByteArray().length > 0);
    }

    @Test
    void testSaveMostReliableRocketNullArguments() throws Exception {
        assertThrows(IllegalArgumentException.class, () ->
                scanner.saveMostReliableRocket(null,null, LocalDate.now()));

        assertThrows(TimeFrameMismatchException.class, () ->
                scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.now(), LocalDate.now().minusDays(1)));

    }

    @Test
    void testValidationExceptions() {
        assertThrows(IllegalArgumentException.class, () -> new MJTSpaceScanner(null, null, null));
        assertThrows(TimeFrameMismatchException.class, () ->
                scanner.getCompanyWithMostSuccessfulMissions(LocalDate.now(), LocalDate.now().minusDays(1)));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanySuccess() throws Exception {
        LocalDate from = LocalDate.of(2020, 8, 1);
        LocalDate to = LocalDate.of(2020, 8, 31);

        Map<String, String> result = scanner.getLocationWithMostSuccessfulMissionsPerCompany(from, to);

        assertEquals("USA", result.get("SpaceX"));
        assertEquals(2, result.size());
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () ->
                        scanner.getLocationWithMostSuccessfulMissionsPerCompany(null, LocalDate.now()));

        assertThrows(TimeFrameMismatchException.class, () ->
                scanner.getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.now(), LocalDate.now().minusDays(1)));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsSuccess() {
        List<String> wikis = scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(
                1, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(1, wikis.size());
        assertEquals("https://en.wikipedia.org/wiki/Falcon_9", wikis.get(0));
    }

    @Test
    void testGetWikiPagesValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                        scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE));

        assertThrows(IllegalArgumentException.class, () ->
                        scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(1, null, RocketStatus.STATUS_ACTIVE));

        assertThrows(IllegalArgumentException.class, () ->
                scanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(1, MissionStatus.SUCCESS, null));
    }
}