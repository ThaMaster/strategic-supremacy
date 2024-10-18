package se.umu.cs.ads.sp.view.util;

import se.umu.cs.ads.sp.utils.Position;

public class Camera {

    private static Position camPos = new Position(0, 0);


    public static void setXPosition(int x) {
        camPos.setX(x);
    }

    public static void setYPosition(int y) {
        camPos.setY(y);
    }

    public static void setPosition(Position newPosition) {
        camPos.setX(newPosition.getX());
        camPos.setY(newPosition.getY());
    }

    public static Position getPosition() {
        return camPos;
    }

    public static Position screenToWorld(int screenX, int screenY) {
        int worldX = camPos.getX() + (screenX - UtilView.screenWidth / 2) / (int) UtilView.scale;
        int worldY = camPos.getY() + (screenY - UtilView.screenHeight / 2) / (int) UtilView.scale;
        return new Position(worldX, worldY);
    }

    public static Position worldToScreen(int worldX, int worldY) {
        int screenX = ((worldX - camPos.getX()) * (int) UtilView.scale + UtilView.screenWidth / 2);
        int screenY = ((worldY - camPos.getY()) * (int) UtilView.scale + UtilView.screenHeight / 2);
        return new Position(screenX, screenY);
    }

    public static Position worldToScreen(Position position) {
        int screenX = ((position.getX() - camPos.getX()) * (int) UtilView.scale + UtilView.screenWidth / 2);
        int screenY = ((position.getY() - camPos.getY()) * (int) UtilView.scale + UtilView.screenHeight / 2);
        return new Position(screenX, screenY);
    }

    public static boolean insideScreen(Position position, int tileSize) {
        // Convert the world position to screen position
        Position screenPos = worldToScreen(position);

        // Check if the tile is within the screen bounds, taking its size into account
        int screenX = screenPos.getX();
        int screenY = screenPos.getY();

        // Check if any part of the tile is within the screen
        boolean withinXBounds = screenX + tileSize >= 0 && screenX <= UtilView.screenWidth;
        boolean withinYBounds = screenY + tileSize >= 0 && screenY <= UtilView.screenHeight;

        return withinXBounds && withinYBounds;
    }

}
