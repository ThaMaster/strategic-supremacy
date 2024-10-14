package se.umu.cs.ads.sp.utils.enums;

public enum DtoTypes {

    CHEST("CHEST"),
    BASE("BASE"),
    GOLDMINE("GOLDMINE"),
    GOLD("GOLD");

    public final String type;
    DtoTypes(String type){
        this.type = type;
    }



    public static DtoTypes fromLabel(String label) {
        for (DtoTypes typeEnum : DtoTypes.values()) {
            if (typeEnum.type.equalsIgnoreCase(label)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("No day with label " + label);
    }

}