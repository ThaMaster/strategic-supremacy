package se.umu.cs.ads.sp.model.map;

import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.model.objects.collectibles.Collectable;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.TileType;

import java.util.ArrayList;

public class TileModel {

    private TileType type;

    Entity entity;
    Collectable collectable;

    private CollisionBox collisionBox;
    private boolean hasCollision = false;

    public TileModel(int type, int width, int height) {
        initTile(type);
    }

    private void initTile(int type){
        //Switch type
        switch (type) {
            case 0:
                this.type = TileType.GRASS;
                break;
            case 1:
                this.type = TileType.STONE;
                break;
            case 2:
                this.type = TileType.WATER;
                break;
            default:
                this.type = TileType.UNKNOWN;
                break;
        }
    }

    public void enableCollision(boolean enable) {
        this.hasCollision = enable;
    }

    public boolean hasCollision() {
        return this.hasCollision;
    }

    public TileType getType() {
        return this.type;
    }

    public GameObject getInhabitant(){
        if(entity != null){
            return entity;
        }else if(collectable != null){
            return collectable;
        }
        return null;
    }

    public void removeInhabitant() {
        entity = null;
    }

    public void removeCollectable() {
        collectable = null;
    }

    public void setInhabitant(GameObject inhabitant){
        if(inhabitant instanceof Entity){
            entity = (Entity) inhabitant;
        }else if(inhabitant instanceof Collectable){
            collectable = (Collectable) inhabitant;
        }
    }

}
