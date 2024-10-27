package se.umu.cs.ads.sp.performance;

import se.umu.cs.ads.sp.util.AppSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestLogger {

    private static Path basePath;
    private final static String[] subDirs = {"L1", "L2", "L3", "Raft", "Consistency"};
    public static String L1_LATENCY = "L1-Latency";
    public static String L2_LATENCY = "L2-Latency";
    public static String L3_LEADER_LATENCY = "L3-Leader-Latency";
    public static String L3_FOLLOWER_LATENCY = "L3-Follower-Latency";
    public static String CONSISTENCY = "Consistency";

    private final static String[] files = {
            L1_LATENCY, L2_LATENCY,
            L3_LEADER_LATENCY, L3_FOLLOWER_LATENCY,
            CONSISTENCY
    };
    private static final Map<String, Path> fileMap = new HashMap<>();
    private static Map<Long, ITest> test = new HashMap<>();


    public synchronized static void init(int numPlayers) {
        basePath = Paths.get("spTests");
        String ff = "";
        if(AppSettings.FORCE_L1) {
            ff = "-F-L1";
        } else if(AppSettings.FORCE_L2) {
            ff = "-F-L2";
        } else if(AppSettings.FORCE_L3) {
            ff = "-F-L3";
        }
        try {
            // Create the "performance test" base directory if it doesn't exist
            if (Files.notExists(basePath)) {
                Files.createDirectories(basePath);
            }

            // Loop through subdirectories, create them
            for (int i = 0; i < subDirs.length; i++) {
                Path subDirPath = basePath.resolve(subDirs[i]);

                if (Files.notExists(subDirPath)) {
                    Files.createDirectory(subDirPath);
                }

                for(int j = 0; j < files.length; j++){
                    String fileName = files[i] + "-" + numPlayers + ff + ".txt";

                    if(fileName.startsWith(subDirs[i])){
                        Path filePath = subDirPath.resolve(fileName);
                        System.out.println("\t Adding Test-File -> " + fileName + " to folder -> " + subDirPath);
                        fileMap.put(fileName, filePath);
                        // Create the file if it does not exist
                        if (Files.notExists(filePath)) {
                            Files.createFile(filePath);
                        }
                    }

                }
            }

            System.out.println("\t[Performance Logger] - Directories and files initialized successfully.");
        } catch (IOException e) {
            System.err.println("\t[Performance Logger] - An error occurred during directory and file initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setFinished(long testId) {
        if(test.containsKey(testId)){
            test.get(testId).finish();
        }else{
            System.out.println("\tCould find test");
        }
    }

    public static void newEntry(String folder, String fileName, ITest test) {
        if(fileMap.containsKey(fileName)){
            test.targetFile = fileMap.get(fileName);
            TestLogger.test.put(test.getId(), test);
        }else{
            init(AppSettings.NUM_BOTS);
            test.targetFile = fileMap.get(fileName);
            TestLogger.test.put(test.getId(), test);
        }
    }

    public static ITest getTest(Long id) {
        return test.get(id);
    }

    public static void outputPerformance() {
        for (ITest perf : test.values()) {
            try {
                perf.output();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}