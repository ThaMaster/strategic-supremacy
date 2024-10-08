package se.umu.cs.ads.ns.util;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    private static long currentId = 1;

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

    public static long generateId() {
        return currentId++;
    }

    public static long sha1Hash(String input, int numBits) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = sha1.digest(input.getBytes());

            BigInteger hashInt = new BigInteger(1, hashBytes);

            // Calculate the maximum number of bits that can be represented
            BigInteger mask = BigInteger.ONE.shiftLeft(numBits).subtract(BigInteger.ONE);
            // Apply the mask to get the desired number of bits
            BigInteger hashResult = hashInt.and(mask);

            // Return the truncated hash as a binary string
            return hashResult.longValue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
