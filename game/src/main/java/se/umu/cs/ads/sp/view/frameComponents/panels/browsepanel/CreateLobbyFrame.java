package se.umu.cs.ads.sp.view.frameComponents.panels.browsepanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class CreateLobbyFrame extends JFrame {

    private JTextField nameField;
    private JSlider slider;
    private final JButton createButton;
    private JFrame parentFrame;

    public CreateLobbyFrame(JFrame mainFrame) {
        this.setTitle("Create new lobby");
        this.setLayout(new BorderLayout());

        JPanel namePanel = new JPanel(new FlowLayout());
        JLabel nameLabel = new JLabel("Lobby Name");
        nameField = new JTextField();
        nameField.setColumns(10);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Create a JSlider
        JPanel sliderSectionPanel = new JPanel();
        sliderSectionPanel.setLayout(new BoxLayout(sliderSectionPanel, BoxLayout.PAGE_AXIS));

        JPanel sliderPanel = new JPanel(new FlowLayout());
        slider = new JSlider(JSlider.HORIZONTAL, 5, 100, 50); // min, max, initial value
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        sliderPanel.add(slider);

        // Create a label to display the slider value
        JLabel sliderLabel = new JLabel("Max Players: " + slider.getValue());

        // Add a ChangeListener to update the label as the slider is dragged
        slider.addChangeListener(e -> sliderLabel.setText("Max Players: " + slider.getValue()));
        sliderSectionPanel.add(sliderLabel);
        sliderSectionPanel.add(sliderPanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.add(namePanel);
        centerPanel.add(sliderSectionPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            showFrame(false);
            nameField.setText("");
        });
        createButton = new JButton("Create");
        buttonPanel.add(backButton);
        buttonPanel.add(createButton);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.setResizable(false);
        this.pack();
        this.parentFrame = mainFrame;
        this.setLocationRelativeTo(mainFrame);
        this.setVisible(false);
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

    public void showFrame(boolean bool) {
        this.setVisible(bool);
        this.parentFrame.setEnabled(!bool);
    }
}
