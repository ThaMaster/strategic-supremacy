package se.umu.cs.ads.sp.view.windows.frames.settings;

import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.windows.frames.settings.panels.AudioPanel;
import se.umu.cs.ads.sp.view.windows.frames.settings.panels.ControlPanel;
import se.umu.cs.ads.sp.view.windows.frames.settings.panels.GeneralPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsFrame extends JFrame {

    private JButton applyButton;

    private GeneralPanel generalPanel;
    private ControlPanel controlPanel;
    private AudioPanel audioPanel;

    public SettingsFrame() {
        this.setTitle("Game Settings");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        this.generalPanel = new GeneralPanel();
        this.controlPanel = new ControlPanel();
        this.audioPanel = new AudioPanel(new SettingsMouseListener());

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
        this.addMouseListener(new SettingsMouseListener());
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> showFrame(false));
        applyButton = new JButton("Apply");
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

    }

    public void applyControlSettings() {

    }

    public void applyAudioSettings() {
        float globalVolume = (audioPanel.globalMute()) ? 0.0f : audioPanel.getGlobalVolume();
        float musicVolume = (audioPanel.musicMute()) ? 0.0f : audioPanel.getMusicVolume();
        float sfxVolume = (audioPanel.sfxMute()) ? 0.0f : audioPanel.getSFXVolume();
        SoundManager.getInstance().setVolume(globalVolume, musicVolume, sfxVolume);
    }


    private class SettingsMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (generalPanel.hasSettingsChanged() || controlPanel.hasSettingsChanged() || audioPanel.hasSettingsChanged()){
                applyButton.setEnabled(true);
            }
        }
    }
}
