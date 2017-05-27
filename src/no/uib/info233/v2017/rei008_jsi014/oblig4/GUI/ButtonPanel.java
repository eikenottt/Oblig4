package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Rune on 25.05.2017.
 */
public class ButtonPanel extends JPanel {


    private JButton button1, button2, button3;

    public ButtonPanel(String firstBtn, String sndBtn, String trdBtn) {
        GridBagConstraints gbc = new GridBagConstraints();

        setLayout(new GridBagLayout());

        button1 = new JButton(firstBtn);
        button1.addActionListener(new ButtonActions());
        button2 = new JButton(sndBtn);
        button2.addActionListener(new ButtonActions());
        button3 = new JButton(trdBtn);
        button3.addActionListener(new ButtonActions());

        gbc.weightx = 0.5;
        gbc.weighty = 0;

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(button1, gbc);

        gbc.gridx = 1;
        add(button2, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 2;
        add(button3, gbc);
    }

    public void setButtonClickable(String btnName, boolean clickable) {
        if(btnName.equals(button1.getName())) {
            button1.setEnabled(clickable);
        }
        else if(btnName.equals(button2.getName())) {
            button2.setEnabled(clickable);
        }
        else if(btnName.equals(button3.getName())) {
            button3.setEnabled(clickable);
        }
    }

}
