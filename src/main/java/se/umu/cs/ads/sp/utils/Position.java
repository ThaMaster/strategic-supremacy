package se.umu.cs.ads.sp.utils;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static double distance(Position origin, Position destination) {
        return Math.sqrt(Math.pow((destination.getX() - origin.getX()), 2)
                + Math.pow((destination.getY() - origin.getY()), 2));
    }

    //Testing purposes
    public void printPosition(String positionName) {
        System.out.println(positionName + " (" + x / Constants.TILE_WIDTH + ", " + y / Constants.TILE_WIDTH + ")");
    }
}
