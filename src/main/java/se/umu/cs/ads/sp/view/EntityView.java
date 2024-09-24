package se.umu.cs.ads.sp.view;

import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.animation.Animator;

import java.awt.*;
import java.util.ArrayList;


public class EntityView {

    private Position position;
    private Animator animator;

    //Temp
    private ArrayList<Color> colors = new ArrayList<>();
    private final Color color;

    public EntityView() {

        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.BLUE);

        color = colors.get((int) (Math.random() * 3));

        this.animator = new Animator();
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillRect(position.getX(), position.getY(), 16, 16);
    }
}
