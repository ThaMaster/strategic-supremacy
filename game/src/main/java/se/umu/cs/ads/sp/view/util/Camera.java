package se.umu.cs.ads.sp.view.util;

import se.umu.cs.ads.sp.utils.Position;

public class Camera {

    private static final Position camPos = new Position(0, 0);

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
        return new Position(
                camPos.getX() + (screenX - UtilView.screenWidth / 2) / (int) UtilView.scale,
                camPos.getY() + (screenY - UtilView.screenHeight / 2) / (int) UtilView.scale);
    }

    public static Position worldToScreen(int worldX, int worldY) {
        return new Position(
                (worldX - camPos.getX()) * (int) UtilView.scale + UtilView.screenWidth / 2,
                (worldY - camPos.getY()) * (int) UtilView.scale + UtilView.screenHeight / 2);
    }

    public static Position worldToScreen(Position position) {
        return new Position(
                (position.getX() - camPos.getX()) * (int) UtilView.scale + UtilView.screenWidth / 2,
                (position.getY() - camPos.getY()) * (int) UtilView.scale + UtilView.screenHeight / 2);
    }

    public static boolean worldPosInsideScreen(Position worldPos, int width, int height) {
        // Convert the world position to screen position
        Position screenPos = worldToScreen(worldPos);

        // Check if any part of the tile is within the screen
        return screenPos.getX() + width >= 0 && screenPos.getX() <= UtilView.screenWidth &&
                screenPos.getY() + height >= 0 && screenPos.getY() <= UtilView.screenHeight;
    }

    public static boolean screenPosInsideScreen(Position screenPos, int width, int height) {
        // Check if any part of the tile is within the screen
        return screenPos.getX() + width >= 0 && screenPos.getX() <= UtilView.screenWidth &&
                screenPos.getY() + height >= 0 && screenPos.getY() <= UtilView.screenHeight;
    }

}
