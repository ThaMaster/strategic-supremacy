package se.umu.cs.ads.sp.model.map;

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
            while ((line = br.readLine()) != null) {
                map.add(new ArrayList<>());
                String[] numbers = line.split(" ");
                for (String number : numbers) {
                    map.get(row).add(new TileModel(Integer.parseInt(number), Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
                }
                currentCols = map.get(row).size();
                if (currentCols > maxCol) {
                    maxCol = currentCols;
                }
                row++;
            }
            System.out.println(row);
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

    public GameObject getInhabitant(Position position) {
        int row = position.getY() / Constants.TILE_WIDTH;
        int col = position.getX() / Constants.TILE_HEIGHT;
        return map.get(row).get(col).getInhabitant();
    }

    public void removeInhabitant(Position position) {
        int row = position.getY() / Constants.TILE_WIDTH;
        int col = position.getX() / Constants.TILE_HEIGHT;
        map.get(row).get(col).removeInhabitant();
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
}
