package se.umu.cs.ads.sp.view.frameComponents.panels.gamepanel.tiles;

import se.umu.cs.ads.sp.view.util.ImageLoader;

public class StoneTile extends TileView{
    @Override
    void initImages() {
        // Load the regular tile images.
        this.images.put("111111111", ImageLoader.loadImage("/sprites/tiles/stone.png"));

        this.images.put("011011011", ImageLoader.loadImage("/sprites/tiles/stone2.png"));
        this.images.put("111011011", ImageLoader.loadImage("/sprites/tiles/stone2.png"));
        this.images.put("011011111", ImageLoader.loadImage("/sprites/tiles/stone2.png"));

        this.images.put("110110110", ImageLoader.loadImage("/sprites/tiles/stone3.png"));
        this.images.put("110110111", ImageLoader.loadImage("/sprites/tiles/stone3.png"));
        this.images.put("111110110", ImageLoader.loadImage("/sprites/tiles/stone3.png"));

        this.images.put("000111111", ImageLoader.loadImage("/sprites/tiles/stone4.png"));
        this.images.put("100111111", ImageLoader.loadImage("/sprites/tiles/stone4.png"));
        this.images.put("001111111", ImageLoader.loadImage("/sprites/tiles/stone4.png"));

        this.images.put("111111000", ImageLoader.loadImage("/sprites/tiles/stone5.png"));
        this.images.put("111111100", ImageLoader.loadImage("/sprites/tiles/stone5.png"));
        this.images.put("111111001", ImageLoader.loadImage("/sprites/tiles/stone5.png"));

        this.images.put("010010010", ImageLoader.loadImage("/sprites/tiles/stone6.png"));
        this.images.put("000111000", ImageLoader.loadImage("/sprites/tiles/stone7.png"));
        this.images.put("000011011", ImageLoader.loadImage("/sprites/tiles/stone8.png"));
        this.images.put("000110110", ImageLoader.loadImage("/sprites/tiles/stone9.png"));
        this.images.put("011011000", ImageLoader.loadImage("/sprites/tiles/stone10.png"));
        this.images.put("110110000", ImageLoader.loadImage("/sprites/tiles/stone11.png"));
        this.images.put("000010000", ImageLoader.loadImage("/sprites/tiles/stone12.png"));
        this.images.put("011111111", ImageLoader.loadImage("/sprites/tiles/stone13.png"));
        this.images.put("110111111", ImageLoader.loadImage("/sprites/tiles/stone14.png"));
        this.images.put("111111011", ImageLoader.loadImage("/sprites/tiles/stone15.png"));
        this.images.put("111111110", ImageLoader.loadImage("/sprites/tiles/stone16.png"));

        // Load all the dark variants of the images.
        this.darkImages.put("111111111", ImageLoader.loadImage("/sprites/tiles/stoneDark.png"));
        this.darkImages.put("011011011", ImageLoader.loadImage("/sprites/tiles/stoneDark2.png"));
        this.darkImages.put("111011011", ImageLoader.loadImage("/sprites/tiles/stoneDark2.png"));
        this.darkImages.put("011011111", ImageLoader.loadImage("/sprites/tiles/stoneDark2.png"));

        this.darkImages.put("110110110", ImageLoader.loadImage("/sprites/tiles/stoneDark3.png"));
        this.darkImages.put("110110111", ImageLoader.loadImage("/sprites/tiles/stoneDark3.png"));
        this.darkImages.put("111110110", ImageLoader.loadImage("/sprites/tiles/stoneDark3.png"));

        this.darkImages.put("000111111", ImageLoader.loadImage("/sprites/tiles/stoneDark4.png"));
        this.darkImages.put("100111111", ImageLoader.loadImage("/sprites/tiles/stoneDark4.png"));
        this.darkImages.put("001111111", ImageLoader.loadImage("/sprites/tiles/stoneDark4.png"));

        this.darkImages.put("111111000", ImageLoader.loadImage("/sprites/tiles/stoneDark5.png"));
        this.darkImages.put("111111100", ImageLoader.loadImage("/sprites/tiles/stoneDark5.png"));
        this.darkImages.put("111111001", ImageLoader.loadImage("/sprites/tiles/stoneDark5.png"));
        this.darkImages.put("010010010", ImageLoader.loadImage("/sprites/tiles/stoneDark6.png"));
        this.darkImages.put("000111000", ImageLoader.loadImage("/sprites/tiles/stoneDark7.png"));
        this.darkImages.put("000011011", ImageLoader.loadImage("/sprites/tiles/stoneDark8.png"));
        this.darkImages.put("000110110", ImageLoader.loadImage("/sprites/tiles/stoneDark9.png"));
        this.darkImages.put("011011000", ImageLoader.loadImage("/sprites/tiles/stoneDark10.png"));
        this.darkImages.put("110110000", ImageLoader.loadImage("/sprites/tiles/stoneDark11.png"));
        this.darkImages.put("000010000", ImageLoader.loadImage("/sprites/tiles/stoneDark12.png"));
        this.darkImages.put("011111111", ImageLoader.loadImage("/sprites/tiles/stoneDark13.png"));
        this.darkImages.put("110111111", ImageLoader.loadImage("/sprites/tiles/stoneDark14.png"));
        this.darkImages.put("111111011", ImageLoader.loadImage("/sprites/tiles/stoneDark15.png"));
        this.darkImages.put("111111110", ImageLoader.loadImage("/sprites/tiles/stoneDark16.png"));

    }
}
