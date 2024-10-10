package se.umu.cs.ads.sp.model.objects.Environment;

import se.umu.cs.ads.sp.model.objects.GameObject;
import se.umu.cs.ads.sp.utils.Position;

public abstract class Environment extends GameObject {

    public Environment(Position pos) {
        super(pos);
    }
}
