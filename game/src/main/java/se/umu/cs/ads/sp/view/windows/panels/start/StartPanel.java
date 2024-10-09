package se.umu.cs.ads.sp.view.windows.panels.start;

import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {
    private JTextField nameField;
    private JButton enterButton;

    public StartPanel() {
        this.setName("StartPanel");
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout());
        JLabel gameTitle = new JLabel("Strategic Supremacy");
        gameTitle.setFont(StyleConstants.TITLE_FONT);
        titlePanel.add(gameTitle);

        JPanel fieldPanel = new JPanel(new FlowLayout());
        JLabel nameFieldLabel = new JLabel("Name:");
        nameFieldLabel.setFont(StyleConstants.TEXT_FIELD_FONT);
        nameField = new JTextField(15);
        fieldPanel.add(nameFieldLabel);
        fieldPanel.add(nameField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        enterButton = new JButton("Enter");
        buttonPanel.add(enterButton);

        this.add(titlePanel);
        this.add(fieldPanel);
        this.add(buttonPanel);
        this.setPreferredSize(new Dimension(275, 125));
    }

    public String getNameInput() {
        return this.nameField.getText();
    }

    public void setNameInput(String name) {
        this.nameField.setText(name);
    }

    public JButton getEnterButton() {
        return this.enterButton;
    }
}
