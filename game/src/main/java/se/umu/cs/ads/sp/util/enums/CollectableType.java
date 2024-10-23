package se.umu.cs.ads.sp.util.enums;

public enum CollectableType {

    CHEST("CHEST"),
    GOLD("GOLD"),
    FLAG("FLAG");

    public final String label;

    CollectableType(String label) {
        this.label = label;
    }

    public static CollectableType fromLabel(String label) {
        for (CollectableType typeEnum : CollectableType.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("ERROR: Unknown collectable type " + label);

    }
}
