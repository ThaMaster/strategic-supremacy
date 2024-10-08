package se.umu.cs.ads.sp.view.objects;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EnvironmentView {

    private final Position pos;
    private long id;
    private BufferedImage image;
    private boolean depleted;

    public EnvironmentView(long id, Position position) {
        this.id = id;
        this.pos = position;
        init();
    }

    public void init(){
        this.image = ImageLoader.loadImage("/sprites/environment/goldPile.png");
        if(image == null){
            System.out.println("IMAGE IS NULL");
        }
    }

    public void draw(Graphics2D g2d, Position position) {
        if(image == null){
            return;
        }
        int screenX = pos.getX() - position.getX() + UtilView.screenX;
        int screenY = pos.getY() - position.getY() + UtilView.screenY;
        if(!depleted) {
            g2d.drawImage(image, screenX, screenY, UtilView.tileSize, UtilView.tileSize, null);
        }
    }

    public void setDepleted(boolean bool) {
        this.depleted = bool;
    }

    public Position getPosition() {
        return pos;
    }
}
