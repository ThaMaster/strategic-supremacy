package se.umu.cs.ads.sp.view.windows.panels.gamepanel.map;

import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.util.enums.TileType;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MiniMap {

    private BufferedImage miniMap;
    private BufferedImage miniMapBorder;
    private final int miniMapWidth;
    private final int miniMapHeight;
    private final double scaleX;
    private final double scaleY;
    private final int mapTileWidth;
    private final int mapTileHeight;

    private int borderPadding = 36;
    private int minimapOffset = 16;

    public MiniMap(int miniMapWidth, int miniMapHeight, int mapWidth, int mapHeight) {
        this.miniMapWidth = miniMapWidth;
        this.miniMapHeight = miniMapHeight;
        miniMapBorder = ImageLoader.loadImage("/sprites/hud/minimapBorder.png");
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
        int pointWidth = (int) Math.ceil(pointSize * scaleX);
        int pointHeight = (int) Math.ceil(pointSize * scaleY);
        int minimapX = (int) Math.floor(entityPosition.getX() * scaleX) - pointWidth / 2;
        int minimapY = (int) Math.floor(entityPosition.getY() * scaleY) - pointHeight / 2;

        Graphics2D mapGraphics = miniMap.createGraphics();
        mapGraphics.setColor(objectColor);
        mapGraphics.fillRect(minimapX, minimapY, pointWidth, pointHeight);
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
        // Calculate position for the minimap
        int minimapX = UtilView.screenWidth - miniMapWidth - minimapOffset; // X position for the minimap
        int minimapY = minimapOffset; // Y position for the minimap

        // Calculate the dimensions of the border based on the minimap dimensions
        int borderWidth = miniMapWidth + borderPadding; // Adjust border size as needed
        int borderHeight = miniMapHeight + borderPadding; // Adjust border size as needed

        // Calculate position for the border image
        int borderX = minimapX - borderPadding / 2; // Center the border around the minimap
        int borderY = minimapY - borderPadding / 2; // Center the border around the minimap

        // Draw the minimap image
        g2d.drawImage(miniMap, minimapX, minimapY, miniMapWidth, miniMapHeight, null); // Draw minimap

        // Draw the border image
        if (miniMapBorder != null) {
            g2d.drawImage(miniMapBorder, borderX, borderY, borderWidth, borderHeight, null); // Draw border
        }
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
