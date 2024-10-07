package se.umu.cs.ads.sp.view.frameComponents.panels.gamepanel.tiles;

import se.umu.cs.ads.sp.model.map.TileModel;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.TileType;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
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
        tileMap.put(TileType.GRASS, new GrassTile());
        tileMap.put(TileType.STONE, new StoneTile());
    }

    public void setMap(ArrayList<ArrayList<TileModel>> modelMap) {
        viewMap = new ArrayList<>();
        int longestRow = 0;
        for (int y = 0; y < modelMap.size(); y++) {
            viewMap.add(new ArrayList<>());
            for (int x = 0; x < modelMap.get(y).size(); x++) {
                viewMap.get(y).add(modelMap.get(y).get(x).getType());
                if (x > longestRow) {
                    longestRow = x;
                }
            }
        }

        numRows = modelMap.size();
        numCols = longestRow;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public MiniMap draw(Graphics2D g2d, Position cameraWorldPosition) {
        MiniMap miniMap = new MiniMap(150, 150, numCols * UtilView.tileSize, numRows * UtilView.tileSize);
        for (int y = 0; y < viewMap.size(); y++) {
            for (int x = 0; x < viewMap.get(y).size(); x++) {
                int worldX = x * UtilView.tileSize;
                int worldY = y * UtilView.tileSize;
                if (insideScreen(worldX, worldY)) {
                    TileType type = viewMap.get(y).get(x);
                    int screenX = worldX - cameraWorldPosition.getX() + UtilView.screenX;
                    int screenY = worldY - cameraWorldPosition.getY() + UtilView.screenY;

                    BufferedImage image;
                    if (fowView.isInFow(new Position(worldX, worldY))) {
                        image = tileMap.get(type).getImage("light", getTileVariant(x, y));
                        miniMap.addTile(worldX, worldY, type, "light");
                    } else {
                        image = tileMap.get(type).getImage("dark", getTileVariant(x, y));
                        miniMap.addTile(worldX, worldY, type, "dark");
                    }
                    g2d.drawImage(image, screenX, screenY, UtilView.tileSize, UtilView.tileSize, null);
                }
            }
        }

        return miniMap;
    }

    public String getTileVariant(int col, int row) {
        if (viewMap.get(row).get(col) == TileType.GRASS) {
            return "000000000";
        }
        StringBuilder tileVariant = new StringBuilder();
        for (int y = row - 1; y <= row + 1; y++) {
            if (y < 0 || y >= viewMap.size()) {
                tileVariant.append("111");
                continue;
            }
            for (int x = col - 1; x <= col + 1; x++) {
                if (x < 0 || x >= viewMap.get(y).size() || viewMap.get(y).get(x) == TileType.STONE) {
                    tileVariant.append("1");
                } else {
                    tileVariant.append("0");
                }
            }
        }
        return tileVariant.toString();
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

