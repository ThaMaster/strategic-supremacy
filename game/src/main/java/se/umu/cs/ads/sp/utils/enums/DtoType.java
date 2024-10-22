package se.umu.cs.ads.sp.utils.enums;

public enum DtoType {

    CHEST("CHEST"),
    BASE("BASE"),
    GOLDMINE("GOLDMINE"),
    GOLD("GOLD"),
    FLAG("FLAG"),
    INCREASED_DMG("INCREASED_DMG"),
    ATTACK_DMG("ATTACK_DMG"),
    MOVEMENT("MOVEMENT_SPEED");

    public final String label;

    DtoType(String label) {
        this.label = label;
    }


    public static DtoType fromLabel(String label) {
        for (DtoType typeEnum : DtoType.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("No DTO with label " + label);
    }

}