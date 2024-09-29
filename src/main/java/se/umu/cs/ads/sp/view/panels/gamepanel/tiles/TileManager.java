package se.umu.cs.ads.sp.view.panels.gamepanel.tiles;

import se.umu.cs.ads.sp.model.map.TileModel;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.TileType;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TileManager {
    private HashMap<TileType, TileView> tileMap;
    private ArrayList<ArrayList<TileType>> viewMap;

    private int numCols;
    private int numRows;
    public TileManager() {
        this.tileMap = new HashMap<>();
        initTiles();
    }

    private void initTiles() {
        // Load all tiles and make them to objects.
        tileMap.put(TileType.GRASS, new TileView("plain", ImageLoader.loadImage("/sprites/tiles/grass.png")));
        tileMap.put(TileType.STONE, new TileView("plain", ImageLoader.loadImage("/sprites/tiles/stone.png")));
    }

    public void setMap(ArrayList<ArrayList<TileModel>> modelMap) {
        viewMap = new ArrayList<>();
        int y = 0;
        int x = 0;
        int longestRow = 0;
        for (y = 0; y < modelMap.size(); y++) {
            viewMap.add(new ArrayList<>());
            for (x = 0; x < modelMap.get(y).size(); x++) {
                viewMap.get(y).add(modelMap.get(y).get(x).getType());
                if(x > longestRow) {
                    longestRow = x;
                }
            }
        }
        numCols = modelMap.size();
        numRows = longestRow;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public void draw(Graphics2D g2d, Position cameraWorldPosition) {
        for (int y = 0; y < viewMap.size(); y++) {
            for (int x = 0; x < viewMap.get(y).size(); x++) {
                int worldX = x * UtilView.tileSize;
                int worldY = y * UtilView.tileSize;
                if (insideScreen(worldX, worldY)) {
                    TileType type = viewMap.get(y).get(x);
                    int screenX = worldX - cameraWorldPosition.getX() + UtilView.screenX;
                    int screenY = worldY - cameraWorldPosition.getY() + UtilView.screenY;
                    g2d.drawImage(tileMap.get(type).getImage(), screenX, screenY, UtilView.tileSize, UtilView.tileSize, null);
                }
            }
        }
    }

    public boolean insideScreen(int worldX, int worldY) {
        // Make sure to return false so nothing is rendered outside the screen.
        return true;
    }

}

