package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String missionsPath = "C:\\Users\\User\\Documents\\uni\\Java\\src\\bg\\sofia\\uni\\fmi\\mjt\\space\\all-missions-from-1957.csv";
        String rocketsPath = "C:\\Users\\User\\Documents\\uni\\Java\\src\\bg\\sofia\\uni\\fmi\\mjt\\space\\all-rockets-from-1957.csv";

        try {
            // 1. Setup Security
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();

            // 2. Initialize Scanner
            System.out.println("--- Initializing Space Scanner ---");
            try (Reader missionsReader = new FileReader(missionsPath);
                 Reader rocketsReader = new FileReader(rocketsPath)) {

                MJTSpaceScanner scanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);

                // 3. Test Basic Loading
                Collection<Mission> allMissions = scanner.getAllMissions();
                Collection<Rocket> allRockets = scanner.getAllRockets();
                System.out.println("Loaded " + allMissions.size() + " missions.");
                System.out.println("Loaded " + allRockets.size() + " rockets.");

                // 4. Test Statistics Logic
                System.out.println("\n--- Testing Statistics ---");
                LocalDate from = LocalDate.of(2010, 1, 1);
                LocalDate to = LocalDate.of(2020, 12, 31);

                String topCompany = scanner.getCompanyWithMostSuccessfulMissions(from, to);
                System.out.println("Company with most success (2010-2020): " + topCompany);

                Map<String, String> desiredLocations = scanner.getMostDesiredLocationForMissionsPerCompany();
                System.out.println("SpaceX most desired location: " + desiredLocations.get("SpaceX"));

                List<Mission> leastexp = scanner.getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
                System.out.println(leastexp.get(0).location());
                System.out.println(leastexp.get(1).location());

                // 5. Test Encryption (Reliability)
                System.out.println("\n--- Testing Encryption ---");
                String encryptedFileName = "most_reliable_rocket.enc";

                try (OutputStream os = new FileOutputStream(encryptedFileName)) {
                    scanner.saveMostReliableRocket(os, from, to);
                }
                System.out.println("Encrypted data saved to " + encryptedFileName);

                // 6. Verify Encryption by Decrypting
                verifyEncryption(encryptedFileName, secretKey);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void verifyEncryption(String filename, SecretKey key) throws Exception {
        Rijndael rijndael = new Rijndael(key);

        try (InputStream is = new FileInputStream(filename);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            rijndael.decrypt(is, baos);
            String decryptedName = baos.toString(StandardCharsets.UTF_8);
            System.out.println("Decrypted Rocket Name: " + decryptedName);
        }
    }
}

