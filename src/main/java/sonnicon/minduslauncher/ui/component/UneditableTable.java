package sonnicon.minduslauncher.ui.component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UneditableTable extends JTable{
    public UneditableTable(Object[] s){
        super(new DefaultTableModel(s, 0));
        getTableHeader().setReorderingAllowed(false);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return false;
    }
}
