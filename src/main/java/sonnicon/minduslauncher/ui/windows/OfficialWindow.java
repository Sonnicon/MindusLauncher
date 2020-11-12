package sonnicon.minduslauncher.ui.windows;

import com.google.gson.internal.LinkedTreeMap;
import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.type.Instance;
import sonnicon.minduslauncher.ui.ModalWindow;
import sonnicon.minduslauncher.ui.component.UneditableTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class OfficialWindow extends ModalWindow{
    public final JTable tableVersion;

    private boolean fetched = false;
    private final HashMap<String, String> downloads = new HashMap<>();

    private static final HashSet<String> jarnames = new HashSet<String>(Arrays.asList("desktop-release.jar", "Mindustry.jar"));

    public OfficialWindow(){
        super("Download Official");
        frame.setLayout(new BorderLayout());

        tableVersion = new UneditableTable(new String[]{"Version", "Date"});

        tableVersion.setColumnSelectionAllowed(false);
        JScrollPane paneInstance = new JScrollPane(tableVersion);
        paneInstance.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(BorderLayout.CENTER, paneInstance);

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(0, 3));

        panelButtons.add(runnableButton("Download", () -> {
            int selected = tableVersion.getSelectedRow();
            if(selected > -1) {
                Instance.instanceFromURL(downloads.get(tableVersion.getModel().getValueAt(selected, 0)));
                frame.setVisible(false);
            }
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
        downloads.clear();
        ((DefaultTableModel) tableVersion.getModel()).setRowCount(0);
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.github.com/repos/Anuken/Mindustry/releases?per_page=999").openConnection();
            con.setRequestMethod("GET");
            con.connect();
            BufferedReader json = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //todo excluder
            ArrayList data = Vars.gson.fromJson(json, ArrayList.class);
            fetched = true;

            for(int i = 0; i < data.size(); i++){
                String tag = (String) getDataByName(data, i, "tag_name");
                ArrayList assets = (ArrayList) getDataByName(data, i, "assets");
                if(assets == null || assets.size() == 0) continue;
                for(Object a : assets){
                    if(jarnames.contains(((LinkedTreeMap)a).get("name"))){
                        downloads.put(tag, (String) ((LinkedTreeMap)a).get("browser_download_url"));
                        break;
                    }
                }
                ((DefaultTableModel) tableVersion.getModel()).addRow(new Object[]{tag, ((String)getDataByName(data, i, "published_at")).substring(0, 10)});
            }
            con.disconnect();
        }catch(Exception ex){
            Logger.getLogger(getClass().getName()).warning(ex.toString());
            JOptionPane.showMessageDialog(frame, "Error fetching releases.\n" + ex.getMessage());
        }
    }

    private Object getDataByName(ArrayList arrayList, int i, String name){
        return ((LinkedTreeMap)arrayList.get(i)).get(name);
    }

    @Override
    public void show(){
        if(!fetched) populate();
        super.show();
    }
}
