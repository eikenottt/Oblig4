package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rune on 25.05.2017.
 */
public class LabelPanel extends JPanel {

    JLabel playerNamePanel, scorePanel;
    GridBagConstraints gbc;

    public LabelPanel(String playerName, Float score) {
        gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());
        playerNamePanel = new JLabel(playerName, JLabel.CENTER);
        scorePanel = new JLabel("Score: "+score);

        gbc.weightx = 0.5;
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(playerNamePanel, gbc);

        gbc.anchor = GridBagConstraints.FIRST_LINE_END;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(scorePanel, gbc);

    }
}
