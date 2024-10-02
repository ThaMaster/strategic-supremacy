package se.umu.cs.ads.ns.app;

import se.umu.cs.ads.ns.util.Util;


public class NamingService {
    private NSServer server;

    public NamingService() {
        String ip = Util.getLocalIP();
        int port = Util.getFreePort();
        server = new NSServer(port);
        System.out.println("Starting NamingService!");
        System.out.println("Local Ip: " + ip);
        System.out.println("Free Port: " + port);
    }

    public void startServer() {
        server.start();
    }

    // Blocking call to keep the server running
    public void blockUntilShutdown() {
        server.blockUntilShutdown();
    }
}