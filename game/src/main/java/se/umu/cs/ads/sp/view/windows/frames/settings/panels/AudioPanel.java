package se.umu.cs.ads.sp.view.windows.frames.settings.panels;

import se.umu.cs.ads.sp.view.soundmanager.SoundManager;
import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseListener;

public class AudioPanel extends JPanel {

    private JSlider globalVolumeSlider;
    private JSlider musicVolumeSlider;
    private JSlider sfxVolumeSlider;

    private final MouseListener settingsListener;

    private boolean muteGlobal = false;
    private boolean muteMusic = false;
    private boolean muteSFX = false;

    private boolean settingsChanged = false;

    public AudioPanel(MouseListener listener) {
        settingsListener = listener;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        globalVolumeSlider = new JSlider(0, 100, SoundManager.getInstance().getGlobalVolume());
        globalVolumeSlider.addMouseListener(settingsListener);
        musicVolumeSlider = new JSlider(0, 100, SoundManager.getInstance().getMusicVolume());
        musicVolumeSlider.addMouseListener(settingsListener);
        sfxVolumeSlider = new JSlider(0, 100, SoundManager.getInstance().getSFXVolume());
        sfxVolumeSlider.addMouseListener(settingsListener);

        this.add(createVolumePanel("Global Volume", globalVolumeSlider, e -> {
            muteGlobal = ((JCheckBox) e.getSource()).isSelected();
            globalVolumeSlider.setEnabled(!muteGlobal);
            settingsChanged = true;
        }));
        this.add(createVolumePanel("Music Volume", musicVolumeSlider, e -> {
            muteMusic = ((JCheckBox) e.getSource()).isSelected();
            musicVolumeSlider.setEnabled(!muteMusic);
            settingsChanged = true;
        }));
        this.add(createVolumePanel("SFX Volume", sfxVolumeSlider, e -> {
            muteSFX = ((JCheckBox) e.getSource()).isSelected();
            sfxVolumeSlider.setEnabled(!muteSFX);
            settingsChanged = true;
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
        muteCheckBox.addMouseListener(settingsListener);
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

    public boolean hasSettingsChanged() {
        return settingsChanged;
    }
}
