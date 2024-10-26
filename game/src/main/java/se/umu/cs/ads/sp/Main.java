package se.umu.cs.ads.sp;

import se.umu.cs.ads.sp.Performance.TestLogger;
import se.umu.cs.ads.sp.controller.GameController;
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

        if(parser.hasFlag(("-t"))){
            AppSettings.RUN_PERFORMANCE_TEST = true;
            TestLogger.init(parser.getValue("-t"));
        }else if(parser.hasFlag(("-test"))){
            AppSettings.RUN_PERFORMANCE_TEST = true;
            TestLogger.init(parser.getValue("-t"));
        }

        int nrBots = 1; // DEFAULT BOTS ARE 1
        if (parser.hasFlag("-b")) {
            nrBots = Integer.parseInt(parser.getValue("-b"));
        } else if (parser.hasFlag("--bots")) {
            nrBots = Integer.parseInt(parser.getValue("--bots"));
        } else {
            startGameController = true;
        }

        long lobbyId = -1;
        if (parser.hasFlag("-l")) {
            lobbyId = Long.parseLong(parser.getValue("-l"));
        } else if (parser.hasFlag("--lobby")) {
            lobbyId = Long.parseLong(parser.getValue("--lobby"));
        }

        AppSettings.PrintSettings();

        if (startGameController) {
            Runnable startApp = () -> {
                try {
                    new GameController();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            };
            SwingUtilities.invokeLater(startApp);
        } else {
            BotHandler.initBots(lobbyId, nrBots);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java AiController [options]");
        System.out.println("Optional:");
        System.out.println("  -l, --lobby   <lobbyId>       The ID of the lobby to join.");
        System.out.println("  -b, --bots    <nrBots>        The number of bots to initialize.");
        System.out.println("  -n, --name    <botName>       The name of the Bot.");
        System.out.println("  -i, --ip      <ipAddress>     The ip address for the Bot.");
        System.out.println("  -p, --port    <port>          The port for the Ai.");
        System.out.println("  -t, --test    <path>          Run performance test");
        System.out.println("  -h, --help                    Show this usage information.");
        System.out.println("Notes:");
        System.out.println("  1. If bots are specified the user will not get any graphical interface.");
        System.out.println("  2. If bots are specified and no lobby id, the bots will fetch lobbies form the");
        System.out.println("     NamingService and join the first available one.");
        System.out.println("  3. Path for performance test is optional, if no path is set, it will output");
        System.out.println("     the files in the current directory");
    }
}