package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.panels.gamepanel.tiles.TileView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    private HashMap<Integer, TileModel> tileMap;
    private ArrayList<ArrayList<Integer>> map;

    private int cols;
    private int rows;

    public Map() {
        this.tileMap = new HashMap<>();
        initTiles();
    }

    public void initTiles() {
        // Create all tiles needed.
    }

    public void loadMap(String file) {
        try {
            InputStream is = getClass().getResourceAsStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                map.add(new ArrayList<>());
                String[] numbers = line.split(" ");
                for (String number : numbers) {
                    map.get(row).add(Integer.parseInt(number));
                }
                row++;
            }

            this.rows = map.size();
            this.cols = map.get(0).size();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getRowLength(int row) {
        map.get(row).size();
    }

    /**
     * Implement in Milestone 3
     */
    public void generateMap() {
        // Generation logic

        // Then load the map
        // loadMap();
    }
}
