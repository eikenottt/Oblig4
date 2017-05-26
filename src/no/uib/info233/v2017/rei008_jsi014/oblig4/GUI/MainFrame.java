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
        DarkUI.setUI();

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


    public MainFrame getFrame() {
        return this;
    }

}
