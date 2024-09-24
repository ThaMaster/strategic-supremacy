package se.umu.cs.ads.sp.view.util;

import se.umu.cs.ads.sp.utils.Position;

public class Utils {


    public static double distance(Position origin,
                                  Position destination)
    {
        return Math.sqrt(Math.pow((destination.getX() - origin.getX()), 2)
                + Math.pow((destination.getY() - origin.getY()), 2));
    }
}
