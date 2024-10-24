package se.umu.cs.ads.sp.view.windows.frames;

import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import java.awt.*;

public class GameEndFrame extends JFrame {

    private final JLabel topLabel;
    private final JLabel textLabel;

    private JButton quitButton;


    public GameEndFrame() {
        this.setTitle("Game Finished");
        this.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        topLabel = new JLabel("");
        topLabel.setFont(StyleConstants.LABEL_FONT);
        topLabel.setHorizontalAlignment(JLabel.CENTER);  // Center alignment within the label
        topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center alignment in BoxLayout

        textLabel = new JLabel("");
        textLabel.setFont(StyleConstants.PLAIN_TEXT_FONT);
        textLabel.setHorizontalAlignment(JLabel.CENTER);  // Center alignment within the label
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center alignment in BoxLayout

        centerPanel.add(topLabel);
        centerPanel.add(textLabel);
        centerPanel.setBorder(StyleConstants.DEFAULT_EMPTY_BORDER);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(createButtonPanel(), BorderLayout.SOUTH);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        quitButton = new JButton("Leave");
        buttonPanel.add(quitButton);
        return buttonPanel;
    }

    public JButton getQuitButton() {
        return quitButton;
    }

    public void showFrame(boolean bool) {
        this.pack();
        this.setVisible(bool);
    }

    public void setContent(boolean isWinner, String endText) {
        if (isWinner) {
            topLabel.setText("VICTORY");
            topLabel.setForeground(Color.GREEN.darker());
            textLabel.setText("You " + endText.split(" ", 2)[1]);
        } else {
            topLabel.setText("DEFEAT");
            topLabel.setForeground(Color.RED.darker());
            textLabel.setText(endText);
        }
        this.pack();
    }
}
