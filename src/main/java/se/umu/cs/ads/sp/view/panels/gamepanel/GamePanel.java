package se.umu.cs.ads.sp.view.panels.gamepanel;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.view.animation.generalanimations.TextAnimation;
import se.umu.cs.ads.sp.view.objects.collectables.ChestView;
import se.umu.cs.ads.sp.view.objects.collectables.CollectableView;
import se.umu.cs.ads.sp.view.objects.collectables.GoldView;
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
    private HashMap<Long, EntityView> gameEntitiesView = new HashMap<>();
    private HashMap<Long, CollectableView> collectables = new HashMap<>();
    private SoundManager soundManager;
    private final int edgeThreshold = 50;

    private Point startDragPoint;
    private Point endDragPoint;

    private ArrayList<TextAnimation> textAnimations = new ArrayList<>();

    public GamePanel(TileManager tm) {
        this.soundManager = new SoundManager();
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.cameraWorldPosition = new Position((UtilView.screenWidth / 2), (UtilView.screenHeight / 2));

        this.tileManager = tm;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setFocusable(true);  // Ensure the panel can receive key events
        this.addKeyListener(this); // Add KeyListener
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Only select a single unit if the user clicked
        // Convert the mouse screen coordinates to world coordinates.
        int worldX = e.getX() - UtilView.screenX + cameraWorldPosition.getX();
        int worldY = e.getY() - UtilView.screenY + cameraWorldPosition.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            gController.setSelection(new Position(worldX, worldY));

            for (long key : gController.getSelectedUnits()) {
                gameEntitiesView.get(key).setSelected(true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Convert the mouse screen coordinates to world coordinates.
        int worldX = e.getX() - UtilView.screenX + cameraWorldPosition.getX();
        int worldY = e.getY() - UtilView.screenY + cameraWorldPosition.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            for (long key : gController.getSelectedUnits()) {
                gameEntitiesView.get(key).setSelected(false);
            }
            startDragPoint = e.getPoint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            boolean allowedDestination = gController.setEntityDestination(new Position(worldX, worldY));

            if (allowedDestination && !gController.getSelectedUnits().isEmpty()) {
                //20 % chance we play move sound
                if (Utils.getRandomSuccess(80)) {
                    soundManager.playMove();
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (startDragPoint != null && endDragPoint != null) {
            int x = Math.min(startDragPoint.x, endDragPoint.x);
            int y = Math.min(startDragPoint.y, endDragPoint.y);
            int width = Math.abs(startDragPoint.x - endDragPoint.x);
            int height = Math.abs(startDragPoint.y - endDragPoint.y);
            Rectangle area = new Rectangle(x - UtilView.screenX + cameraWorldPosition.getX(), y - UtilView.screenY + cameraWorldPosition.getY(), width, height);
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
        if (e.getButton() != 0) {
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
        for (long unitIds : gController.getSelectedUnits()) {
            gameEntitiesView.get(unitIds).setSelected(false);
        }
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
                gController.stopSelectedEntities();
                break;
            case KeyEvent.VK_RIGHT:
                cameraWorldPosition.setX(cameraWorldPosition.getX() + 10);
                break;
            case KeyEvent.VK_LEFT:
                if(cameraWorldPosition.getX() < 0)
                    return;
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
        for (EntityView entity : gameEntitiesView.values()) {
            entity.draw(g2d, cameraWorldPosition);
        }

        for (TextAnimation animation : textAnimations){
            if(animation.hasCompleted()){
                this.textAnimations.remove(animation);
                this.remove(animation);
                break;
            }
            animation.update();
        }

        // Draw selection box
        if (startDragPoint != null && endDragPoint != null) {
            drawDragBox(g2d);
        }
    }

    private void drawDragBox(Graphics2D g2d) {
        int x = Math.min(startDragPoint.x, endDragPoint.x);
        int y = Math.min(startDragPoint.y, endDragPoint.y);
        int width = Math.abs(startDragPoint.x - endDragPoint.x);
        int height = Math.abs(startDragPoint.y - endDragPoint.y);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

        // fill
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(x, y, width, height);

        // Border
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(Color.ORANGE);
        g2d.drawRect(x, y, width, height);
    }

    public void setEntities(HashMap<Long, Entity> entities) {
        this.gameEntitiesView.clear();
        for (Entity entity : entities.values()) {
            EntityView newEntity = new PlayerUnitView(entity.getId(), entity.getPosition());
            newEntity.setSelected(entity.isSelected());
            this.gameEntitiesView.put(newEntity.getId(), newEntity);
        }
    }

    public void setCollectables(HashMap<Long, Collectable> collectables) {
        this.collectables.clear();
        CollectableView newCollectable = null;
        for (Collectable collectable : collectables.values()) {
            if(collectable instanceof Chest) {
                newCollectable = new ChestView(collectable.getId(), collectable.getPosition());
            }
            else if(collectable instanceof Gold){
                collectable.getPosition().printPosition("Gold");
                newCollectable = new GoldView(collectable.getId(), collectable.getPosition());
            }
            if(newCollectable != null) {
                this.collectables.put(collectable.getId(), newCollectable);
            }
        }
    }

    public void updateCollectables() {
        for (CollectableView collectable : collectables.values()) {
            collectable.update();
        }
    }

    public void updateEntityViews(HashMap<Long, Entity> entities) {
        for (Entity entityModel : entities.values()) {
            this.gameEntitiesView.get(entityModel.getId()).setEntityState(entityModel.getState());
            this.gameEntitiesView.get(entityModel.getId()).setPosition(entityModel.getPosition());
            this.gameEntitiesView.get(entityModel.getId()).setDestination(entityModel.getDestination());
            this.gameEntitiesView.get(entityModel.getId()).setSelected(entityModel.isSelected());
            this.gameEntitiesView.get(entityModel.getId()).update();
        }
    }

    public void setGameController(GameController gc) {
        this.gController = gc;
    }

    public void moveCamera(int xAmount, int yAmount) {
        if(canMoveCameraHorizontaly(xAmount)){
            cameraWorldPosition.setX(cameraWorldPosition.getX() + xAmount);
        }
        if(canMoveCameraVertically(yAmount)){
            cameraWorldPosition.setY(cameraWorldPosition.getY() + yAmount);
        }
    }
    private boolean canMoveCameraVertically(int yAmount){
        if(cameraWorldPosition.getY() + yAmount < 0){
            return false;
        }
        if((cameraWorldPosition.getY() + yAmount > Constants.TILE_HEIGHT * tileManager.getNumCols())){
            return false;
        }

        return true;
    }

    private boolean canMoveCameraHorizontaly(int xAmount){
        if(cameraWorldPosition.getX() + xAmount < 0){
            return false;
        }
        return cameraWorldPosition.getX() + xAmount <= Constants.TILE_WIDTH * tileManager.getNumRows();
    }

    public void setCameraWorldPosition(int xAmount, int yAmount) {
        cameraWorldPosition.setX(xAmount);
        cameraWorldPosition.setY(yAmount);
    }

    public void performPickUp(long collectable, String reward) {

        if(!this.collectables.containsKey(collectable)){
            return;
        }
        if(this.collectables.get(collectable).hasBeenCollected()){
            return;
        }

        CollectableView collectableView = this.collectables.get(collectable);
        collectableView.pickup();

        if(collectableView instanceof ChestView){
            soundManager.play(SoundFX.OPEN_CHEST);
        }else if(collectableView instanceof GoldView){
            soundManager.play(SoundFX.GOLD);
            this.collectables.remove(collectable);
        }

        TextAnimation newAnim = new TextAnimation(reward);
        this.textAnimations.add(newAnim);
        this.add(newAnim);
        this.revalidate();
        this.repaint();

    }
}
