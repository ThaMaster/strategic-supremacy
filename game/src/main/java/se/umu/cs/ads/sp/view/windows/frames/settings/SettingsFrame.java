package se.umu.cs.ads.sp.view.windows.frames.settings;

import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.util.UtilView;
import se.umu.cs.ads.sp.view.windows.MainFrame;
import se.umu.cs.ads.sp.view.windows.frames.settings.panels.AudioPanel;
import se.umu.cs.ads.sp.view.windows.frames.settings.panels.ControlPanel;
import se.umu.cs.ads.sp.view.windows.frames.settings.panels.GeneralPanel;

import javax.swing.*;
import java.awt.*;

public class SettingsFrame extends JFrame {

    private JButton applyButton;

    private GeneralPanel generalPanel;
    private ControlPanel controlPanel;
    private AudioPanel audioPanel;

    private MainFrame mainFrame;

    public SettingsFrame(MainFrame mainFrame) {
        this.setTitle("Game Settings");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.mainFrame = mainFrame;
        // Create a tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        applyButton = new JButton("Apply");

        this.generalPanel = new GeneralPanel(applyButton, UtilView.isFullscreenSupported());
        this.controlPanel = new ControlPanel(applyButton);
        this.audioPanel = new AudioPanel(applyButton);

        // Add panels to the tabbed pane
        tabbedPane.addTab("General", generalPanel);
        tabbedPane.addTab("Controls", controlPanel);
        tabbedPane.addTab("Audio", audioPanel);

        // Add the tabbed pane to the frame
        this.add(tabbedPane, BorderLayout.CENTER);
        this.add(createButtonPanel(), BorderLayout.SOUTH);

        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setSize(400, 600);
        this.setVisible(false);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showFrame(false));
        applyButton.addActionListener(e -> {
            applyGeneralSettings();
            applyControlSettings();
            applyAudioSettings();
            applyButton.setEnabled(false);
            showFrame(false);
        });

        applyButton.setEnabled(false);
        buttonPanel.add(applyButton);
        buttonPanel.add(backButton);
        return buttonPanel;
    }

    public void showFrame(boolean bool) {
        this.setVisible(bool);
    }

    public void applyGeneralSettings() {
        if (generalPanel.isFullscreen()) {
            UtilView.FULLSCREEN = true;
            // Get the default graphics environment
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
            // Get the current display mode
            DisplayMode displayMode = graphicsDevice.getDisplayMode();
            UtilView.changeScreenSize(displayMode.getWidth(), displayMode.getHeight());
        } else {
            UtilView.FULLSCREEN = false;
            String[] numbers = generalPanel.getResolution().split("x");
            UtilView.changeScreenSize(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
        }
    }

    public void applyControlSettings() {

    }

    public void applyAudioSettings() {
        float globalVolume = (audioPanel.globalMute()) ? 0.0f : audioPanel.getGlobalVolume();
        float musicVolume = (audioPanel.musicMute()) ? 0.0f : audioPanel.getMusicVolume();
        float sfxVolume = (audioPanel.sfxMute()) ? 0.0f : audioPanel.getSFXVolume();
        SoundManager.getInstance().setVolume(globalVolume, musicVolume, sfxVolume);
    }
}
