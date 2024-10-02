package se.umu.cs.ads.ns;

import se.umu.cs.ads.ns.app.NamingService;

public class Main {
    public static void main(String[] args) {
        NamingService namingService = new NamingService();
        namingService.startServer();
        namingService.blockUntilShutdown();
    }
}
