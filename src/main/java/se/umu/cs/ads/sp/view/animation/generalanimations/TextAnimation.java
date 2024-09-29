package se.umu.cs.ads.sp.view.animation.generalanimations;

import javax.swing.*;
import java.awt.*;

public class TextAnimation extends JLabel {

    private String text = "";
    private int alpha = 0;
    private int increment = 1;
    private TextState state;
    private boolean animationComplete = false;

    //Used to decide how long it waits until it fades away
    private float duration = 100f;
    private final float durationDecremenet = 0.1f;

    public TextAnimation(String text) {
        this.setText(text);
        this.setFont(new Font("Arial", Font.BOLD, 50));
        this.setForeground(new Color(77, 54, 207, alpha));
        this.state = TextState.START;}

    public boolean hasCompleted(){
        return animationComplete;
    }

    public void update(){
        switch (this.state){
            case START:
                alpha += increment;
                if (alpha >= 255) {
                    alpha = 255;
                    state = TextState.DISPLAY;
                }
                break;
            case DISPLAY:
                duration -= durationDecremenet;
                if(duration <= 0){
                    state = TextState.FINISHED;
                }
                break;
            case FINISHED:
                alpha -= increment;
                if (alpha <= 0) {
                    alpha = 0;
                    animationComplete = true;
                }
        }
        this.setForeground(new Color(251 ,251, 0, alpha));
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        super.paintComponent(g2d);

        g2d.dispose();
    }


    private enum TextState {
        START, DISPLAY, FINISHED
    }

}

