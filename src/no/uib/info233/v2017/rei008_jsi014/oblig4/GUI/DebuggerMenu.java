package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import no.uib.info233.v2017.rei008_jsi014.oblig4.Debugger;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuBarUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Rune on 25.05.2017.
 */
public class DebuggerMenu extends JMenuBar implements ActionListener, KeyListener {

    private JMenu debuggerMenu;
    private JMenuItem dmShowDebugger, dmFrameSize;
    private DebugFrame debugFrame;

    public DebuggerMenu() {

        setUI( new BasicMenuBarUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                g.setColor(new Color(30,30,30));
                g.fillRect(0,0,c.getWidth(),c.getHeight());
            }
        });
        debuggerMenu = new JMenu("Debugger");

        dmShowDebugger = new JMenuItem("Show Debugger");
        dmFrameSize = new JMenuItem("Show Frame Height");
        dmShowDebugger.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.SHIFT_MASK));
        dmShowDebugger.addActionListener(this);

        debuggerMenu.add(dmShowDebugger);

        add(debuggerMenu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == dmShowDebugger){
            debugFrame = new DebugFrame("Debugger");
        }
        if(e.getSource() == dmFrameSize) {
            System.out.println();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
