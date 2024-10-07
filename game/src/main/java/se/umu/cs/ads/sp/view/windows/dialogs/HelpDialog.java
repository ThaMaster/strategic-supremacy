package se.umu.cs.ads.sp.view.frameComponents.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HelpDialog extends JDialog {
    public HelpDialog() {
        this.setSize(400, 300);

        // Create a JTextArea with a lot of information
        JPanel textPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setText("Put help information here if you have time.");

        // Make the text area read-only and set line wrap
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(false);

        // Add the text area to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        textPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the scroll pane to the dialog
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Create an OK button to close the dialog
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> this.dispose());
        buttonPanel.add(backButton);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Display the dialog
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
