package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.RewardType;
import se.umu.cs.ads.sp.utils.enums.UpgradeType;

public class Reward {
    private int quantity;
    private RewardType type;

    public Reward(int quantity, RewardType type) {
        this.quantity = quantity;
        this.type = type;
    }

    @Override
    public String toString() {
        return "+" + quantity + " " + type;
    }

    public int getQuantity() {
        return quantity;
    }

    public static int parseQuantity(String input) {
        // The regex will match a '+' followed by digits.
        String numberPart = input.replaceAll("[^\\d]", "");
        return Integer.parseInt(numberPart);
    }

    public static String parseReward(String input) {
        // The regex will remove the number part, leaving just the name.
        return input.replaceAll("\\+\\d+\\s*", "").trim();
    }

    public RewardType getType() {
        return type;
    }

    public static Reward getRandomReward() {
        int randomNum = Utils.getRandomInt(0, 4);
        return switch (randomNum) {
            case 0 -> new Reward(Utils.getRandomInt(1, 5), RewardType.POINT);
            case 1 -> new Reward(UpgradeType.MOVEMENT_SPEED.upgradeAmount, RewardType.MOVEMENT_SPEED);
            case 2 -> new Reward(UpgradeType.ATTACK_DMG.upgradeAmount, RewardType.ATTACK_DMG);
            default -> new Reward(Utils.getRandomInt(10, 20), RewardType.GOLD);
        };

    }
}


