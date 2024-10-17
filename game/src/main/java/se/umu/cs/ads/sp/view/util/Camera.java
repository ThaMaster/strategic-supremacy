package se.umu.cs.ads.sp.view.util;

import se.umu.cs.ads.sp.utils.Position;

public class Camera {

    private static Position cameraWorldPosition = new Position(0, 0);


    public static void setXPosition(int x) {
        cameraWorldPosition.setX(x);
    }

    public static void setYPosition(int y) {
        cameraWorldPosition.setY(y);
    }

    public static void setCameraWorldPosition(Position newPosition) {
        cameraWorldPosition = newPosition;
    }

    public static Position getPosition() {
        return cameraWorldPosition;
    }

    public static Position screenToWorld(int screenX, int screenY) {
        int worldX = cameraWorldPosition.getX() + (screenX - UtilView.screenWidth / 2) / (int) UtilView.scale;
        int worldY = cameraWorldPosition.getY() + (screenY - UtilView.screenHeight / 2) / (int) UtilView.scale;
        return new Position(worldX, worldY);

    }

    public static Position worldToScreen(int worldX, int worldY) {
        int screenX = ((worldX - cameraWorldPosition.getX()) * (int) UtilView.scale + UtilView.screenWidth / 2);
        int screenY = ((worldY - cameraWorldPosition.getY()) * (int) UtilView.scale + UtilView.screenHeight / 2);
        return new Position(screenX, screenY);
    }

    public static Position worldToScreen(Position position) {
        int screenX = ((position.getX() - cameraWorldPosition.getX()) * (int) UtilView.scale + UtilView.screenWidth / 2);
        int screenY = ((position.getY() - cameraWorldPosition.getY()) * (int) UtilView.scale + UtilView.screenHeight / 2);
        return new Position(screenX, screenY);
    }


    public static boolean isInView(Position position) {
        return true;
    }
}
