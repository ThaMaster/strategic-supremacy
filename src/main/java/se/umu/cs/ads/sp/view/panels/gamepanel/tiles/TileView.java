package se.umu.cs.ads.sp.view.panels.gamepanel.tiles;

import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.enums.TileType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileView {

    private final String variant;
    private final BufferedImage image;

    public TileView(String variant, BufferedImage tileImage) {
        this.variant = variant;
        this.image = tileImage;
    }

    public String getVariant() {
        return this.variant;
    }

    public BufferedImage getImage() {
        return this.image;
    }

//    public void draw(Graphics2D g2d, int x, int y) {
//        g2d.drawImage(image, x, y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT, null);
//    }
}
