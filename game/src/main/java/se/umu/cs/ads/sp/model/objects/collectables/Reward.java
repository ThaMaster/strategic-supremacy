package se.umu.cs.ads.sp.model.objects.collectables;

import se.umu.cs.ads.sp.utils.Utils;

public class Reward {
    private int quantity;
    private String type;

    public Reward(int quantity, String type) {
        this.quantity = quantity;
        this.type = type;
    }

    @Override
    public String toString(){
        return "+"+quantity+" "+type;
    }
    public int getQuantity(){
        return quantity;
    }

    public String getType(){
        return type;
    }

    public static class RewardType {
        public static String GOLD = "Gold";
        public static String MOVEMENT ="Movement";
        public static String POINT = "Points";
        public static String INCREASED_DMG = "IncreasedDmg";
    }

    public static Reward getRandomReward(){
        int randomNum = Utils.getRandomInt(0,5);
        return switch (randomNum) {
            case 0 -> new Reward(Utils.getRandomInt(1, 5), RewardType.MOVEMENT);
            case 1 -> new Reward(Utils.getRandomInt(1, 5), RewardType.POINT);
            case 2 -> new Reward(Utils.getRandomInt(5, 20), RewardType.GOLD);
            case 3 -> new Reward(Utils.getRandomInt(1, 5), RewardType.INCREASED_DMG);
            default -> new Reward(Utils.getRandomInt(10, 20), RewardType.GOLD);
        };

    }
}


