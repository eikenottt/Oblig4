package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rune on 25.05.2017.
 */
public class ImagePanel extends JPanel {

    GridBagConstraints gbc;

    public ImagePanel(String imgPath) {
        gbc = new GridBagConstraints();
        ImageIcon imageIcon = new ImageIcon(imgPath);
        JLabel imageIconLabel = new JLabel(imageIcon);
        setLayout(new GridBagLayout());

        gbc.weighty = 10;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(imageIconLabel, gbc);

    }
}
