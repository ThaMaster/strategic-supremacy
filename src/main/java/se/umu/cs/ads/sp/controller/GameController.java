package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.model.map.TileModel;
import se.umu.cs.ads.sp.model.objects.collectables.Collectable;
import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.utils.enums.Direction;
import se.umu.cs.ads.sp.view.MainFrame;
import se.umu.cs.ads.sp.view.panels.gamepanel.tiles.TileManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController implements ActionListener {

    private final int FPS = 60;

    private Timer timer;
    private MainFrame mainFrame;

    private ModelManager modelManager;
    private TileManager tileManager;

    private Direction cameraPanningDirection = Direction.NONE;

    public GameController() {

        modelManager = new ModelManager();
        tileManager = new TileManager();
        tileManager.setMap(modelManager.getMap().getModelMap());

        // TODO: Fix the initialization of the tile manager (should not be passed in the constructor of the main frame)
        mainFrame = new MainFrame(tileManager);
        this.timer = new Timer(1000 / FPS, this);
        mainFrame.getGamePanel().setGameController(this);

        mainFrame.getGamePanel().setEntities(modelManager.getGameEntities());
        mainFrame.getGamePanel().setCollectables(modelManager.getCollectables());
        startGame();
    }

    public void startGame() {
        this.timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        render();
    }

    private void update() {
        modelManager.update();

        for(Entity entity : modelManager.getGameEntities()) {
            for (int i = 0; i < modelManager.getCollectables().size(); i++) {
                Collectable collectable = modelManager.getCollectables().get(i);
                if (entity.getCollisionBox().checkCollision(collectable.getCollisionBox())) {
                    collectable.pickUp();
                    mainFrame.getGamePanel().performPickUp(i);
                }
            }
        }

        mainFrame.getGamePanel().updateEntityViews(modelManager.getGameEntities());

        mainFrame.getGamePanel().updateCollectables();

        // Check where to move the camera.
        if(cameraPanningDirection != Direction.NONE) {
            switch (cameraPanningDirection) {
                case NORTH:
                    mainFrame.getGamePanel().moveCamera(0, -5);
                    break;
                case SOUTH:
                    mainFrame.getGamePanel().moveCamera(0, 5);
                    break;
                case WEST:
                    mainFrame.getGamePanel().moveCamera(-5, 0);
                    break;
                case EAST:
                    mainFrame.getGamePanel().moveCamera(5, 0);
                    break;
                default:
                    break;
            }
        }
    }

    private void render() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getGamePanel().invalidate();
            mainFrame.getGamePanel().repaint();
        });
    }

    public void setEntityDestination(Position newPos) {
        if(modelManager.isWalkable(newPos)) {
            modelManager.setEntityDestination(newPos);
        }
    }

    public void setSelection(Position clickLocation){
        modelManager.setSelection(clickLocation);
    }

    public void setSelection(int entityId){
        modelManager.setSelection(entityId);
        Position newCameraPos = modelManager.getGameEntities().get(entityId).getPosition();
        mainFrame.getGamePanel().setCameraWorldPosition(newCameraPos.getX(), newCameraPos.getY());
    }

    public int getSelectedUnit() {
        return modelManager.getSelectedUnit();
    }

    public void stopSelectedEntity() {
        modelManager.stopSelectedEntity();
    }

    public void setCameraPanningDirection(Direction dir) {
        this.cameraPanningDirection = dir;
    }
}
