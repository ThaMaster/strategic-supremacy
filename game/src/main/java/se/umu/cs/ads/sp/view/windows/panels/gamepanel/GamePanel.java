package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.utils.enums.EventColor;
import se.umu.cs.ads.sp.utils.enums.EventType;
import se.umu.cs.ads.sp.view.animation.generalanimations.TextAnimation;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.tiles.MiniMap;
import se.umu.cs.ads.sp.view.objects.EnvironmentView;
import se.umu.cs.ads.sp.view.objects.collectables.ChestView;
import se.umu.cs.ads.sp.view.objects.collectables.CollectableView;
import se.umu.cs.ads.sp.view.objects.collectables.GoldView;
import se.umu.cs.ads.sp.view.objects.entities.EntityView;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.tiles.TileManager;
import se.umu.cs.ads.sp.view.soundmanager.SoundFX;
import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.util.StyleConstants;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    // TODO: Handle the camera world position in a better way, how i do not know...
    private Position cameraWorldPosition;

    private TileManager tileManager;
    private GameController gController;

    private HashMap<Long, PlayerUnitView> myUnitsView = new HashMap<>();
    private HashMap<Long, EntityView> gameEntitiesView = new HashMap<>();
    private HashMap<Long, CollectableView> collectables = new HashMap<>();
    private SoundManager soundManager;
    private final int edgeThreshold = 50;

    private Point startDragPoint;
    private Point endDragPoint;

    private ArrayList<TextAnimation> textAnimations = new ArrayList<>();
    private EnvironmentView goldPile;

    public GamePanel(TileManager tm) {
        this.soundManager = new SoundManager();
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.cameraWorldPosition = new Position((int) ((UtilView.screenWidth / 2) * UtilView.scale), (int) ((UtilView.screenHeight / 2) * UtilView.scale));

        this.tileManager = tm;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.setFocusable(true);  // Ensure the panel can receive key events
        this.addKeyListener(this); // Add KeyListener
        this.goldPile = new EnvironmentView(52, new Position(200, 200));
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
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
                if (cameraWorldPosition.getX() < 0)
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
        MiniMap miniMap =  tileManager.draw(g2d, cameraWorldPosition);

        // Draw collectables
        for (CollectableView collectableView : collectables.values()) {
            if (tileManager.isInFow(collectableView.getPosition())) {
                miniMap.addPoint(goldPile.getPosition(), Color.BLACK, 50);
                collectableView.draw(g2d, cameraWorldPosition);
            }
        }

        if (tileManager.isInFow(this.goldPile.getPosition())) {
            miniMap.addPoint(goldPile.getPosition(), StyleConstants.GOLD_COLOR, 100);
            this.goldPile.draw(g2d, cameraWorldPosition);
        }

        // Draw entities
        for (EntityView entity : gameEntitiesView.values()) {
            if (tileManager.isInFow(entity.getPosition())) {
                if(myUnitsView.containsKey(entity.getId())) {
                    miniMap.addPoint(entity.getPosition(), StyleConstants.ALLY_COLOR, 50);
                } else {
                    miniMap.addPoint(entity.getPosition(), StyleConstants.ENEMY_COLOR, 50);
                }
                entity.draw(g2d, cameraWorldPosition);
            }
        }

        collectEvents();

        // Draw selection box
        if (startDragPoint != null && endDragPoint != null) {
            drawDragBox(g2d);
        }

        // === DRAW MINIMAP ===
        miniMap.draw(g2d);

        for (TextAnimation animation : textAnimations) {
            if (animation.hasCompleted()) {
                this.textAnimations.remove(animation);
                this.remove(animation);
                break;
            }
            animation.update();
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

    public void setEntities(HashMap<Long, PlayerUnit> myUnits, HashMap<Long, Entity> entities) {
        this.myUnitsView.clear();
        this.gameEntitiesView.clear();

        for (PlayerUnit unit : myUnits.values()) {
            PlayerUnitView newUnit = new PlayerUnitView(unit.getId(), unit.getPosition());
            newUnit.setSelected(unit.isSelected());
            this.myUnitsView.put(newUnit.getId(), newUnit);
        }
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
            if (collectable instanceof Chest) {
                newCollectable = new ChestView(collectable.getId(), collectable.getPosition());
            } else if (collectable instanceof Gold) {
                collectable.getPosition().printPosition("Gold");
                newCollectable = new GoldView(collectable.getId(), collectable.getPosition());
                newCollectable.setCollisionBox(collectable.getCollisionBox());
            }
            if (newCollectable != null) {
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
            if (entityModel instanceof PlayerUnit modelUnit) {
                this.gameEntitiesView.get(entityModel.getId()).setEntityState(modelUnit.getState());
                this.gameEntitiesView.get(entityModel.getId()).setPosition(modelUnit.getPosition());
                this.gameEntitiesView.get(entityModel.getId()).setDestination(modelUnit.getDestination());
                this.gameEntitiesView.get(entityModel.getId()).setSelected(modelUnit.isSelected());
                this.gameEntitiesView.get(entityModel.getId()).setAttackRange(modelUnit.getAttackRange());
                this.gameEntitiesView.get(entityModel.getId()).setCollisionBox(modelUnit.getCollisionBox());
                this.gameEntitiesView.get(entityModel.getId()).setInRange(modelUnit.isInAttackRange());
                this.gameEntitiesView.get(entityModel.getId()).setHasAttacked(modelUnit.hasAttacked());
                this.gameEntitiesView.get(entityModel.getId()).setHasBeenHit(modelUnit.hasBeenHit());
                this.gameEntitiesView.get(entityModel.getId()).update();
                if (myUnitsView.containsKey(modelUnit.getId())) {
                    PlayerUnitView myUnit = (PlayerUnitView) gameEntitiesView.get(entityModel.getId());
                    this.myUnitsView.put(entityModel.getId(), myUnit);
                }
            }
        }
        tileManager.updateFowView(new ArrayList<>(myUnitsView.values()));
    }

    public void setGameController(GameController gc) {
        this.gController = gc;
    }

    public void moveCamera(int xAmount, int yAmount) {
        if (canMoveCameraHorizontaly(xAmount)) {
            cameraWorldPosition.setX(cameraWorldPosition.getX() + xAmount);
        }
        if (canMoveCameraVertically(yAmount)) {
            cameraWorldPosition.setY(cameraWorldPosition.getY() + yAmount);
        }
    }

    private boolean canMoveCameraVertically(int yAmount) {
        if (cameraWorldPosition.getY() + yAmount < 0) {
            return false;
        }
        if ((cameraWorldPosition.getY() + yAmount > Constants.TILE_HEIGHT * tileManager.getNumCols())) {
            return false;
        }

        return true;
    }

    private boolean canMoveCameraHorizontaly(int xAmount) {
        if (cameraWorldPosition.getX() + xAmount < 0) {
            return false;
        }
        return cameraWorldPosition.getX() + xAmount <= Constants.TILE_WIDTH * tileManager.getNumRows();
    }

    public void setCameraWorldPosition(int xAmount, int yAmount) {
        cameraWorldPosition.setX(xAmount);
        cameraWorldPosition.setY(yAmount);
    }

    private void collectEvents() {
        ArrayList<GameEvent> events = GameEvents.getInstance().getEvents();
        for (GameEvent event : events) {
            switch (event.getType()) {
                case COLLECT:
                    addTextEvent(event, 15, EventColor.SUCCESS);
                    break;
                case NEW_ROUND:
                    addTextEvent(event, 15, EventColor.ALERT);
                    break;
                case LOGG:
                    addTextEvent(event, 15, EventColor.DEFAULT);
                    break;
                case MINE_DEPLETED:
                    // Change this to hashmap and get the goldpile with specific id.
                    this.goldPile.setDepleted(true);
                    break;
                case MINING:
                    break;
                case DEATH:
                    soundManager.play("DEATH");
                    break;
                case ATTACK:
                    soundManager.play("ATTACK");
                    break;
                case TAKE_DMG:
                    soundManager.play("TAKE_DMG");
                    break;
                default:
                    //This is default case, it's a collectable we have stored
                    addCollectableEvent(event);
                    break;
            }
        }
        GameEvents.getInstance().clearEvents();
    }

    private void addTextEvent(GameEvent event, int size, EventColor color) {
        TextAnimation textAnim = new TextAnimation(event.getEvent());
        textAnim.setSize(size);
        textAnim.setDisplayTime(2);
        textAnim.setColor(color);
        this.textAnimations.add(textAnim);
        this.add(textAnim);
        this.revalidate();
        this.repaint();
    }

    public void addCollectableEvent(GameEvent event) {
        if (!this.collectables.containsKey(event.getId())) {
            //Collected something that doesn't need an animation, forexample mining gold
            if (event.getType() == EventType.GOLD_PICK_UP) {
                //Inc displayed gold?
            }
            addTextEvent(event, 25, EventColor.SUCCESS);
            return;
        }

        if (this.collectables.get(event.getId()).hasBeenCollected()) {
            return;
        }

        CollectableView collectableView = this.collectables.get(event.getId());
        collectableView.pickup();

        if (collectableView instanceof ChestView) {
            soundManager.play(SoundFX.OPEN_CHEST);
        } else if (collectableView instanceof GoldView) {
            soundManager.play(SoundFX.GOLD);
            this.collectables.remove(event.getId());
        }
        addTextEvent(event, 25, EventColor.SUCCESS);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() > 0) {
                UtilView.changeScale(-0.01);
            } else {
                UtilView.changeScale(0.01);
            }
        }
    }
}
