package se.umu.cs.ads.sp.view.util;

public class UtilView {
    // SCREEN SETTINGS
    public static final int originalTileSize = 64;
    public static final int originalEntitySize = 32;
    public static final int originalObjectSize = 16;
    public static double scale = 1;
    public static int tileSize = (int)(originalTileSize * scale);
    public static int entitySize = (int)(originalEntitySize * scale);
    public static int objectSize = (int)(originalObjectSize * scale);


    // Should this be here or somewhere else?
    public static final int maxScreenCol = 10;
    public static final int maxScreenRow = 10;
    public static final int screenWidth = tileSize * maxScreenCol;
    public static final int screenHeight = tileSize * maxScreenRow;

    public static int screenX = screenWidth/2 - tileSize/2;
    public static int screenY = screenHeight/2 - tileSize/2;

    public static void changeScale(double amount) {
        double newScale = scale + amount;
        if(newScale > 10 || newScale < 1) {
            return;
        }
        scale = newScale;
        tileSize = (int)(originalTileSize * scale);
        screenX = screenWidth/2 - tileSize/2;
        screenY = screenHeight/2 - tileSize/2;
        entitySize = (int)(originalEntitySize * scale);
        objectSize = (int)(originalObjectSize * scale);
    }

}
