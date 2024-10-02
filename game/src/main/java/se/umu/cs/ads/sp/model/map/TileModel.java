package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.enums.TileType;

import java.util.ArrayList;

public class TileModel {

    private TileType type;

    ArrayList<GameObject> inhabitants = new ArrayList<>();

    private boolean hasCollision = false;

    public TileModel(int type) {
        initTile(type);
    }

    private void initTile(int type) {
        //Switch type
        switch (type) {
            case 0:
                this.type = TileType.GRASS;
                break;
            case 1:
                this.type = TileType.STONE;
                hasCollision = true;
                break;
            case 2:
                this.type = TileType.WATER;
                break;
            default:
                this.type = TileType.UNKNOWN;
                break;
        }
    }

    public boolean hasCollision() {
        return this.hasCollision;
    }

    public TileType getType() {
        return this.type;
    }

    public ArrayList<GameObject> getInhabitants() {
        return inhabitants;
    }

    public void removeInhabitant(Long id) {
        inhabitants.removeIf(gameObject -> gameObject.getId() == id);
    }

    public void setInhabitant(GameObject inhabitant) {
        if (!inhabitants.contains(inhabitant)) {
            inhabitants.add(inhabitant);
        }
    }
}