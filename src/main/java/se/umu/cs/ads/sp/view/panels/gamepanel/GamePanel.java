package se.umu.cs.ads.sp.view.panels.gamepanel;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.view.objects.entities.EntityView;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
import se.umu.cs.ads.sp.view.panels.gamepanel.tiles.TileManager;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    // TODO: Handle the camera world position in a better way, how i do not know...
    private Position cameraWorldPosition;

    private TileManager tileManager;
    private GameController gController;
    private ArrayList<EntityView> entities;

    private final int edgeThreshold = 50;

    public GamePanel(TileManager tm) {
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.cameraWorldPosition = new Position((UtilView.screenWidth / 2), (UtilView.screenHeight / 2));

        this.tileManager = tm;
        entities = new ArrayList<>();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setFocusable(true);  // Ensure the panel can receive key events
        this.addKeyListener(this); // Add KeyListener
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Convert the mouse screen coordinates to world coordinates.
        int worldX = e.getX() - UtilView.screenX + cameraWorldPosition.getX();
        int worldY = e.getY() - UtilView.screenY + cameraWorldPosition.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            entities.get(gController.getSelectedUnit()).setSelected(false);
            gController.setSelection(new Position(worldX, worldY));
            entities.get(gController.getSelectedUnit()).setSelected(true);

        } else if (e.getButton() == MouseEvent.BUTTON3) {
            gController.setEntityDestination(new Position(worldX, worldY));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Not used yet

        // Maybe be able to drag a box to select multiple units?
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Get the size of the panel (or the component)
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Check if the mouse is within the edgeThreshold from the edge
        if (mouseX < edgeThreshold) {
            gController.setCameraPanningDirection(Direction.WEST);
        } else if (mouseX > panelWidth - edgeThreshold) {
            gController.setCameraPanningDirection(Direction.EAST);
        } else if (mouseY < edgeThreshold) {
            gController.setCameraPanningDirection(Direction.NORTH);
        } else if (mouseY > panelHeight - edgeThreshold) {
            gController.setCameraPanningDirection(Direction.SOUTH);
        } else {
            gController.setCameraPanningDirection(Direction.NONE);
        }
    }

    // KeyListener methods
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        entities.get(gController.getSelectedUnit()).setSelected(false);
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
            case KeyEvent.VK_S:
                gController.stopSelectedEntity();
                break;
            case KeyEvent.VK_RIGHT:
                cameraWorldPosition.setX(cameraWorldPosition.getX() + 10);
                break;
            case KeyEvent.VK_LEFT:
                cameraWorldPosition.setX(cameraWorldPosition.getX() - 10);
                break;
            case KeyEvent.VK_UP:
                cameraWorldPosition.setY(cameraWorldPosition.getY() - 10);
                break;
            case KeyEvent.VK_DOWN:
                cameraWorldPosition.setY(cameraWorldPosition.getY() + 10);
                break;
            default:
                break;
        }
        entities.get(gController.getSelectedUnit()).setSelected(true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        tileManager.draw(g2d, cameraWorldPosition);

        for (EntityView entity : entities) {
            entity.draw(g2d, cameraWorldPosition);
        }
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities.clear();
        for (Entity entity : entities) {
            EntityView newEntity = new PlayerUnitView();
            newEntity.setPosition(entity.getPosition());
            this.entities.add(newEntity);
        }
    }

    public void updateEntityViews(ArrayList<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            this.entities.get(i).setEntityState(entities.get(i).getState());
            this.entities.get(i).setPosition(entities.get(i).getPosition());
            this.entities.get(i).setDestination(entities.get(i).getDestination());
            this.entities.get(i).update();
        }
    }

    public void setGameController(GameController gc) {
        this.gController = gc;
    }

    public void moveCamera(int xAmount, int yAmount) {
        cameraWorldPosition.setX(cameraWorldPosition.getX() + xAmount);
        cameraWorldPosition.setY(cameraWorldPosition.getY() + yAmount);
    }
}
