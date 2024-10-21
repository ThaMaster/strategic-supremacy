package se.umu.cs.ads.sp.view.windows.panels.gamepanel;

import se.umu.cs.ads.sp.controller.GameController;
import se.umu.cs.ads.sp.events.GameEvent;
import se.umu.cs.ads.sp.events.GameEvents;
import se.umu.cs.ads.sp.model.objects.collectables.Chest;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.collectables.Flag;
import se.umu.cs.ads.sp.model.objects.collectables.Gold;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.model.objects.entities.units.PlayerUnit;
import se.umu.cs.ads.sp.model.objects.environment.Base;
import se.umu.cs.ads.sp.model.objects.environment.Environment;
import se.umu.cs.ads.sp.model.objects.environment.GoldMine;
import se.umu.cs.ads.sp.utils.Constants;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.Utils;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.utils.enums.EventColor;
import se.umu.cs.ads.sp.utils.enums.EventType;
import se.umu.cs.ads.sp.view.animation.generalanimations.TextAnimation;
import se.umu.cs.ads.sp.view.objects.ObjectView;
import se.umu.cs.ads.sp.view.objects.collectables.ChestView;
import se.umu.cs.ads.sp.view.objects.collectables.CollectableView;
import se.umu.cs.ads.sp.view.objects.collectables.FlagView;
import se.umu.cs.ads.sp.view.objects.collectables.GoldView;
import se.umu.cs.ads.sp.view.objects.entities.EntityView;
import se.umu.cs.ads.sp.view.objects.entities.units.EnemyUnitView;
import se.umu.cs.ads.sp.view.objects.entities.units.PlayerUnitView;
import se.umu.cs.ads.sp.view.objects.environments.BaseView;
import se.umu.cs.ads.sp.view.objects.environments.EnvironmentView;
import se.umu.cs.ads.sp.view.objects.environments.GoldMineView;
import se.umu.cs.ads.sp.view.soundmanager.SoundFX;
import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.util.Camera;
import se.umu.cs.ads.sp.view.util.StyleConstants;
import se.umu.cs.ads.sp.view.util.UtilView;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.MiniMap;
import se.umu.cs.ads.sp.view.windows.panels.gamepanel.map.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    private TileManager tileManager;
    private GameController gController;

    private HashMap<Long, EntityView> gameEntitiesView = new HashMap<>();
    private HashMap<Long, CollectableView> collectables = new HashMap<>();
    private HashMap<Long, EnvironmentView> environments = new HashMap<>();

    private final int edgeThreshold = 50;

    private Point startDragPoint;
    private Point endDragPoint;

    private ArrayList<TextAnimation> textAnimations = new ArrayList<>();

    public GamePanel(TileManager tm) {
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.tileManager = tm;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.setFocusable(true);  // Ensure the panel can receive key events
        this.addKeyListener(this); // Add KeyListener
        this.setPreferredSize(new Dimension(UtilView.screenWidth, UtilView.screenHeight));
        this.setBounds(0, 0, UtilView.screenWidth, UtilView.screenHeight);
    }

    public void startGame() {
        this.requestFocusInWindow();
        SoundManager.getInstance().playMusic();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Only select a single unit if the user clicked
        // Convert the mouse screen coordinates to world coordinates.

        if (e.getButton() == MouseEvent.BUTTON1) {
            gController.setSelection(Camera.screenToWorld(e.getX(), e.getY()));

            for (long key : gController.getSelectedUnits()) {
                gameEntitiesView.get(key).setSelected(true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            for (long key : gController.getSelectedUnits()) {
                gameEntitiesView.get(key).setSelected(false);
            }
            startDragPoint = e.getPoint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            boolean allowedDestination = gController.setEntityDestination(Camera.screenToWorld(e.getX(), e.getY()));

            if (allowedDestination && !gController.getSelectedUnits().isEmpty()) {
                // 80 % chance we play move sound
                if (Utils.getRandomSuccess(80)) {
                    SoundManager.getInstance().playMove();
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

            Position dragWorldPosition = Camera.screenToWorld(x, y);
            Rectangle area = new Rectangle(dragWorldPosition.getX(), dragWorldPosition.getY(), width, height);
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
            case KeyEvent.VK_ESCAPE:
                gController.openQuitWindow();
                break;
            case KeyEvent.VK_RIGHT:
                Camera.setXPosition(Camera.getPosition().getX() + 20);
                break;
            case KeyEvent.VK_LEFT:
                if (Camera.getPosition().getX() < 0)
                    return;
                Camera.setXPosition(Camera.getPosition().getX() - 20);
                break;
            case KeyEvent.VK_UP:
                Camera.setYPosition(Camera.getPosition().getY() - 20);
                break;
            case KeyEvent.VK_DOWN:
                Camera.setYPosition(Camera.getPosition().getY() + 20);
                break;
            case KeyEvent.VK_B:
                gController.toggleShopWindow();
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
        MiniMap miniMap = tileManager.draw(g2d);

        // Collectable, Environment, and Entity rendering loop
        for (var collectableView : collectables.values()) {
            renderObject(g2d, miniMap, collectableView, Color.BLACK, 50);
        }
        for (var environmentView : environments.values()) {
            renderObject(g2d, miniMap, environmentView, StyleConstants.GOLD_COLOR, 100);
        }
        for (var entity : gameEntitiesView.values()) {
            Color entityColor = entity.isMyUnit ? StyleConstants.ALLY_COLOR : StyleConstants.ENEMY_COLOR;
            renderObject(g2d, miniMap, entity, entityColor, 50);
        }

        collectEvents();

        // Draw selection box
        if (startDragPoint != null && endDragPoint != null) {
            drawDragBox(g2d);
        }

        // Draw minimap
        miniMap.addCameraBox();
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

    private void renderObject(Graphics2D g2d, MiniMap miniMap, ObjectView entity, Color minimapColor, int pointSize) {
        if (tileManager.isInFow(entity.getPosition())) {
            miniMap.addPoint(entity.getPosition(), minimapColor, pointSize);
            entity.draw(g2d);
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

    public void setEntities(HashMap<Long, PlayerUnit> myUnits, HashMap<Long, PlayerUnit> enemyUnits) {
        this.gameEntitiesView.clear();

        for (PlayerUnit unit : myUnits.values()) {
            PlayerUnitView newUnit = new PlayerUnitView(unit.getId(), unit.getEntityName(), unit.getPosition());
            newUnit.setSelected(unit.isSelected());
            newUnit.isMyUnit = true;
            newUnit.setHealthBarValues(unit.getMaxHp(), unit.getCurrentHp());
            this.gameEntitiesView.put(newUnit.getId(), newUnit);
            Camera.setPosition(newUnit.getPosition());
        }

        for (PlayerUnit enemyUnit : enemyUnits.values()) {
            EnemyUnitView newUnit = new EnemyUnitView(enemyUnit.getId(), enemyUnit.getEntityName(), enemyUnit.getPosition());
            newUnit.setHealthBarValues(enemyUnit.getMaxHp(), enemyUnit.getCurrentHp());
            this.gameEntitiesView.put(newUnit.getId(), newUnit);
        }


    }

    public void setCollectables(HashMap<Long, Collectable> collectables) {
        System.out.println("Setting collectables!");
        this.collectables.clear();
        CollectableView newCollectableView = null;
        for (Collectable collectable : collectables.values()) {
            if (collectable instanceof Chest) {
                newCollectableView = new ChestView(collectable.getId(), collectable.getPosition());
            }
            else if (collectable instanceof Gold) {
                newCollectableView = new GoldView(collectable.getId(), collectable.getPosition());
                newCollectableView.setCollisionBox(collectable.getCollisionBox());
            }
            else if(collectable instanceof Flag){
                System.out.println("Got a flag view");
                newCollectableView = new FlagView(collectable.getId(), collectable.getPosition());
                newCollectableView.setCollisionBox(collectable.getCollisionBox());
            }

            if (newCollectableView != null) {
                this.collectables.put(collectable.getId(), newCollectableView);
            }
        }
    }

    public void setEnvironemnts(HashMap<Long, Environment> environments) {
        this.environments.clear();
        EnvironmentView newEnvironmentView = null;
        for (Environment environment : environments.values()) {
            if (environment instanceof GoldMine) {
                newEnvironmentView = new GoldMineView(environment.getId(), environment.getPosition());
            } else if (environment instanceof Base) {
                newEnvironmentView = new BaseView(environment.getId(), environment.getPosition());
            }
            if (newEnvironmentView != null) {
                this.environments.put(newEnvironmentView.getId(), newEnvironmentView);
            }
        }
    }

    public void updateCollectables() {
        for (CollectableView collectable : collectables.values()) {
            collectable.update();
        }
    }

    public void updateEnvironments() {
        for (EnvironmentView environment : environments.values()) {
            environment.update();
        }
    }

    public void updateEntityViews(ArrayList<Entity> entities) {
        ArrayList<PlayerUnitView> myUnits = new ArrayList<>();
        for (Entity entityModel : entities) {
            EntityView entity = this.gameEntitiesView.get(entityModel.getId());
            if (entityModel instanceof PlayerUnit modelUnit) {
                entity.setEntityState(modelUnit.getState());
                entity.setPosition(modelUnit.getPosition());
                entity.setDestination(modelUnit.getDestination());
                entity.setSelected(modelUnit.isSelected());
                entity.setAttackRange(modelUnit.getAttackRange());
                entity.setCollisionBox(modelUnit.getCollisionBox());
                entity.setInRange(modelUnit.isInAttackRange());
                entity.setHasAttacked(modelUnit.hasAttacked());
                entity.setHasBeenHit(modelUnit.hasBeenHit());
                entity.setHealthBarValues(entityModel.getMaxHp(), entityModel.getCurrentHp());
                entity.update();
                if (entity.isMyUnit) {
                    myUnits.add((PlayerUnitView) entity);
                }
            }
        }
        tileManager.updateFowView(myUnits);
    }

    public void setGameController(GameController gc) {
        System.out.println("Setting gController");
        this.gController = gc;
    }

    public void moveCamera(int xAmount, int yAmount) {
        if (canMoveCameraHorizontaly(xAmount)) {
            Camera.setXPosition(Camera.getPosition().getX() + xAmount);
        }
        if (canMoveCameraVertically(yAmount)) {
            Camera.setYPosition(Camera.getPosition().getY() + yAmount);
        }
    }

    private boolean canMoveCameraVertically(int yAmount) {
        if (Camera.getPosition().getY() + yAmount < 0) {
            return false;
        }
        if ((Camera.getPosition().getY() + yAmount > Constants.TILE_HEIGHT * tileManager.getNumRows())) {
            return false;
        }

        return true;
    }

    private boolean canMoveCameraHorizontaly(int xAmount) {
        if (Camera.getPosition().getX() + xAmount < 0) {
            return false;
        }
        return Camera.getPosition().getX() + xAmount <= Constants.TILE_WIDTH * tileManager.getNumCols();
    }

    public void setCameraWorldPosition(Position newCameraPosition) {
        Camera.setPosition(newCameraPosition);
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
                    GoldMineView goldMine = (GoldMineView) environments.get(event.getId());
                    goldMine.setDepleted(true);
                    break;
                case MINING:
                    break;
                case DEATH:
                    SoundManager.getInstance().play(SoundFX.DEATH);
                    addTextEvent(event, 15, EventColor.ALERT);
                    break;
                case ATTACK:
                    SoundManager.getInstance().play(SoundFX.ATTACK);
                    break;
                case TAKE_DMG:
                    SoundManager.getInstance().play(SoundFX.TAKE_DMG);
                    break;
                case FLAG_TO_BASE:
                    SoundManager.getInstance().play(SoundFX.FLAG_TO_BASE);
                    addTextEvent(event, 15, EventColor.SUCCESS);
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

    public void spawnFlag(long id, Position flagPos){
        this.collectables.put(id, new FlagView(id, flagPos));
    }

    public void addCollectableEvent(GameEvent event) {

        if (!this.collectables.containsKey(event.getId())) {
            //Collected something that doesn't need an animation, for example mining gold
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

        if(collectableView instanceof ChestView) {
            SoundManager.getInstance().play(SoundFX.OPEN_CHEST);
        }else if (collectableView instanceof GoldView) {
            SoundManager.getInstance().play(SoundFX.GOLD);
            System.out.println("Picked up collectable with id -> " + collectableView.getId());
            this.collectables.remove(event.getId());
        }else if (collectableView instanceof FlagView){
            SoundManager.getInstance().play(SoundFX.FLAG_PICK_UP);
            this.collectables.remove(event.getId());
        }
        addTextEvent(event, 25, EventColor.SUCCESS);
    }

    public void removeEntities(ArrayList<Long> unitIds) {
        for (Long id : unitIds) {
            this.gameEntitiesView.remove(id);
        }
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
