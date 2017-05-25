package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rune on 25.05.2017.
 */
public class MenuPanel extends JPanel {

    public MenuPanel(String playerName, Float score, ButtonPanel buttonPanel) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new LabelPanel(playerName, score));

        add(buttonPanel);

        add(new ImagePanel(getClass().getResource("/img/icon.png").getPath()));
    }

    public void updateButtons(ButtonPanel buttonPanel) {
        remove(1);
        add(buttonPanel, 1);
    }


}
