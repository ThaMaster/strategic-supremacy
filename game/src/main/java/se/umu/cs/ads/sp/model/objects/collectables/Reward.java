package se.umu.cs.ads.sp.model.objects.collectables;

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

    public class RewardType {
        public static String GOLD = "Gold";
        public static String MOVEMENT ="Movement";
        public static String POINT = "Points";
    }

    public int getQuantity(){
        return quantity;
    }
}


