package se.umu.cs.ads.sp;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.performance.TestLogger;
import se.umu.cs.ads.sp.util.AppSettings;
import se.umu.cs.ads.sp.util.ArgParser;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        AppSettings.SetGameConfig();

        ArgParser parser = new ArgParser(args);

        if (parser.hasFlag("-h") || parser.hasFlag("--help")) {
            printUsage();
            System.exit(0);
        }

        boolean startGameController = false;

        if (parser.hasFlag("-d") || parser.hasFlag("--debug")) {
            AppSettings.DEBUG = true;
        }

        String forceFlag = "";
        if (parser.hasFlag("-l1")) {
            AppSettings.FORCE_L1 = true;
            forceFlag = "-l1";
        } else if (parser.hasFlag(("-l2"))) {
            AppSettings.FORCE_L2 = true;
            forceFlag = "-l2";
        } else if (parser.hasFlag("-l3")) {
            AppSettings.FORCE_L3 = true;
            forceFlag = "-l3";
        }

        int nrBots = 2; // DEFAULT BOTS ARE 2
        if (parser.hasFlag("-b")) {
            nrBots = Integer.parseInt(parser.getValue("-b"));
        } else if (parser.hasFlag("--bots")) {
            nrBots = Integer.parseInt(parser.getValue("--bots"));
        } else {
            startGameController = true;
        }

        AppSettings.NUM_BOTS = nrBots;

        boolean runTests = false;
        if (parser.hasFlag(("-t"))) {
            AppSettings.RUN_PERFORMANCE_TEST = true;
            runTests = true;
            TestLogger.init();
        } else if (parser.hasFlag(("--test"))) {
            AppSettings.RUN_PERFORMANCE_TEST = true;
            runTests = true;
            TestLogger.init();
        }

        boolean testAll = false;
        if (parser.hasFlag("--testAll")) {
            AppSettings.RUN_PERFORMANCE_TEST = true;
            runTests = true;
            testAll = true;
            TestLogger.init();
        }

        long lobbyId = -1;
        if (parser.hasFlag("-l")) {
            lobbyId = Long.parseLong(parser.getValue("-l"));
        } else if (parser.hasFlag("--lobby")) {
            lobbyId = Long.parseLong(parser.getValue("--lobby"));
        }


        boolean startLeaderBot = false;
        if (parser.hasFlag("-a") || parser.hasFlag("--auto")) {
            startGameController = false;
            startLeaderBot = true;
        }

        int mapIndex = 0; // DEFAULT BEGINNER MAP
        if (parser.hasFlag("-m")) {
            mapIndex = Integer.parseInt(parser.getValue("-m"));
        } else if (parser.hasFlag("--map")) {
            mapIndex = Integer.parseInt(parser.getValue("--map"));
        }

        if (parser.hasFlag("-i")) {
            AppSettings.NAMING_SERVICE_IP = parser.getValue("-i");
        } else if (parser.hasFlag("--ip")) {
            AppSettings.NAMING_SERVICE_IP = parser.getValue("--ip");
        }

        if (parser.hasFlag("-p")) {
            AppSettings.NAMING_SERVICE_PORT = Integer.parseInt(parser.getValue("-p"));
        } else if (parser.hasFlag("--port")) {
            AppSettings.NAMING_SERVICE_PORT = Integer.parseInt(parser.getValue("--port"));
        }

        AppSettings.PrintSettings();
        System.out.println(1);
        if (startGameController) {
            Runnable startApp = () -> {
                try {
                    new GameController();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            };
            SwingUtilities.invokeLater(startApp);
        } else if (startLeaderBot) {
            if (nrBots < 2) {
                System.out.println();
                System.out.println("Error: 2 or more bots required when starting application with leader bot!");
                System.out.println();
                printUsage();
                System.exit(1);
            }

            if (mapIndex < 0 || mapIndex > 4) {
                System.out.println();
                System.out.println("Error: Unknown MapIndex, must be in the range of [0,4]");
                System.out.println();
                printUsage();
                System.exit(1);
            }

            BotHandler.startLeaderBot(nrBots, forceFlag, mapIndex, runTests, testAll);
        } else {
            BotHandler.initBots(lobbyId, nrBots, forceFlag, runTests, true);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar StrategicSupremacy-1.0-shaded.jar [options]");
        System.out.println("Optional:");
        System.out.println("  -l, --lobby   <lobbyId>       The ID of the lobby to join.");
        System.out.println("  -b, --bots    <nrBots>        The number of bots to initialize.");
        System.out.println("  -i  --ip      <ipAddress>     The ip address for the naming service.");
        System.out.println("  -p, --port    <port>          The port for the naming service.");
        System.out.println("  -t, --test                    Run performance test.");
        System.out.println("  -l1, -l2, -l3                 Force clients to only update the game using l1, l2, or l3.");
        System.out.println("  -a, --auto                    Creates a leader bot that will create a new lobby and start.");
        System.out.println("                                the number of bots specified and start the game automatically.");
        System.out.println("  -m, --map     <mapIndex>      The index of the map the leader bot will use when creating a lobby.");
        System.out.println("  -h, --help                    Show this usage information.");
        System.out.println("Notes:");
        System.out.println("  1. If bots are specified the user will not get any graphical interface.");
        System.out.println("  2. If bots are specified and no lobby id, the bots will fetch lobbies form the");
        System.out.println("     NamingService and join the first available one.");
        System.out.println("  3. Path for performance test is optional, if no path is set, it will output");
        System.out.println("     the files in the current directory");
    }
}