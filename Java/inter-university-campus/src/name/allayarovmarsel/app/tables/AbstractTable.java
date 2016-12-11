package name.allayarovmarsel.app.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTable {
    public JTable AbstractJTable;
    public DefaultTableModel tModel;
    public TableRowSorter<TableModel> sorter;
    public JScrollPane scroll;

    protected void initTableModel(String[] columns){
        tModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        AbstractJTable = new JTable(tModel);
        scroll = new JScrollPane(AbstractJTable);
        AbstractJTable.setCellSelectionEnabled(false);
        AbstractJTable.setColumnSelectionAllowed(false);
        AbstractJTable.setRowSelectionAllowed(true);

        // see https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
        AbstractJTable.getTableHeader().setResizingAllowed(false);
        AbstractJTable.getTableHeader().setReorderingAllowed(false);

        // Настройка сортировки
        sorter = new TableRowSorter<>(tModel);
        AbstractJTable.setRowSorter(sorter);
        int size = columns.length;
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(size);
        for(int i = 0; i < size; ++i)
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
    }
}
