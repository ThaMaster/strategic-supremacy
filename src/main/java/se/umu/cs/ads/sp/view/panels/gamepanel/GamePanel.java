package se.umu.cs.ads.sp.view.panels.gamepanel;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.view.objects.collectables.ChestView;
import se.umu.cs.ads.sp.view.objects.collectables.CollectableView;
import se.umu.cs.ads.sp.view.objects.entities.EntityView;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
import se.umu.cs.ads.sp.view.panels.gamepanel.tiles.TileManager;
import se.umu.cs.ads.sp.view.soundmanager.SoundFX;
import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

    // TODO: Handle the camera world position in a better way, how i do not know...
    private Position cameraWorldPosition;

    private TileManager tileManager;
    private GameController gController;
    private ArrayList<EntityView> entities;
    //private ArrayList<CollectableView> collectables;
    private HashMap<Integer, CollectableView> collectables;
    private SoundManager soundManager;
    private final int edgeThreshold = 50;

    private Point startDragPoint;
    private Point endDragPoint;


    public GamePanel(TileManager tm) {
        this.soundManager = new SoundManager();
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.cameraWorldPosition = new Position((UtilView.screenWidth / 2), (UtilView.screenHeight / 2));

        this.tileManager = tm;
        entities = new ArrayList<>();
        collectables = new HashMap<>();

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
        // Convert the mouse screen coordinates to world coordinates.
        int worldX = e.getX() - UtilView.screenX + cameraWorldPosition.getX();
        int worldY = e.getY() - UtilView.screenY + cameraWorldPosition.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            //entities.get(gController.getSelectedUnit()).setSelected(false);
           // gController.setSelection(new Position(worldX, worldY));
            //entities.get(gController.getSelectedUnit()).setSelected(true);
            startDragPoint = e.getPoint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            gController.setEntityDestination(new Position(worldX, worldY));

            //30 % chance we play move sound
            if(Utils.getRandomSuccess(80)){
                soundManager.playMove();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(startDragPoint != null && endDragPoint != null) {
            int x = Math.min(startDragPoint.x, endDragPoint.x);
            int y = Math.min(startDragPoint.y, endDragPoint.y);
            int width = Math.abs(startDragPoint.x - endDragPoint.x);
            int height = Math.abs(startDragPoint.y - endDragPoint.y);
            Rectangle area = new Rectangle(x - UtilView.screenX + cameraWorldPosition.getX(),y - UtilView.screenY + cameraWorldPosition.getY(),width,height);
            gController.setSelectedUnit(area);
        }

        startDragPoint = null;
        endDragPoint = null;
        repaint();
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
        if(e.getButton() != 0){
            return;
        }
        endDragPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Get the size of the panel (or the component)
        int panelWidth = getWidth();
        int panelHeight = getHeight();

       // System.out.println("mouse x <- " + mouseX);
      //  System.out.println("panelWidth - edgeTreshold = " + (panelWidth-edgeThreshold));
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

        // Draw tile set
        tileManager.draw(g2d, cameraWorldPosition);

        // Draw collectables
        for (CollectableView collectableView : collectables.values()) {
            collectableView.draw(g2d, cameraWorldPosition);
        }

        // Draw entities
        for (EntityView entity : entities) {
            entity.draw(g2d, cameraWorldPosition);
        }

        if (startDragPoint != null && endDragPoint != null) {
            int x = Math.min(startDragPoint.x, endDragPoint.x);
            int y = Math.min(startDragPoint.y, endDragPoint.y);
            int width = Math.abs(startDragPoint.x - endDragPoint.x);
            int height = Math.abs(startDragPoint.y - endDragPoint.y);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

            //fill
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(x, y, width, height);

            //Border
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setColor(Color.ORANGE);
            g2d.drawRect(x, y, width, height);
        }


    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities.clear();
        for (Entity entity : entities) {
            EntityView newEntity = new PlayerUnitView(entity.getPosition());
            this.entities.add(newEntity);
        }
    }

    public void setCollectables(ArrayList<Collectable> collectables) {
        this.collectables.clear();
        for (Collectable collectable : collectables) {
            CollectableView newCollectable = new ChestView(collectable.getPosition());
            System.out.println(newCollectable.getPosition());
            this.collectables.put(0, newCollectable);
            //this.collectables.add(newCollectable);
        }
    }

    public void updateCollectables() {
        for(CollectableView collectable : collectables.values()) {
            collectable.update();
        }
        /*for(CollectableView collectableView : collectables) {
            collectableView.update();
        }*/
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

    public void setCameraWorldPosition(int xAmount, int yAmount) {
        cameraWorldPosition.setX(xAmount);
        cameraWorldPosition.setY(yAmount);
    }

    public void performPickUp(int collectable) {
        this.collectables.get(collectable).pickup();
        if(!this.collectables.get(collectable).hasPlayedSoundFx){
            soundManager.play(SoundFX.OPEN_CHEST);
            this.collectables.get(collectable).hasPlayedSoundFx = true;
        }
    }
}
