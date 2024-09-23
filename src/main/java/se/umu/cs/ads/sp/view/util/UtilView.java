package se.umu.cs.ads.sp.view.util;

public class UtilView {
    // SCREEN SETTINGS
    public static final int originalTileSize = 64;
    public static final int scale = 1;
    public static final int tileSize = originalTileSize * scale;

    // Should this be here or somewhere else?
    public static final int maxScreenCol = 12;
    public static final int maxScreenRow = 10;
    public static final int screenWidth = tileSize * maxScreenCol;
    public static final int screenHeight = tileSize * maxScreenRow;
}
