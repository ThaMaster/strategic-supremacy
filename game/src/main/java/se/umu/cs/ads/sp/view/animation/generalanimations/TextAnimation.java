package se.umu.cs.ads.sp.view.animation.generalanimations;

import se.umu.cs.ads.sp.utils.Cooldown;
import se.umu.cs.ads.sp.utils.enums.EventColor;

import javax.swing.*;
import java.awt.*;

public class TextAnimation extends JLabel {

    private int alpha = 0;
    private int increment = 1;
    private TextState state;
    private boolean animationComplete = false;
    private Cooldown displayCooldown;
    //Used to decide how long it waits until it fades away
    private int fontSize = 50;

    private int red = 255;
    private int green = 204;
    private int blue = 51;

    public TextAnimation(String text) {
        this.setText(text);
        this.setFont(new Font("Arial", Font.BOLD, fontSize));
        this.setForeground(new Color(red, green, blue, alpha));
        this.state = TextState.START;
        displayCooldown = new Cooldown(2);
    }

    public void setDisplayTime(int seconds){
        displayCooldown = new Cooldown(seconds);
    }

    public void setSize(int size){
        this.fontSize = size;
        this.setFont(new Font("Arial", Font.BOLD, fontSize));
        this.setForeground(new Color(red, green, blue, alpha));
    }

    public void setColor(EventColor color){
        switch(color){
            case INFO:
                break;
            case SUCCESS:
                red = 250;
                green = 142;
                blue = 0;
                break;
            case ALERT:
                red = 23;
                green = 23; //Fix later
                blue = 23;
                break;
            case WARNING:
                red = 255;
                green = 0;
                blue = 0;
                break;
            case DEFAULT:
                red = 52;
                green = 52;
                blue = 52;
                break;
        }
    }


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
                    displayCooldown.start();
                }
                break;
            case DISPLAY:
                if(displayCooldown.hasElapsed()){
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
        this.setForeground(new Color(red ,green, blue, alpha));
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

