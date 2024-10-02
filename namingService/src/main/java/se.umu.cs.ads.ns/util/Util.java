package se.umu.cs.ads.ns.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Util {

    public static int getFreePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getLocalIP() {
        String ip = null;
        try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

}
