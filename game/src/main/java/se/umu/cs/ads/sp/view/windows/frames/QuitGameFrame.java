package se.umu.cs.ads.sp.view.windows.frames;

import se.umu.cs.ads.sp.view.util.StyleConstants;
import se.umu.cs.ads.sp.view.util.UtilView;

import javax.swing.*;
import java.awt.*;

public class QuitGameFrame extends JFrame {

    private JButton quitButton;

    public QuitGameFrame() {
        this.setTitle("Quit Game");
        this.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        JLabel textLabel = new JLabel("Do you want to quit the game?");
        textLabel.setFont(StyleConstants.PLAIN_TEXT_FONT);
        textLabel.setHorizontalAlignment(JLabel.CENTER);

        centerPanel.add(textLabel);
        centerPanel.setBorder(StyleConstants.DEFAULT_EMPTY_BORDER);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(createButtonPanel(), BorderLayout.SOUTH);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("No");
        backButton.addActionListener(e -> {
            showFrame(false);
        });

        quitButton = new JButton("Yes");
        buttonPanel.add(backButton);
        buttonPanel.add(quitButton);
        return buttonPanel;
    }

    public JButton getQuitButton() {
        return quitButton;
    }

    public void showFrame(boolean bool) {
        this.setVisible(bool);
    }
}
