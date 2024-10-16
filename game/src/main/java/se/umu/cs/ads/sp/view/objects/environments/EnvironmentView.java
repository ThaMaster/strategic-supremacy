package se.umu.cs.ads.sp.view.objects.environments;

import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.objects.ObjectView;
import se.umu.cs.ads.sp.view.util.ImageLoader;
import se.umu.cs.ads.sp.view.util.UtilView;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class EnvironmentView extends ObjectView {

    public EnvironmentView(long id, Position position) {
        super(id, position);
    }

    public void update() {
        this.animator.update();
    }

}
