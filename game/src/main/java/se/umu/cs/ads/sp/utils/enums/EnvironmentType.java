package se.umu.cs.ads.sp.utils.enums;

public enum EnvironmentType {

    BASE("BASE"),
    GOLDMINE("GOLDMINE");

    public final String label;

    EnvironmentType(String label) {
        this.label = label;
    }

    public static EnvironmentType fromLabel(String label) {
        for (EnvironmentType typeEnum : EnvironmentType.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("ERROR: Unknown environment type " + label);

    }
}
