package se.umu.cs.ads.sp.utils.enums;

public enum DtoTypes {

    CHEST("CHEST"),
    BASE("BASE"),
    GOLDMINE("GOLDMINE"),
    GOLD("GOLD"),
    FLAG("FLAG"),
    INCREASED_DMG("INCREASED_DMG"),
    POINT("POINT"),
    MOVEMENT("MOVEMENT");

    public final String label;

    DtoTypes(String label) {
        this.label = label;
    }


    public static DtoTypes fromLabel(String label) {
        for (DtoTypes typeEnum : DtoTypes.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("No DTO with label " + label);
    }

}