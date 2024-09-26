package se.umu.cs.ads.sp.utils;

import java.util.Random;

public class Utils {

    public static boolean getRandomSuccess(int successChange){
        int num = getRandomInt(0, 100);
        return num <= successChange;
    }

    public static int getRandomInt(int min, int max){
        Random random = new Random();
        return random.nextInt(min, max);
    }
}

