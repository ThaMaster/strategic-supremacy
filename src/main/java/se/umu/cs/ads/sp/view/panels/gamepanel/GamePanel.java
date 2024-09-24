package se.umu.cs.ads.sp.view.panels.gamepanel;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.EntityView;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener {

    private GameController gController;
    private ArrayList<EntityView> entities;

    public GamePanel() {
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        entities = new ArrayList<>();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Setting next position: (" + e.getX() + ", " + e.getY() + ")");
        gController.setEntityPosition(new Position(e.getX(), e.getY()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        for (EntityView entity : entities) {
            entity.draw(g2d);
        }
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities.clear();
        for (Entity entity : entities) {
            EntityView newEntity = new EntityView();
            newEntity.setPosition(entity.getPosition());
            this.entities.add(newEntity);
        }
    }

    public void setGameController(GameController gc) {
        this.gController = gc;
    }
}
