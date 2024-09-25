package se.umu.cs.ads.sp.utils;

import java.util.Random;

public class Utils {


    public static boolean getRandomSuccess(int successChange){
        Random random = new Random();
        int num = random.nextInt(0,100);
        return num <= successChange;
    }
}

