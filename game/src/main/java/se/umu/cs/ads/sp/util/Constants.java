package se.umu.cs.ads.sp.util;

public class Constants {
    public static final int TILE_WIDTH = 64;
    public static final int TILE_HEIGHT = 64;

    public static final int ENTITY_WIDTH = 32;
    public static final int ENTITY_HEIGHT = 32;

    public static final int OBJECT_WIDTH = 16;
    public static final int OBJECT_HEIGHT = 16;

    public static final long L3_UPDATE_TIME = 2000L;
    public static final long L2_UPDATE_TIME = L3_UPDATE_TIME / 5;

    public static final int HIGH_SEVERITY = 1;
    public static final int MID_SEVERITY = 2;
    public static final int LOW_SEVERITY = 3;

    public static final int FIELD_OF_VIEW_RADIUS = 250;
    public static final int L2_RADIUS = FIELD_OF_VIEW_RADIUS * 2;
    public static final int L1_RADIUS = FIELD_OF_VIEW_RADIUS;
}
