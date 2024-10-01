package se.umu.cs.ads.sp.view.panels.gamepanel.tiles;

import se.umu.cs.ads.sp.model.map.TileModel;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.TileType;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class TileManager {
    private HashMap<TileType, TileView> tileMap;
    private ArrayList<ArrayList<TileType>> viewMap;

    private int numCols;
    private int numRows;

    private FowView fowView;

    public TileManager() {
        fowView = new FowView(new ArrayList<>());
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
        int longestRow = 0;
        for (int y = 0; y < modelMap.size(); y++) {
            viewMap.add(new ArrayList<>());
            for (int x = 0; x < modelMap.get(y).size(); x++) {
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
                    if(fowView.isInFow(new Position(worldX, worldY))) {
                        TileType type = viewMap.get(y).get(x);
                        int screenX = worldX - cameraWorldPosition.getX() + UtilView.screenX;
                        int screenY = worldY - cameraWorldPosition.getY() + UtilView.screenY;
                        BufferedImage image = tileMap.get(type).getImage();
                        g2d.drawImage(image, screenX, screenY, UtilView.tileSize, UtilView.tileSize, null);
                    }
                }
            }
        }
    }

    public boolean insideScreen(int worldX, int worldY) {
        // Make sure to return false so nothing is rendered outside the screen.
        return true;
    }

    public void updateFowView(ArrayList<PlayerUnitView> unitViews) {
        this.fowView.updateUnitPositions(new ArrayList<>(unitViews));
    }

    public boolean isInFow(Position pos) {
        return this.fowView.isInFow(pos);
    }

}

