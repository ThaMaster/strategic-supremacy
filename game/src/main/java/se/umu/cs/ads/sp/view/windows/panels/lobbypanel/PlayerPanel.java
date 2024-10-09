package se.umu.cs.ads.sp.view.windows.panels.lobbypanel;

import se.umu.cs.ads.sp.view.util.StyleConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PlayerPanel extends JPanel {
    private JTable table;
    private final String[][] empty = {{"", ""}};
    private final String[] column = {"ID", "Name"};
    private JLabel playerLabel;

    public PlayerPanel() {
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());

        JPanel groupTextPanel = new JPanel(new FlowLayout());
        playerLabel = new JLabel("Players");
        playerLabel.setFont(StyleConstants.LABEL_FONT);
        groupTextPanel.add(playerLabel);

        table = new JTable(new DefaultTableModel(empty, column)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);

        this.add(groupTextPanel, BorderLayout.NORTH);
        this.add(scroll, BorderLayout.CENTER);
    }

    public void setData(String[][] data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.getDataVector().removeAllElements();
        for (int i = 0; i < data.length; i++) {
            model.addRow(data[i]);
        }
        model.fireTableDataChanged();
    }

    public void setPlayerAmount(int current, int max) {
        playerLabel.setText("Players " + current + "/" + max);
    }
}
