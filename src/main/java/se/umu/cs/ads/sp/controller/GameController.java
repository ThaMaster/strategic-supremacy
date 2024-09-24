package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.sp.model.ModelManager;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController implements ActionListener {

    private final int FPS = 60;

    private Timer timer;
    private MainFrame mainFrame;
    private ModelManager modelManager;

    public GameController() {

        modelManager = new ModelManager(this);
        mainFrame = new MainFrame();
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
        // System.out.println("Updating!");
        modelManager.update();
        mainFrame.getGamePanel().updateEntityPositions(modelManager.getGameEntities());
    }

    private void render() {
        // System.out.println("Rendering!");
        SwingUtilities.invokeLater(() -> {
            mainFrame.getGamePanel().invalidate();
            mainFrame.getGamePanel().repaint();
        });
    }

    public void setEntityPosition(Position newPos) {
        modelManager.setEntityPosition(newPos);
    }

    public void setSelection(Position clickLocation){
        modelManager.setSelection(clickLocation);
    }

    public void setSelection(int entityId){
        modelManager.setSelection(entityId);
    }
}
