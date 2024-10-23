package se.umu.cs.ads.sp.util.enums;

public enum UpgradeType {
    MOVEMENT_SPEED("MOVEMENT_SPEED", 100, 2),
    ATTACK_DMG("ATTACK_DMG", 150, 1),
    MAX_HP("MAX_HP", 200, 3);

    public final String label;
    public final int initalCost;
    public final int upgradeAmount;

    UpgradeType(String label, int initalCost, int upgradeAmount) {
        this.label = label;
        this.initalCost = initalCost;
        this.upgradeAmount = upgradeAmount;
    }

    public static UpgradeType fromLabel(String label) {
        for (UpgradeType typeEnum : UpgradeType.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("ERROR: Unknown upgrade type " + label);
    }

}
