package se.umu.cs.ads.sp.view.panels.browsepanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BrowsePanel extends JPanel {
    private JTable table;
    private String empty[][] = {{"", "", "", ""}};
    private String column[] = {"ID", "Lobby Name", "Members", "Private"};

    private JButton joinButton;
    private JButton refreshButton;
    private JButton backButton;
    private JButton createButton;

    public BrowsePanel() {
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());

        JPanel groupTextPanel = new JPanel(new FlowLayout());
        JLabel groupText = new JLabel("Available Lobbies");
        groupText.setFont(new Font("serif", Font.BOLD, 20));
        groupTextPanel.add(groupText);

        table = new JTable(new DefaultTableModel(empty, column)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        joinButton = new JButton("Join");
        refreshButton = new JButton("Refresh");
        joinButton.setEnabled(false);
        buttonPanel.add(joinButton);
        buttonPanel.add(refreshButton);

        this.add(groupTextPanel, BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setData(String[][] data) {
        clearTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < data.length; i++) {
            model.addRow(data[i]);
        }
        model.fireTableDataChanged();
    }

    private void clearTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
    }

    public JButton getJoinButton() {
        return joinButton;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public JTable getBrowseTable() {
        return table;
    }

    public void setJoinEnabled(boolean bool) {
        joinButton.setEnabled(bool);
    }
}
