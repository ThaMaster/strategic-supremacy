package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {

    // Now we store a whole object for every tile even though they are the same, did we not decide to not do this?
    private ArrayList<ArrayList<TileModel>> map;

    private int cols;
    private int rows;

    public Map() {
        this.map = new ArrayList<>();
        initTiles();
    }

    public void initTiles() {
        // Create all tiles needed.
    }

    public void loadMap(String file) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0;
            int maxCol = 0;
            int currentCols;
            this.map = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                map.add(new ArrayList<>());
                String[] numbers = line.split(" ");
                for (String number : numbers) {
                    map.get(row).add(new TileModel(Integer.parseInt(number)));
                }
                currentCols = map.get(row).size();
                if (currentCols > maxCol) {
                    maxCol = currentCols;
                }
                row++;
            }
            this.rows = row;
            this.cols = maxCol;
        } catch (IOException e) {
            System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    public void setInhabitant(GameObject object, Position position) {
        map.get(position.getY() / Constants.TILE_HEIGHT).get(position.getX() / Constants.TILE_WIDTH).setInhabitant(object);
    }

    public ArrayList<GameObject> getInhabitants(Position position) {
        int row = position.getY() / Constants.TILE_HEIGHT;
        int col = position.getX() / Constants.TILE_WIDTH;
        return map.get(row).get(col).getInhabitants();
    }

    public ArrayList<GameObject> getInhabitants(int row, int col) {
        if (inBounds(row, col)) {
            return map.get(row).get(col).getInhabitants();
        }

        return new ArrayList<>();
    }

    /**
     * Function for getting all the inhabitants from a whole collision box and not just the corners.
     *
     * @param cBox The collision box to check.
     * @return the inhabitants
     */
    public ArrayList<GameObject> getInhabitants(CollisionBox cBox) {
        ArrayList<GameObject> inhabitants = new ArrayList<>();
        ArrayList<Position> corners = cBox.getCorners();
        Position topLeft = corners.get(0);
        Position topRight = corners.get(1);
        Position bottomLeft = corners.get(2);

        int col = topLeft.getX() / Constants.TILE_WIDTH;
        int colEnd = topRight.getX() / Constants.TILE_WIDTH;
        int row = topLeft.getY() / Constants.TILE_HEIGHT;
        int rowEnd = bottomLeft.getY() / Constants.TILE_HEIGHT;

        for (int y = row - 1; y >= 0 && y < rowEnd && y < map.size(); y++) {
            for (int x = col - 1; x >= 0 && x < colEnd && x < map.get(y).size(); x++) {
                inhabitants.addAll(map.get(y).get(x).getInhabitants());
            }
        }
        return inhabitants;
    }

    public void removeInhabitant(GameObject inhabitant, Position position) {
        int row = position.getY() / Constants.TILE_WIDTH;
        int col = position.getX() / Constants.TILE_HEIGHT;
        map.get(row).get(col).removeInhabitant(inhabitant.getId());
    }

    /**
     * Implement in Milestone 3
     */
    public void generateMap() {
        // Generation logic

        // Then load the map
        // loadMap();
    }

    public ArrayList<ArrayList<TileModel>> getModelMap() {
        return this.map;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < map.size() && col >= 0 && col < map.get(row).size();
    }

    public ArrayList<Position> getStartPositions(int numPlayers){
        return new ArrayList<>();
    }
}
