package se.umu.cs.ads.sp;

import se.umu.cs.ads.sp.controller.BotController;

import java.io.IOException;
import java.util.ArrayList;

public class BotHandler {

    public static void initBots(long lobbyId, int nrBots) {
        ArrayList<Process> bots = new ArrayList<>();
        try {
            for (int i = 0; i < nrBots; i++) {
                bots.add(startBotInstance(lobbyId));
            }

            // Add a shutdown hook to destroy all processes when the JVM shuts down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (Process process : bots) {
                    process.destroy(); // Destroy each process
                }
            }));

            for (Process process : bots) {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Process startBotInstance(long lobbyId) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java",   // Command to start Java
                "-cp", "target/StrategicSupremacy-1.0-shaded.jar",  // Path to your JAR file
                "se.umu.cs.ads.sp.controller.BotController", // Main class of your AI controller
                String.valueOf(lobbyId)  // Pass lobbyId as an argument to AI instance
        );

        processBuilder.inheritIO(); // Ensure output is inherited so you can see System.out.print
        return processBuilder.start();
    }

}
