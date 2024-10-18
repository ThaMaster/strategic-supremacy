package se.umu.cs.ads.sp.utils.enums;

public enum UnitType {

    GUNNER("GUNNER"),
    // Maybe more units later?
    BRAWLER("BRAWLER"),
    ARCHER("ARCHER"),
    GOLD("GOLD");

    public final String label;

    UnitType(String label) {
        this.label = label;
    }

    public static UnitType fromLabel(String label) {
        for (UnitType typeEnum : UnitType.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("ERROR: Unknown unit type " + label);
    }
}
