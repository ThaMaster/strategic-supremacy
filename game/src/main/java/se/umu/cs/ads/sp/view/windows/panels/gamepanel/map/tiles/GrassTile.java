package se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.tiles;

import se.umu.cs.ads.sp.view.util.ImageLoader;

public class GrassTile extends TileView {
    @Override
    void initImages() {
        this.images.put("000000000", ImageLoader.loadImage("/sprites/tiles/grass.png"));
        this.darkImages.put("000000000", ImageLoader.loadImage("/sprites/tiles/grassDark.png"));
    }
}
