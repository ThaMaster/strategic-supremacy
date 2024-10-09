package se.umu.cs.ads.sp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class AppSettings {
    public static int HOST_PORT = 8080;
    public static String NAMING_SERVICE_IP = "127.0.0.1";
    public static int NAMING_SERVICE_PORT = 1337;
    public static boolean DEBUG = false;

    public static void SetGameConfig() {
        InputStream settingsUrl = AppSettings.class.getClassLoader().getResourceAsStream("AppSettings.cfg");

        if (settingsUrl == null) {
            System.out.println("Could not find config file. ");
            return;
        }

        Scanner sc = new Scanner(settingsUrl);
        int row = 0;
        while (sc.hasNext()) {
        row++;
        String[] words = sc.nextLine().split(" ");
        if (words[0].startsWith("/")) {
          // Comment
          continue;
        }
        if (words.length != 2) {
          return;
        }
        setConfigField(words[0], words[1]);
        }
        sc.close();
    }

    private static void setConfigField(String key, String value) {
        switch (key) {
            case "naming_service_ip:":
                NAMING_SERVICE_IP = value;
                break;
            case "naming_service_port:":
                NAMING_SERVICE_PORT = Integer.parseInt(value);
                break;
            case "host_port:":
                HOST_PORT = Integer.parseInt(value);
                break;
            case "debug:":
                DEBUG = value.equals("true");
                break;
            default:
                System.out.println("Unknown config key: " + key);
                break;
        }
    }

    public static void PrintSettings() {
        System.out.println("============AppSettings============");
        System.out.printf("%-20s %s%n", "HOST_PORT: ", HOST_PORT);
        System.out.printf("%-20s %s%n", "NAMING_SERVICE_IP:", NAMING_SERVICE_IP);
        System.out.printf("%-20s %s%n", "NAMING_SERVICE_PORT:", NAMING_SERVICE_PORT);
        System.out.printf("%-20s %s%n", "DEBUG MODE:", DEBUG);
        System.out.println("===================================");
    }
}
