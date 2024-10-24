package se.umu.cs.ads.sp.view.windows.frames;

import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;

public class CreateLobbyFrame extends JFrame {

    private JTextField nameField;
    private JSlider slider;
    private JButton createButton;
    private JComboBox comboBox;

    public CreateLobbyFrame() {
        this.setTitle("Create new lobby");
        this.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(createNamePanel());
        centerPanel.add(createMapSelectionPanel());
        centerPanel.add(createSliderPanel());

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(createButtonPanel(), BorderLayout.SOUTH);

        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }

    private JPanel createNamePanel() {
        JPanel namePanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Lobby Name");
        nameField = new JTextField();
        nameField.setColumns(10);
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        return namePanel;
    }

    private JPanel createMapSelectionPanel() {
        JPanel mapSelectionPanel = new JPanel(new FlowLayout());
        String[] names = {"Beginner", "Easy", "Medium", "Hard", "Extreme"}; // Sample descriptive names
        JLabel mapSelectionLabel = new JLabel("Selected Map:");
        comboBox = new JComboBox<>(names);

        mapSelectionPanel.add(mapSelectionLabel);
        mapSelectionPanel.add(Box.createHorizontalGlue());
        mapSelectionPanel.add(comboBox);

        return mapSelectionPanel;
    }

    private JPanel createSliderPanel() {
        JPanel sliderSectionPanel = new JPanel();
        sliderSectionPanel.setLayout(new BoxLayout(sliderSectionPanel, BoxLayout.PAGE_AXIS));

        JPanel sliderPanel = new JPanel(new FlowLayout());
        slider = new JSlider(JSlider.HORIZONTAL, 2, 100, 50); // min, max, initial value
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        sliderPanel.add(slider);

        // Create a label to display the slider value
        JLabel sliderLabel = new JLabel("Max Players: " + slider.getValue());

        // Add a ChangeListener to update the label as the slider is dragged
        slider.addChangeListener(e -> sliderLabel.setText("Max Players: " + slider.getValue()));
        sliderSectionPanel.add(sliderLabel);
        sliderSectionPanel.add(sliderPanel);
        return sliderSectionPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            showFrame(false);
            nameField.setText("");
        });

        createButton = new JButton("Create");
        buttonPanel.add(backButton);
        buttonPanel.add(createButton);
        return buttonPanel;
    }

    public JTextField getLobbyNameField() {
        return nameField;
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public int getMaxPlayerValue() {
        return slider.getValue();
    }

    public String getSelectedMap() {
        return (String)comboBox.getSelectedItem();
    }

    public void showFrame(boolean bool) {
        if(UtilView.DARK_MODE) {
            UtilView.setDarkMode();
        } else {
            UtilView.setLightMode();
        }
        this.revalidate();
        this.repaint();
        this.setVisible(bool);
    }
}
