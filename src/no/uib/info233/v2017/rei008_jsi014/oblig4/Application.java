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




//        SwingUtilities.invokeLater(() -> {
//            String player1Name = "Svæla";
//            long time = System.currentTimeMillis();
//
//            Player player1 = new HumanPlayer(player1Name);
//
//            MainFrame mainFrame = new MainFrame("Game");
//
//            ButtonPanel buttonPanel = new ButtonPanel("Singleplayer", "Multiplayer", "Quit Game");
//
//            MenuPanel menuPanel = new MenuPanel(player1Name, Queries.getScore(player1Name), buttonPanel);
//
//            mainFrame.add(menuPanel);
//
//            System.out.println(System.currentTimeMillis() - time);
//
//
//            for(int i = 0; i <500; i++){
//                Debugger.print("This is a test, to check if it prints out a number:  " + i );
//            }
//
//
//
//
//
//
//        });

        GameMaster gameMaster = new GameMaster();
        Player player1 = new AggressivePlayer("Fangen");
        Player player2 = new HumanPlayer("Makaroni");
        gameMaster.setPlayers(player1, player2);

        gameMaster.saveGame();


//        Player p2 = new HumanPlayer("TestJohn");
//        String gameToJoin = Queries.getPlayerRandom("TestRune");
//        GameMaster gm = new GameMaster();
//        gm.joinGame(gameToJoin, p2);

//        Player player1 = new AggressivePlayer("Finne");
//        player1.slash(player1.getCurrentEnergy());
//        player1.setCurrentEnergy(0);
//        player1.slash(0);
//        player1.slash(8);
//        player1.slash(50);
//        player1.stab(5);
//        player1.slash(15);
//        player1.slash(2);





    }

}
