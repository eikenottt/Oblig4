package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import no.uib.info233.v2017.rei008_jsi014.oblig4.AggressivePlayer;
import no.uib.info233.v2017.rei008_jsi014.oblig4.GameMaster;
import no.uib.info233.v2017.rei008_jsi014.oblig4.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Rune on 25.05.2017.
 */
public class ButtonActions implements ActionListener {

    JFrame frame;
    Container container;
    MenuPanel menuPanel;
    ButtonPanel buttonPanel;

    public ButtonActions() {
        this.frame = (JFrame) MainFrame.getFrames()[0];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        container = frame.getContentPane();
        menuPanel = (MenuPanel) container.getComponent(0);
        switch (e.getActionCommand()){
            case "Singleplayer":
                changeButtons("New Game", "Load Game", "Back to Menu");
                break;
            case "Quit Game":
                exitProgram();
                break;
            case "New Game":
                Player player2 = new AggressivePlayer("CPU");
                GameMaster gameMaster = new GameMaster();

                System.out.println(frame.getOwner());
                break;
            case "Back to Menu":
                changeButtons("Singleplayer", "Multiplayer", "Quit Game");
                break;

        }
    }

    public static void exitProgram() {
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit Game", JOptionPane.YES_NO_OPTION);
        if(choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void changeButtons(String fstBtn, String sndBtn, String trdBtn) {
        buttonPanel = new ButtonPanel(fstBtn, sndBtn, trdBtn);
        container.removeAll();

        menuPanel.updateButtons(buttonPanel);

        container.add(menuPanel);
        container.validate();
        container.repaint();
        container.setVisible(true);
    }

}
