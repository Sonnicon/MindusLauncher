package sonnicon.minduslauncher.ui.windows;

import sonnicon.minduslauncher.core.Vars;
import sonnicon.minduslauncher.ui.ModalWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

public class AboutWindow extends ModalWindow{
    protected boolean reload = false;

    public AboutWindow(){
        super("Settings");
        frame.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        frame.add(content);

        JLabel title = new JLabel("MindusLauncher");
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 20));
        content.add(title);

        // I dislike this, but wrapping is more important
        content.add(new JLabel("A launcher for the game Mindustry written with Java Swing."));

        final String url = "https://github.com/Sonnicon/MindusLauncher";
        JLabel github = new JLabel(url);
        content.add(github);
        github.setForeground(Color.blue);
        github.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                try{
                    Desktop.getDesktop().browse(URI.create(url));
                }catch(UnsupportedOperationException ex){
                    JOptionPane.showMessageDialog(frame, "This operation is not supported on the current platform.");
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        content.add(new JLabel("\nCopyright (c) 2020 Sonnicon"));

        //bottom
        JPanel panelButtons = new JPanel();

        panelButtons.add(runnableButton("Close", () ->
            frame.setVisible(false)
        ));

        frame.add(BorderLayout.SOUTH, panelButtons);
    }
    @Override
    protected int defaultWidth() {
        return 350;
    }

    @Override
    protected int defaultHeight() {
        return 150;
    }


    JButton runnableButton(String text, Runnable onPress){
        JButton button = new JButton(text);
        button.addActionListener(e -> onPress.run());
        return button;
    }
}
