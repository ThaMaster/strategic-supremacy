package se.umu.cs.ads.sp.controller;

import se.umu.cs.ads.sp.view.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController implements ActionListener {

    private final int FPS = 60;

    private Timer timer;
    private double currentTime = 0;
    private double lastTime = 0;

    private MainFrame mainFrame;

    public GameController() {

        mainFrame = new MainFrame();
        this.timer = new Timer(1000/FPS, this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        render();
    }

    private void update() {

    }

    private void render() {

    }
}
