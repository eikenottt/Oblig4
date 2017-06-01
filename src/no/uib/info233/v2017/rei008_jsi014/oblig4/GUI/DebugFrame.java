package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import static no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.GUI.*;

/**
 * Is the Debugger frame
 */
public class DebugFrame extends JFrame {

    JButton closeDebugger;
    TextArea debugStream;
    DebugFrame debugFrame = this;

    public DebugFrame(String title) {
        super(title);
        setUI();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setSize(550, 600);
        setPreferredSize(new Dimension(550, 600));

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width-550, dim.height-630);

        // FileMenu

        //New panel with BorderLayout
        JPanel container = new JPanel(new BorderLayout());

        //Configurations for the debugging stream TextArea
        debugStream = new TextArea(15, 30);
        debugStream.setEditable(false);
        debugStream.setText("");
        debugStream.setBackground(new Color(50,50,50));
        // Close button - closes the Debugging console
        closeDebugger = new JButton("Close Debugger");

        //Create a new listener for the closeDebugger button
        ListenForButton listenForButton = new ListenForButton();
        closeDebugger.addActionListener(listenForButton);

        //Adding Jcomponents to the content panel
        container.add(debugStream,BorderLayout.CENTER);
        container.add(closeDebugger, BorderLayout.PAGE_END);

        //add content panel
        this.add(container);

        //makes it invisible by default
        setVisible(false);

    }


    // Imported from Main-Frame, might have to make a couple of changes


    /**
     * Constructor DebugFrame
     * @return DebugFrame instance
     */
    public DebugFrame getFrame() {
        return this;
    }

    // Adds a new string to the debug-console
    public void debugOut(String out) {
        debugStream.append(out);
    }

    private class ListenForButton implements ActionListener{

        //Closes the debugger window
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == closeDebugger){
               debugFrame.setVisible(false);
            }
        }
    }
}


