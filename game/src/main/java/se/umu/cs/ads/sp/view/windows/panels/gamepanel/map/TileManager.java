package se.umu.cs.ads.sp.view.windows.panels.gamepanel.map;

import se.umu.cs.ads.sp.model.map.TileModel;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.TileType;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.UtilView;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.tiles.GrassTile;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.tiles.StoneTile;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.tiles.TileView;

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
            }
            if (modelMap.get(y).size() > longestRow) {
                longestRow = modelMap.get(y).size();
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

    public MiniMap draw(Graphics2D g2d) {
        MiniMap miniMap = new MiniMap(150, 150, numCols * UtilView.tileSize, numRows * UtilView.tileSize);
        for (int y = 0; y < viewMap.size(); y++) {
            for (int x = 0; x < viewMap.get(y).size(); x++) {
                Position tileWorldPosition = new Position(x * UtilView.tileSize, y * UtilView.tileSize);
                boolean inFow = fowView.isInFow(tileWorldPosition);
                String variant = inFow ? "light" : "dark";
                TileType type = viewMap.get(y).get(x);
                miniMap.addTile(tileWorldPosition.getX(), tileWorldPosition.getY(), type, variant);

                if (Camera.worldPosInsideScreen(tileWorldPosition, UtilView.tileSize, UtilView.tileSize)) {
                    Position screenPos = Camera.worldToScreen(tileWorldPosition);
                    BufferedImage image = tileMap.get(type).getImage(variant, getTileVariant(x, y));
                    g2d.drawImage(image, screenPos.getX(), screenPos.getY(), UtilView.tileSize, UtilView.tileSize, null);
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

    public void updateFowView(ArrayList<PlayerUnitView> unitViews) {
        this.fowView.updateUnitPositions(new ArrayList<>(unitViews));
    }

    public boolean isInFow(Position pos) {
        return this.fowView.isInFow(pos);
    }

    public ArrayList<ArrayList<TileType>> getViewMap() {
        return this.viewMap;
    }

    public int getMapWidth() {
        return numCols;
    }

    public int getMapHeight() {
        return numRows;
    }

}

