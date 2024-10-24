package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.util.enums.TileType;

import java.util.ArrayList;

public class TileModel {

    private TileType type;

    ArrayList<GameObject> inhabitants = new ArrayList<>();

    private boolean hasCollision = false;

    public TileModel(int type) {
        initTile(type);
    }

    private void initTile(int type) {
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
                hasCollision = true;
                break;
            default:
                System.out.println("Random tile detected when initializing the map!");
                hasCollision = true;
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

    public synchronized void removeInhabitant(Long id) {
        inhabitants.removeIf(gameObject -> gameObject != null && gameObject.getId() == id);
    }

    public synchronized void setInhabitant(GameObject inhabitant) {
        if (!inhabitants.contains(inhabitant)) {
            inhabitants.add(inhabitant);
        }
    }

    public void clearInhabitants() {
        this.inhabitants.clear();
    }
}
