package se.umu.cs.ads.sp.view.panels.gamepanel;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.EntityView;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    private GameController gController;
    private ArrayList<EntityView> entities;

    public GamePanel() {
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        entities = new ArrayList<>();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setFocusable(true);  // Ensure the panel can receive key events
        this.addKeyListener(this); // Add KeyListener
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            gController.setSelection(new Position(e.getX(), e.getY()));
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            gController.setEntityPosition(new Position(e.getX(), e.getY()));
        }
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

    public void updateEntityPositions(ArrayList<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            this.entities.get(i).setPosition(entities.get(i).getPosition());
        }
    }

    public void setGameController(GameController gc) {
        this.gController = gc;
    }

    // KeyListener methods
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1:
                gController.setSelection(0);
                break;
            case KeyEvent.VK_2:
                gController.setSelection(1);
                break;
            case KeyEvent.VK_3:
                gController.setSelection(2);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}
