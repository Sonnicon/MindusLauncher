package sonnicon.minduslauncher.ui.windows;

import sonnicon.minduslauncher.files.FileIO;
import sonnicon.minduslauncher.ui.ModalWindow;
import sonnicon.minduslauncher.ui.component.UneditableTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class ModsWindow extends ModalWindow{
    public final JTable tableMods;
    private final File modsFile;

    public ModsWindow(){
        super("Mods");
        modsFile = new File(FileIO.getDatadir(), "mods");

        frame.setLayout(new BorderLayout());
        tableMods = new UneditableTable(new String[]{"Disabled", "Name"}){
            @Override
            public Class<?> getColumnClass(int column){
                return column == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex){
                return columnIndex == 0;
            }
        };

        tableMods.setColumnSelectionAllowed(false);
        JScrollPane paneInstance = new JScrollPane(tableMods);
        paneInstance.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(BorderLayout.CENTER, paneInstance);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 3));

        panelButtons.add(runnableButton("OK", () -> {
            for(int i = 0; i < tableMods.getRowCount(); i++){
                String name = (String) tableMods.getModel().getValueAt(i, 1);
                File fi = new File(modsFile, name);
                if((boolean) tableMods.getModel().getValueAt(i, 0)){
                    if(!fi.getName().endsWith(".disabled")){
                        fi.renameTo(new File(modsFile, name + ".disabled"));
                    }
                }else{
                    if(fi.getName().endsWith(".disabled")){
                        fi.renameTo(new File(modsFile, name.substring(0, name.length() - 9)));
                    }
                }
            }
            frame.setVisible(false);
        }));
        panelButtons.add(runnableButton("Refresh", this::populate));
        panelButtons.add(runnableButton("Cancel", () -> frame.setVisible(false)));

        frame.add(BorderLayout.SOUTH, panelButtons);
    }

    @Override
    protected int defaultWidth() {
        return 300;
    }

    @Override
    protected int defaultHeight() {
        return 400;
    }


    JButton runnableButton(String text, Runnable onPress){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        return button;
    }

    public void populate(){
        ((DefaultTableModel) tableMods.getModel()).setRowCount(0);
        for(File file : modsFile.listFiles()){
            ((DefaultTableModel) tableMods.getModel()).addRow(new Object[]{file.getName().endsWith(".disabled"), file.getName()});
        }
    }

    @Override
    public void show(){
        populate();
        super.show();
    }
}
