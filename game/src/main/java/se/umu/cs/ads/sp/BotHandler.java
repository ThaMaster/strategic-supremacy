package se.umu.cs.ads.sp;

import java.io.IOException;
import java.util.ArrayList;

public class BotHandler {

    public static void startLeaderBot(int nrBots, String forceFlag, int mapIndex, boolean runTests, boolean testAll) {
        try {
            Process leaderBot = startBotInstance(-1, true, forceFlag, nrBots, mapIndex, runTests, testAll);
            Runtime.getRuntime().addShutdownHook(new Thread(leaderBot::destroy));
            try {
                leaderBot.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initBots(long lobbyId, int nrBots, String forceFlag, boolean runTests, boolean noLeader) {
        ArrayList<Process> bots = new ArrayList<>();
        try {
            for (int i = 0; i < nrBots; i++) {
                bots.add(startBotInstance(lobbyId, false, forceFlag, nrBots, -1, runTests, false));
                Thread.sleep(250);
            }

            // Add a shutdown hook to destroy all processes when the JVM shuts down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (Process process : bots) {
                    process.destroy(); // Destroy each process
                }
            }));
            if (noLeader) {
                for (Process process : bots) {
                    try {
                        process.waitFor();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Process startBotInstance(long lobbyId, boolean isLeader, String forceFlag, int nrBots, int mapIndex, boolean runTests, boolean testAll) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java",   // Command to start Java
                "-cp", "target/StrategicSupremacy-1.0-shaded.jar",  // Path to your JAR file
                "se.umu.cs.ads.sp.controller.BotController", // Main class of your AI controller
                String.valueOf(lobbyId),
                String.valueOf(isLeader),
                forceFlag,
                String.valueOf(nrBots),
                String.valueOf(mapIndex),
                String.valueOf(runTests),
                String.valueOf(testAll)
        );

        processBuilder.inheritIO(); // Ensure output is inherited so you can see System.out.print
        return processBuilder.start();
    }

}
