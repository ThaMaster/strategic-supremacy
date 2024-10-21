package se.umu.cs.ads.sp.utils.enums;

import java.util.ArrayList;

public enum RewardType {
    GOLD("GOLD", false),
    POINT("POINT", false),
    FLAG("FLAG", false),
    MOVEMENT_SPEED("MOVEMENT_SPEED", true),
    ATTACK_DMG("ATTACK_DMG", true),
    MAX_HP("MAX_HP", true);

    public final String label;
    private final boolean upgrade;

    RewardType(String label, boolean upgrade) {
        this.label = label;
        this.upgrade = upgrade;
    }

    public static RewardType fromLabel(String label) {
        for (RewardType typeEnum : RewardType.values()) {
            if (typeEnum.label.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("ERROR: Unknown reward type " + label);
    }

    public static ArrayList<String> getUpgradeTypes() {
        ArrayList<String> upgradeTypes = new ArrayList<>();
        for (RewardType typeEnum : RewardType.values()) {
            if (typeEnum.upgrade) {
                upgradeTypes.add(typeEnum.label);
            }
        }
        return upgradeTypes;
    }
}
