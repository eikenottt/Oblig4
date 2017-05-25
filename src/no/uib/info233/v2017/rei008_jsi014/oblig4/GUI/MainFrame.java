package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;

/**
 * Created by Rune on 25.05.2017.
 */
public class MainFrame extends JFrame {

    String playerName, fstBtn, sndBtn, trdBtn;
    Float score;

    public MainFrame(String title) {
        super(title);
        setUI();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setSize(1000, 500);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);

        // FileMenu
        JMenuBar menuBar = new DebuggerMenu();

        setJMenuBar(menuBar);

        setVisible(true);

        addWindowListener(new WindowListener() {
            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                ButtonActions.exitProgram();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

    }

    private void setUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        FontUIResource f = new FontUIResource("Calibri", Font.PLAIN, 22);
        ColorUIResource bg = new ColorUIResource(70,70,70);
        ColorUIResource fg = new ColorUIResource(255,255,255);
        Color buttonBG = new Color(120,120,120);
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String keyLower = key.toString().toLowerCase();
            Object value = UIManager.get(key);
            if(value != null) {
                if (value instanceof FontUIResource) {
                    UIManager.put(key, f);
                }
                if (value instanceof ColorUIResource) {
                    if(keyLower.contains("background"))
                        UIManager.put(key, bg);
                    if(keyLower.toString().contains("foreground"))
                        UIManager.put(key, fg);
                    if(keyLower.toString().contains("button.background"))
                        UIManager.put(key, buttonBG);
                    if(keyLower.toString().contains("button.foreground"))
                        UIManager.put(key, fg);
                    if(keyLower.toString().contains("button.select"))
                        UIManager.put(key, bg);
                    if(keyLower.toString().contains("button.focus"))
                        UIManager.put(key, buttonBG);
                    if(keyLower.toString().contains("textfield.background"))
                        UIManager.put(key, fg);
                    if(keyLower.toString().contains("textfield.foreground"))
                        UIManager.put(key, bg);
                    if(keyLower.toString().contains("list.background"))
                        UIManager.put(key, buttonBG);
                    if(keyLower.toString().contains("optionpane.messageforeground")) {
                        UIManager.put(key, fg);
                    }
                    if(keyLower.toString().contains("progressbar.foreground")) {
                        UIManager.put(key, ColorUIResource.RED);
                    }
                    if(keyLower.toString().contains("menu.foreground")) {
                        UIManager.put(key, fg);
                    }
                }
            }
        }
    }

    public MainFrame getFrame() {
        return this;
    }

}
