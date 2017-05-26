package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Created by John Tore on 26.05.2017.
 */
public class DebugFrame extends JFrame {

    JButton closeDebugger;
    TextArea debugStream;
    DebugFrame debugFrame = this;

    public DebugFrame(String title) {
        super(title);
        setUI();
        setUndecorated(true);
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
                    if(keyLower.contains("textarea.")) {
                        UIManager.put(key, buttonBG);
                    }
                }
            }
        }
    }

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


