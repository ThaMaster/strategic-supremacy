package se.umu.cs.ads.sp;

import se.umu.cs.ads.sp.controller.BotController;

import java.util.ArrayList;

public class BotHandler {

    public static void initBots(long lobbyId, int nrBots) {
        ArrayList<Thread> bots = new ArrayList<>();
        for (int i = 0; i < nrBots; i++) {
            BotController bot = new BotController(lobbyId);
            Thread botThread = new Thread(bot);
            botThread.start();
            bots.add(botThread);
        }
    }

}
