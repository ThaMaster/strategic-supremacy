package se.umu.cs.ads.sp.PerformanceTest;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class PerformanceLogger {

    private static Path basePath;

    private final static String[] subDirs = {"L1", "L2", "L3", "Raft"};

    private final static String[] files = {
            "L1-Follower.txt", "L1-Leader.txt",
            "L2-Follower.txt", "L2-Leader.txt",
            "L3-Follower.txt", "L3-Leader.txt",
            "Leader-Election.txt"
    };
    private static final Map<String, Path> fileMap = new HashMap<>();

    private static Map<Long, PerformanceTest> performaceTest = new HashMap<>();


    public static String L1_FOLLOWER = "L1-Follower.txt";
    public static String L1_LEADER = "L2-Follower.txt";

    public static void init(String path) {
        basePath = Paths.get("performance test");
        if (!path.equals("true")) {
            basePath = Paths.get(path, "performance test");
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

    public static void newEntry(String file, PerformanceTest test){
        Path targetFile = fileMap.get(file);
        if (targetFile == null) {
            System.err.println("\t[Performance Logger] - File " + file + " not found in the structure.");
        }
        else{
            test.setPath(targetFile);
            performaceTest.put(test.getId(), test);
        }
    }

    public static void outputPerformance() {
        for(PerformanceTest perf : performaceTest.values()){
            try{
                perf.output();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public static void setFinished(long id){
        performaceTest.get(id).finish();
    }

}