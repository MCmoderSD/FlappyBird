package de.MCmoderSD.UI;

import de.MCmoderSD.main.Config;
import de.MCmoderSD.utilities.Calculate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScoreBoard extends JPanel {

    // Attributes
    private HashMap<String, Integer> scores;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public ScoreBoard(Menu menu, Config config) {
        super();
        setLayout(new BorderLayout());

        Font font = new Font("Roboto", Font.PLAIN, 22);
        Font headerFont = new Font("Roboto", Font.BOLD, 22);

        // Create table model with three columns
        tableModel = new DefaultTableModel(new Object[]{config.getRank(), config.getUsername(), config.getScore()}, 0);

        // Create table with the model
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Set font
        table.setFont(font);
        table.getTableHeader().setFont(headerFont);

        // Disable column reordering
        table.getTableHeader().setReorderingAllowed(false);

        // Disable column resizing
        table.getTableHeader().setResizingAllowed(false);

        // Add table to the panel
        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(false);
        menu.add(this);
    }

    private void updateTable() {
        // Clear table
        tableModel.setRowCount(0);

        // Sort scores by value in descending order
        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
        sortedScores.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        // Add sorted scores to table
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedScores) {
            tableModel.addRow(new Object[]{rank + ".", entry.getKey(), entry.getValue()});
            rank++;
        }

        int margin = 5;

        for (int row = 0; row < table.getRowCount(); row++) {
            // Get the current default height for all rows
            int height = table.getRowHeight();

            // Determine the highest cell in the row
            for (int column = 0; column < table.getColumnCount(); column++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                int h = comp.getPreferredSize().height + 2 * margin;

                // Increase height for the current row
                height = Math.max(height, h);
            }

            // Update the row height
            table.setRowHeight(row, height);
        }

        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                //  We've exceeded the maximum width, no need to check other rows
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth + margin);
        }
    }

    // Setter
    public void setHashMap(HashMap<String, Integer> scores) {
        if (Calculate.compareHashMaps(this.scores, scores)) return;
        this.scores = scores;
        updateTable();
    }
}
