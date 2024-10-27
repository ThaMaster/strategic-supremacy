package se.umu.cs.ads.sp.performance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestLogger {

    private static Path basePath;
    private final static String[] subDirs = {"L1", "L2", "L3", "Raft", "Consistency"};
    public static String L1_LATENCY = "L1-Latency.txt";
    public static String L2_LATENCY = "L2-Latency.txt";
    public static String L3_LEADER_LATENCY = "L3-Leader-Latency.txt";
    public static String L3_FOLLOWER_LATENCY = "L3-Follower-Latency.txt";
    public static String CONSISTENCY = "Consistency.txt";

    private final static String[] files = {
            L1_LATENCY, L2_LATENCY,
            L3_LEADER_LATENCY, L3_FOLLOWER_LATENCY,
            CONSISTENCY
    };
    private static final Map<String, Path> fileMap = new HashMap<>();
    private static Map<Long, ITest> test = new HashMap<>();


    public static void init(String path) {
        basePath = Paths.get("spTests");
        if (!path.equals("true")) {
            basePath = Paths.get(path, "spTests");
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
            }

            System.out.println("\t[Performance Logger] - Directories and files initialized successfully.");
        } catch (IOException e) {
            System.err.println("\t[Performance Logger] - An error occurred during directory and file initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setFinished(long testId) {
        test.get(testId).finish();
    }

    public static void newEntry(String folder, String fileName, ITest test) {
        try {
            Path subDirPath = basePath.resolve(folder);

            if (Files.notExists(subDirPath)) {
                Files.createDirectory(subDirPath);
            }

            Path filePath = subDirPath.resolve(fileName);
            fileMap.put(fileName, filePath);

            // Create the file if it does not exist
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }

            test.setTargetFile(filePath);
            TestLogger.test.put(test.getId(), test);
        } catch (IOException e) {
            e.printStackTrace();
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