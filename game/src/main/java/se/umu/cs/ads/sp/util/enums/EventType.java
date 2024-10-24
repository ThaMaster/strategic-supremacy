package se.umu.cs.ads.sp.util.enums;

public enum EventType {
    //General game event
    NEW_ROUND,
    LOGG,
    GOLD_PICK_UP,
    BUFF_PICK_UP,
    FLAG_PICK_UP,
    MINE_DEPLETED,
    MINING,
    FLAG_TO_BASE,
    POINT_PICK_UP,

    ENEMY_PICK_UP,

    //Game actions
    TAKE_DMG,
    ATTACK,
    DEATH,

    // Communication events
    PLAYER_LEFT,
    PLAYER_DEFEATED
}
