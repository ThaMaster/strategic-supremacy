package se.umu.cs.ads.sp.performance;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class TestLogger {

    private static Path basePath;
    private final static String[] subDirs = {"L1", "L2", "L3", "Raft", "Consistency"};
    public static String L1_FOLLOWER = "L1-Follower.txt";
    public static String L1_LEADER = "L2-Follower.txt";
    public static String L3_LEADER = "L3-Leader.txt";
    public static String L3_FOLLOWER = "L3-Follower.txt";

    public static String CONSISTENCY = "Consistency.txt";
    private final static String[] files = {
            L1_FOLLOWER, L1_LEADER,
            "L2-Follower.txt", "L2-Leader.txt",
            "L3-Follower.txt", L3_LEADER,
            "Leader-Election.txt", CONSISTENCY
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

            // Loop through subdirectories, create them, and add files to the map
            for (int i = 0; i < subDirs.length; i++) {
                Path subDirPath = basePath.resolve(subDirs[i]);

                if (Files.notExists(subDirPath)) {
                    Files.createDirectory(subDirPath);
                }

                // Map each required file to its path in the fileMap
                for (String fileName : files) {
                    if (fileName.startsWith(subDirs[i])) {
                        Path filePath = subDirPath.resolve(fileName);
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

    public static void setFinished(long testId){
        test.get(testId).finish();
    }

    public static void newEntry(String file, ITest test) {
        Path targetFile = fileMap.get(file);
        test.setTargetFile(targetFile);
        TestLogger.test.put(test.getId(), test);
    }

    public static ITest getTest(Long id){
        return test.get(id);
    }

    public static void outputPerformance(){
        for(ITest perf : test.values()){
            try{
                perf.output();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}