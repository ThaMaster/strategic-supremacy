package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.utils.Position;
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

    public GameController() {

        modelManager = new ModelManager(this);
        tileManager = new TileManager();
        tileManager.setMap(modelManager.getMap().getModelMap());

        // TODO: Fix the initialization of the tile manager (should not be passed in the constructor of the main frame)
        mainFrame = new MainFrame(tileManager);
        this.timer = new Timer(1000 / FPS, this);
        mainFrame.getGamePanel().setGameController(this);

        mainFrame.getGamePanel().setEntities(modelManager.getGameEntities());
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

        // Updates
        mainFrame.getGamePanel().updateEntityViews(modelManager.getGameEntities());
    }

    private void render() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.getGamePanel().invalidate();
            mainFrame.getGamePanel().repaint();
        });
    }

    public void setEntityDestination(Position newPos) {
        modelManager.setEntityDestination(newPos);
    }

    public void setSelection(Position clickLocation){
        modelManager.setSelection(clickLocation);
    }

    public void setSelection(int entityId){
        modelManager.setSelection(entityId);
    }

    public int getSelectedUnit() {
        return modelManager.getSelectedUnit();
    }

    public void stopSelectedEntity() {
        modelManager.stopSelectedEntity();
    }
}
