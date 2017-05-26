package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import no.uib.info233.v2017.rei008_jsi014.oblig4.Debugger;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Rune on 25.05.2017.
 */
public class DebuggerMenu extends JMenuBar implements ActionListener, KeyListener {

    JMenu debuggerMenu;
    JMenuItem dmShowDebugger;
    DebugFrame debugFrame;

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
        dmShowDebugger.setMnemonic(KeyEvent.VK_D);
        dmShowDebugger.addActionListener(this);

        debuggerMenu.add(dmShowDebugger);

        add(debuggerMenu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == dmShowDebugger){
            debugFrame = new DebugFrame("Debugger");
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
