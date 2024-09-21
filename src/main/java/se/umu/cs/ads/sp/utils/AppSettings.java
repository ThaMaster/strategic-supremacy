package main.java.se.umu.cs.ads.sp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class AppSettings {
    public static int HOST_PORT = 8080;
    public static String NAMING_SERVICE_IP = "127.0.0.1";
    public static int NAMING_SERVICE_PORT = 1337;
    public static boolean DEBUG = false;

    public static void SetGameConfig(){
        File config = new File("./AppSettings.cfg");

        if(!config.exists()){
            System.out.println("Could not find config file. " + config.getAbsolutePath());
            return;
        }

        try{
            Scanner sc = new Scanner(config);
            int row = 0;
            while(sc.hasNext()){
                row++;
                String[] words = sc.nextLine().split(" ");
                if(words[0].startsWith("/")){
                    //Comment
                    continue;
                }
                if(words.length != 2){
                    System.out.println("Invalid syntax -> " + config.getName() + " row: " + row);
                    return;
                }
                setConfigField(words[0], words[1]);

            }
        }catch(FileNotFoundException e){
            System.out.println("Cannot find or open config file found");
        }
    }

    public static void PrintSettings(){
        System.out.println("=== Settings ===");
        System.out.println("HOST_PORT: " + HOST_PORT);
        System.out.println("NAMING_SERVICE_IP: " + NAMING_SERVICE_IP);
        System.out.println("NAMING_SERVICE_PORT: " + NAMING_SERVICE_PORT);
        System.out.println("DEBUG MODE: " + DEBUG);
        System.out.println("================");
    }

    private static void setConfigField(String key, String value){
        switch(key){
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
}
