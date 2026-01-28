package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        if (missionsReader == null) {
            throw new IllegalArgumentException("Mission reader cannot be null");
        }
        this.missions = loadMissions(missionsReader);

        if (rocketsReader == null) {
            throw new IllegalArgumentException("Rocket reader cannot be null");
        }
        this.rockets = loadRockets(rocketsReader);

        if (secretKey == null) {
            throw new IllegalArgumentException("Secret key cannot be null");
        }
        this.secretKey = secretKey;
    }

    private List<Mission> loadMissions(Reader reader) {
        try (var bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                    .skip(1) // Skip the CSV header row
                    .map(Mission::of)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load missions from reader", e);
        }
    }

    private List<Rocket> loadRockets(Reader reader) {
        try (var bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                    .skip(1) // Skip the CSV header row
                    .map(Rocket::of)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load rocketsfrom reader", e);
        }
    }

    /**
     * Returns all missions in the dataset.
     * If there are no missions, return an empty collection.
     */
    @Override
    public Collection<Mission> getAllMissions() {
        return missions.isEmpty() ? new ArrayList<>() : missions;
    }

    /**
     * Returns all missions in the dataset with a given status.
     * If there are no missions, return an empty collection.
     *
     * @param missionStatus the status of the missions
     * @throws IllegalArgumentException if missionStatus is null
     */
    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null");
        }

        return missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus)
                .collect(Collectors.toList());
    }

    /**
     * Returns the company with the most successful missions in a given time period.
     * Success is defined as MissionStatus.SUCCESS.
     * If multiple companies have the same number of successful missions, return any of them.
     * If there are no successful missions in the period, return an empty string.
     * If there are no missions at all, return an empty string.
     *
     * @param from the inclusive beginning of the time frame
     * @param to   the inclusive end of the time frame
     * @throws IllegalArgumentException   if from or to is null
     * @throws TimeFrameMismatchException if to is before from
     */
    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From or to date cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("Missions cannot be before from");
        }

        return missions.stream()
                .filter(m -> m.missionStatus() == MissionStatus.SUCCESS)
                .filter(m -> !m.date().isBefore(from) && !m.date().isAfter(to))
                .collect(Collectors.groupingBy(
                        Mission::company,
                        Collectors.counting())).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("");
    }

    /**
     * Groups missions by country.
     * If there are no missions, return an empty map.
     */
    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream().collect(Collectors.groupingBy(
                m -> getCountry(m.location()),
                Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Helper function for getMissionsPerCountry for csv logic
     *
     * @param location the whole string location for the CSV line
     * @return the country - the last field of each location  - by the data from the CSV files
     */
    private String getCountry(String location) {
        String[] fields = location.split(",");
        return fields[fields.length - 1].trim();
    }

    /**
     * Returns the top N least expensive missions, ordered from cheapest to more expensive.
     * If there are no missions, return an empty list.
     *
     * @param n             the number of missions to be returned
     * @param missionStatus the status of the missions
     * @param rocketStatus  the status of the rockets
     * @throws IllegalArgumentException if n is less than or equal to 0, missionStatus or rocketStatus is null
     */
    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("N cannot be less than or equal 0");
        }

        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Mission status or rocket status cannot be null");
        }

        return missions.stream()
                .filter(m -> m.missionStatus() == missionStatus)
                .filter(m -> m.rocketStatus() == rocketStatus)
                .filter(m -> m.cost().isPresent()) //Optional check
                .sorted(Comparator.comparingDouble(m -> m.cost().get()))
                .limit(n).toList();
    }

    /**
     * Returns the most desired location for each company.
     * Most desired = location with the highest number of missions for that company.
     * Location is defined as the value in the "Location" column (e.g., "Kennedy Space Center, FL, USA").
     * If a company has multiple locations with the same count, return any of them.
     * If there are no missions, return an empty map.
     *
     * @return a map where keys are company names and values are their most used mission locations
     */
    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        //first grouping by company name and then location with highest number of appearances

        return missions.stream().collect(Collectors.groupingBy(Mission::company,
                Collectors.collectingAndThen(
                        Collectors.groupingBy(Mission::location, Collectors.counting()),
                        locationCounts -> locationCounts.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey).orElse(""))));
    }

    /**
     * Returns the location with most successful missions for each company in a given time period.
     * Successful = MissionStatus.SUCCESS.
     * For each company, finds the location where that company had the most successful missions.
     * If a company has multiple locations with the same count of successful missions, return any of them.
     * If a company has no successful missions in the period, it is NOT included in the result.
     * If there are no missions at all, return an empty map.
     *
     * @param from the inclusive beginning of the time frame (inclusive)
     * @param to   the inclusive end of the time frame (inclusive)
     * @return a map where keys are company names and values are their locations with
     * most successful missions in the period
     * @throws IllegalArgumentException   if from or to is null
     * @throws TimeFrameMismatchException if to is before from
     */
    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From or to date cannot be null");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("Missions cannot be before from");
        }

        return missions.stream()
                .filter(m -> m.missionStatus() == MissionStatus.SUCCESS)
                .filter(m -> !m.date().isBefore(from) && !m.date().isAfter(to))
                .collect(Collectors.groupingBy(Mission::company,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Mission::location, Collectors.counting()),
                                locationCounts -> locationCounts.entrySet().stream()
                                        .max(Map.Entry.comparingByValue())
                                        .map(Map.Entry::getKey))))
                .entrySet().stream()
                //removing companies with no successful missions
                .filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().get()));
    }

    /**
     * Returns all rockets in the dataset.
     * If there are no rockets, return an empty collection.
     */
    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets.isEmpty() ? new ArrayList<>() : rockets;
    }

    /**
     * Returns the top N tallest rockets, in decreasing order.
     * If there are no rockets, return an empty list.
     *
     * @param n the number of rockets to be returned
     * @throws IllegalArgumentException if n is less than or equal to 0
     */
    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N cannot be less than or equal 0");
        }

        return rockets.stream()
                .filter(r -> r.height().isPresent())
                .sorted(Comparator.<Rocket>comparingDouble(r -> r.height().get()).reversed())
                .limit(n).toList();
    }

    /**
     * Returns a mapping of rockets (by name) to their respective wiki page (if present).
     * If there are no rockets, return an empty map.
     */
    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        //not filtering for rockets who have a wiki page
        //because the return type is Optional
        return rockets.stream().collect(Collectors.toMap(
                Rocket::name, Rocket::wiki));
    }

    /**
     * Returns the wiki pages for the rockets used in the N most expensive missions.
     * If there are no missions, return an empty list.
     *
     * @param n             the number of wiki pages to be returned
     * @param missionStatus the status of the missions
     * @param rocketStatus  the status of the rockets
     * @throws IllegalArgumentException if n is less than or equal to 0, or missionStatus or rocketStatus is null
     */
    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException("N cannot be less than or equal 0");
        }

        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Mission status or rocket status cannot be null");
        }

        List<String> rocketNames = missions.stream()
                .filter(m -> m.missionStatus() == missionStatus)
                .filter(m -> m.rocketStatus() == rocketStatus)
                .filter(m -> m.cost().isPresent()) //Optional check
                .sorted(Comparator.<Mission>comparingDouble(m -> m.cost().get()).reversed())
                .map(Mission::detail).map(Detail::rocketName).toList();

        //using HashSet because the list of names is large and checking manually will be very slow
        //using set will ensure non-duplicating names and hash code will ensure fast equals check
        Set<String> nameSet = new HashSet<>(rocketNames);

        return rockets.stream()
                .filter(r-> nameSet.contains(r.name()))
                .filter(r -> r.wiki().isPresent())
                .map(r -> r.wiki().get())
                //limiting here, not when working with the missions, so the returned wiki pages are N
                .limit(n).toList();
    }

    /**
     * Saves the name of the most reliable rocket in a given time period in an encrypted format.
     *
     * <p><b>Important:</b> The implementation is expected to wrap {@code outputStream} in a
     * {@link javax.crypto.CipherOutputStream}. Since block ciphers (e.g. AES) write the final block
     * only on {@code close()}, this method <b>must close</b> the stream after writing.
     *
     * @param outputStream the output stream where the encrypted result is written into;
     *                     it will be closed by this method
     * @param from         the inclusive beginning of the time frame
     * @param to           the inclusive end of the time frame
     * @throws IllegalArgumentException   if outputStream, from or to is null
     * @throws CipherException            if the encrypt/decrypt operation cannot be completed successfully
     * @throws TimeFrameMismatchException if to is before from
     */
    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to)
            throws CipherException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream cannot be null");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("From or to date cannot be null");
        }
        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException("Missions cannot be before from");
        }

        String rocketName = getMostReliableRocketName(from, to);
        // convert the name to InputStream, because Rijndael class needs it
        byte[] nameBytes = rocketName.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        try (InputStream inputStream = new ByteArrayInputStream(nameBytes); outputStream) {
            Rijndael rijndael = new Rijndael(this.secretKey);

            rijndael.encrypt(inputStream, outputStream);
        } catch (IOException e) {
            throw new CipherException("Failed to process the output stream", e);
        }
    }

    /**
     * Helper method for saveMostReliableRocket to separate encryptic logic and stream logic
     *
     * @param from the inclusive beginning of the time frame
     * @param to the inclusive end of the time frame
     * @return the name of the most reliable rocket
     */
    private String getMostReliableRocketName(LocalDate from, LocalDate to) {

        return missions.stream()
                .filter(m -> !m.date().isBefore(from) && !m.date().isAfter(to))
                .collect(Collectors.groupingBy(m -> m.detail().rocketName()))
                .entrySet().stream()
                .max(Comparator.comparingDouble(this::rocketReliability))
                .map(Map.Entry::getKey).orElse("");
    }

    /**
     * Helper method for getMostReliableRocketName to separate stream logic and calculating
     *
     * @param entry contains a rocket name as a key and the list of missions this rocket has as value
     * @return reliability of the rocket
     */
    private double rocketReliability(Map.Entry<String, List<Mission>> entry) {
        List<Mission> rocketMissions = entry.getValue();

        long successfulCount = rocketMissions.stream()
                .filter(m -> m.missionStatus() == MissionStatus.SUCCESS)
                .count();

        long unsuccessfulCount = rocketMissions.stream()
                .filter(m -> ((m.missionStatus() == MissionStatus.FAILURE) ||
                                      (m.missionStatus() == MissionStatus.PARTIAL_FAILURE) ||
                                      (m.missionStatus() == MissionStatus.PRELAUNCH_FAILURE)))
                .count();

        return (successfulCount + unsuccessfulCount == 0) ? 0.0
                : (double) (2 * successfulCount + unsuccessfulCount) /
                        (2 * (successfulCount + unsuccessfulCount));
    }
}
