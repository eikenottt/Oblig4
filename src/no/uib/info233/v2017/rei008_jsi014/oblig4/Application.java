package no.uib.info233.v2017.rei008_jsi014.oblig4;

import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.ButtonPanel;
import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.DebugFrame;
import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.MainFrame;
import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.MenuPanel;
import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

import javax.swing.*;

/**
 * Created by Rune on 25.05.2017.
 */
public class Application {

    public static void main(String[] args) {




        SwingUtilities.invokeLater(() -> {
            String player1Name = "Svæla";
            long time = System.currentTimeMillis();

            Player player1 = new HumanPlayer(player1Name);

            MainFrame mainFrame = new MainFrame("Game");

            ButtonPanel buttonPanel = new ButtonPanel("Singleplayer", "Multiplayer", "Quit Game");

            MenuPanel menuPanel = new MenuPanel(player1Name, Queries.getScore(player1Name), buttonPanel);

            mainFrame.add(menuPanel);

            System.out.println(System.currentTimeMillis() - time);


            for(int i = 0; i <500; i++){
                Debugger.print("This is a test, to check if it prints out a number:  " + i + "\n" );
            }



        });

        /*GameMaster gameMaster = new GameMaster();
        Player player1 = new AggressivePlayer("Finne");
        Player player2 = new HumanPlayer("Svæla");
        gameMaster.setPlayers(player1, player2);
        gameMaster.startGame();*/
    }

}
