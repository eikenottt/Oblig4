package no.uib.info233.v2017.rei008_jsi014.oblig4;

/**
 * Created by Rune on 25.05.2017.
 */
public class Application {

    public static void main(String[] args) {
        GameMaster gameMaster = new GameMaster();
        Player player1 = new AggressivePlayer("Finne");
        Player player2 = new HumanPlayer("Svæla");
        gameMaster.setPlayers(player1, player2);
        gameMaster.startGame();
    }
}
