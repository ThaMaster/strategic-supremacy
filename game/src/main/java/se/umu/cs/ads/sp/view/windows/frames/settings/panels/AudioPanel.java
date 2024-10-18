package se.umu.cs.ads.sp.view.windows.frames.settings.panels;

import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AudioPanel extends JPanel {

    private JButton applyButton;
    private JSlider globalVolumeSlider;
    private JSlider musicVolumeSlider;
    private JSlider sfxVolumeSlider;

    private boolean muteGlobal = false;
    private boolean muteMusic = false;
    private boolean muteSFX = false;


    public AudioPanel(JButton applyButton) {
        this.applyButton = applyButton;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        globalVolumeSlider = new JSlider(0, 100, SoundManager.getInstance().getGlobalVolume());
        globalVolumeSlider.addMouseListener(new SetChangedAdapter());
        musicVolumeSlider = new JSlider(0, 100, SoundManager.getInstance().getMusicVolume());
        musicVolumeSlider.addMouseListener(new SetChangedAdapter());
        sfxVolumeSlider = new JSlider(0, 100, SoundManager.getInstance().getSFXVolume());
        sfxVolumeSlider.addMouseListener(new SetChangedAdapter());
        this.add(createVolumePanel("Global Volume", globalVolumeSlider, e -> {
            muteGlobal = ((JCheckBox) e.getSource()).isSelected();
            globalVolumeSlider.setEnabled(!muteGlobal);
            applyButton.setEnabled(true);
        }));
        this.add(createVolumePanel("Music Volume", musicVolumeSlider, e -> {
            muteMusic = ((JCheckBox) e.getSource()).isSelected();
            musicVolumeSlider.setEnabled(!muteMusic);
            applyButton.setEnabled(true);
        }));
        this.add(createVolumePanel("SFX Volume", sfxVolumeSlider, e -> {
            muteSFX = ((JCheckBox) e.getSource()).isSelected();
            sfxVolumeSlider.setEnabled(!muteSFX);
            applyButton.setEnabled(true);
        }));
    }

    private JPanel createVolumePanel(String labelText, JSlider slider, ChangeListener checkBoxListener) {
        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.PAGE_AXIS));

        // Label Panel
        JPanel labelPanel = new JPanel(new FlowLayout());
        JLabel label = new JLabel(labelText);
        label.setFont(StyleConstants.LABEL_FONT);
        labelPanel.add(label);

        // Mute Checkbox
        JCheckBox muteCheckBox = new JCheckBox("Mute");
        muteCheckBox.addChangeListener(checkBoxListener);

        // Slider Panel
        JPanel sliderPanel = new JPanel(new FlowLayout());
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        // Create a label to display the slider value
        sliderPanel.add(slider);
        sliderPanel.add(muteCheckBox);

        // Add components to volume panel
        volumePanel.add(Box.createVerticalGlue());
        volumePanel.add(labelPanel);
        volumePanel.add(sliderPanel);
        volumePanel.add(Box.createVerticalGlue());

        return volumePanel;
    }

    public int getGlobalVolume() {
        return this.globalVolumeSlider.getValue();
    }

    public int getMusicVolume() {
        return this.musicVolumeSlider.getValue();
    }

    public int getSFXVolume() {
        return this.sfxVolumeSlider.getValue();
    }

    public boolean globalMute() {
        return this.muteGlobal;
    }

    public boolean musicMute() {
        return this.muteMusic;
    }

    public boolean sfxMute() {
        return this.muteSFX;
    }

    private class SetChangedAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            applyButton.setEnabled(true);
        }
    }
}
