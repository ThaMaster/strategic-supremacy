package se.umu.cs.ads.sp.view.windows.panels.gamepanel.map;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.TileType;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MiniMap {

    private BufferedImage miniMap;
    private final int miniMapWidth;
    private final int miniMapHeight;
    private final double scaleX;
    private final double scaleY;
    private final int mapTileWidth;
    private final int mapTileHeight;

    public MiniMap(int miniMapWidth, int miniMapHeight, int mapWidth, int mapHeight) {
        this.miniMapWidth = miniMapWidth;
        this.miniMapHeight = miniMapHeight;
        scaleX = (double) miniMapWidth / mapWidth;
        scaleY = (double) miniMapHeight / mapHeight;
        mapTileWidth = (int) Math.ceil(UtilView.tileSize * scaleX);
        mapTileHeight = (int) Math.ceil(UtilView.tileSize * scaleY);
        miniMap = new BufferedImage(miniMapWidth, miniMapHeight, BufferedImage.TYPE_INT_ARGB);
    }

    public void addTile(int x, int y, TileType type, String variant) {
        int minimapX = (int) Math.floor(x * scaleX);
        int minimapY = (int) Math.floor(y * scaleY);
        Graphics2D mapGraphics = miniMap.createGraphics();

        if (type == TileType.GRASS) {
            mapGraphics.setColor((variant.equals("light") ? Color.GREEN : Color.GREEN.darker().darker()));
        } else {
            mapGraphics.setColor((variant.equals("light") ? Color.GRAY : Color.GRAY.darker().darker()));
        }

        mapGraphics.fillRect(minimapX, minimapY, mapTileWidth, mapTileHeight);
        mapGraphics.dispose();
    }

    public void addPoint(Position entityPosition, Color objectColor, int pointSize) {
        int minimapX = (int) Math.floor(entityPosition.getX() * scaleX);
        int minimapY = (int) Math.floor(entityPosition.getY() * scaleY);
        Graphics2D mapGraphics = miniMap.createGraphics();
        mapGraphics.setColor(objectColor);
        mapGraphics.fillRect(minimapX, minimapY, (int) Math.ceil(pointSize * scaleX), (int) Math.ceil(pointSize * scaleY));
        mapGraphics.dispose();
    }

    public void addCameraBox() {
        int topLeftX = Camera.getPosition().getX() - UtilView.screenWidth / 2;
        int topLeftY = Camera.getPosition().getY() - UtilView.screenHeight / 2;

        int minimapX = (int) Math.floor(topLeftX * scaleX);
        int minimapY = (int) Math.floor(topLeftY * scaleY);
        // Convert the camera's width and height to minimap dimensions
        int minimapWidth = (int) Math.ceil(UtilView.screenWidth * scaleX);
        int minimapHeight = (int) Math.ceil(UtilView.screenHeight * scaleY);

        Graphics2D mapGraphics = miniMap.createGraphics();
        mapGraphics.setColor(Color.BLUE);
        mapGraphics.drawRect(minimapX, minimapY, minimapWidth, minimapHeight);
        mapGraphics.dispose();
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(miniMap, UtilView.screenWidth - miniMap.getWidth(), 0, miniMapWidth, miniMapHeight, null);
    }

    public static BufferedImage createMinimapPreview(ArrayList<ArrayList<TileType>> map, int mapWidth, int mapHeight, int width, int height) {
        double scaleX = (double) width / mapWidth;
        double scaleY = (double) height / mapHeight;
        int mapTileWidth = (int) Math.ceil(UtilView.tileSize * scaleX);
        int mapTileHeight = (int) Math.ceil(UtilView.tileSize * scaleY);
        BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D mapGraphics = mapImage.createGraphics();

        for (int y = 0; y < map.size(); y++) {
            for (int x = 0; x < map.get(y).size(); x++) {
                int minimapX = (int) Math.floor(x * scaleX);
                int minimapY = (int) Math.floor(y * scaleY);
                if (map.get(y).get(x) == TileType.GRASS) {
                    mapGraphics.setColor(Color.GREEN);
                } else {
                    mapGraphics.setColor(Color.GRAY);
                }
                mapGraphics.fillRect(minimapX, minimapY, mapTileWidth, mapTileHeight);
            }
        }
        mapGraphics.dispose();
        return mapImage;
    }
}
