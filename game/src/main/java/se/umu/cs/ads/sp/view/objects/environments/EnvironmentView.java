package se.umu.cs.ads.sp.view.objects.environments;

import se.umu.cs.ads.sp.util.Position;
import se.umu.cs.ads.sp.view.objects.ObjectView;

public abstract class EnvironmentView extends ObjectView {

    public EnvironmentView(long id, Position position) {
        super(id, position);
    }

    public void update() {
        this.animator.update();
    }

}
