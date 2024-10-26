package se.umu.cs.ads.sp.util.enums;

public enum EntityState {
    IDLE("IDLE"),
    RUNNING("RUNNING"),
    ATTACKING("ATTACKING"),
    TAKING_DAMAGE("TAKING_DAMAGE"),
    MINING("MINING"),
    DEAD("DEAD");

    public final String label;
    EntityState(String label) {
        this.label = label;
    }

    public static EntityState fromLabel(String label) {
        for (EntityState typeEnum : EntityState.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("ERROR: Unknown collectable type " + label);
    }
}
