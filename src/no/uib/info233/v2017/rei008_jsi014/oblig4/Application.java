package no.uib.info233.v2017.rei008_jsi014.oblig4;

import no.uib.info233.v2017.rei008_jsi014.oblig4.GUI.*;
import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

import javax.swing.*;
import java.util.Objects;

/**
 * Created by Rune on 25.05.2017.
 */
public class Application {

    public static void main(String[] args) {


        SwingUtilities.invokeLater(GUI::new);

        /*GameMaster gameMaster = new GameMaster();
        Player player1 = new AggressivePlayer("Finne");
        Player player2 = new HumanPlayer("Svæla");
        gameMaster.setPlayers(player1, player2);
        gameMaster.startGame();*/

        /*Player p2 = new HumanPlayer("TestJohn");
        String gameToJoin = Queries.getPlayerRandom("TestRune");
        System.out.println("no string? - " + gameToJoin);
        Queries.joinGame(gameToJoin, p2);*/
    }

}
