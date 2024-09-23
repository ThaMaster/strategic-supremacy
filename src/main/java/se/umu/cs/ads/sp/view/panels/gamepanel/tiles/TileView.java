package se.umu.cs.ads.sp.view.panels.gamepanel.tiles;

import se.umu.cs.ads.sp.utils.enums.TileType;

import java.awt.image.BufferedImage;

public class TileView {

    private final String variant;
    private final TileType type;
    private final BufferedImage image;

    public TileView(String variant, TileType type, BufferedImage tileImage) {
        this.variant = variant;
        this.type = type;
        this.image = tileImage;
    }

    public String getVariant() {
        return this.variant;
    }

    public TileType getType() {
        return this.type;
    }

    public BufferedImage getImage() {
        return this.image;
    }
}
