package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.sp.model.objects.entities.Entity;
import se.umu.cs.ads.sp.utils.Position;
import se.umu.cs.ads.sp.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameController implements ActionListener {

    private final int FPS = 60;

    private Timer timer;
    private MainFrame mainFrame;
    private ArrayList<Entity> gameEntities = new ArrayList<>();

    public GameController() {

        mainFrame = new MainFrame();
        this.timer = new Timer(1000 / FPS, this);

        Entity newEntity = new Entity(new Position(100, 100));
        gameEntities.add(newEntity);

        mainFrame.getGamePanel().setGameController(this);

        mainFrame.getGamePanel().setEntities(gameEntities);
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
        mainFrame.getGamePanel().updateEntityPositions(gameEntities);
        for (Entity entity : gameEntities) {
            entity.update();
        }
    }

    private void render() {
        // System.out.println("Rendering!");
        SwingUtilities.invokeLater(() -> {
            mainFrame.getGamePanel().invalidate();
            mainFrame.getGamePanel().repaint();
        });
    }

    public void setEntityPosition(Position newPos) {
        this.gameEntities.get(0).setDestination(newPos);
    }
}
