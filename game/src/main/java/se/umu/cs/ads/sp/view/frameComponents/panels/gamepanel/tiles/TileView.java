package se.umu.cs.ads.sp.view.frameComponents.panels.gamepanel.tiles;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class TileView {

    protected final HashMap<String, BufferedImage> images;
    protected final HashMap<String, BufferedImage> darkImages;

    public TileView() {
        this.images = new HashMap<>();
        this.darkImages = new HashMap<>();
        initImages();
    }

    public BufferedImage getImage(String mode, String imageVariant) {
        if (mode.equals("dark"))
            return darkImages.get(imageVariant);
        return this.images.get(imageVariant);
    }

    abstract void initImages();
}
