package no.uib.info233.v2017.rei008_jsi014.oblig4;

import com.sun.org.apache.xpath.internal.SourceTree;
import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Connector;
import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 * GameMaster sets up and keeps track of a game between two players
 *
 * @author rei008
 * @author jsi014
 * @version 0.2
 */
public class GameMaster {

    // ID of the GameMaster
    private String gameID;

    // The Players
    private Player player1;
    private Player player2;

    // ArrayList containing the two positions where the game ends
    private final ArrayList<Integer> GOAL = new ArrayList<>(2);
    private int gamePosition;

    // Players energy use
    private int p1_energyUse;
    private int p2_energyUse;

    // Player Names
    private String player1Name;
    private String player2Name;

    // Determines the state of the game
    private boolean gameOver;
    private int gameRounds;
    private boolean isUpdated = false;

    Timer timer;


    /**
     * Constructor for GameMaster
     * initializes the energyUse for both players
     * and the positions where the games ends
     */
    public GameMaster() {
        gamePosition = 0;
        gameOver = false;
        p1_energyUse = -1;
        p2_energyUse = -1;
        gameRounds = 1;
        GOAL.add(-3);
        GOAL.add(3);
    }

    /**
     * Tells the players to make their first move
     */
    public void startGame(){
        setGameOver(false);
        Debugger.print("######-----| " + player1Name + " vs " + player2Name + " |-----######\n");
        player1.setCurrentEnergy(100);
        player2.setCurrentEnergy(100);

        player1.setPlayerMove(0);
        player2.setPlayerMove(0);
        //TODO change makeNextMove with listenToPlayerMove ?
        /*while (!gameOver){
            //TODO refresh every 2 seconds
            //TODO run listenToPlayerMove
            //TODO Update Table after every move
        }*/
    }

    /**
     * Determines the winner of the game, according to the current gamePosition.
     * @return Player - Returns the winner of the game
     */
    public Player determineWinner(){

        Player player;

        if(gamePosition > 0 ){
            player = this.player1; // Signals player 1 is the winner
        }else if (gamePosition < 0){
             player = this.player2; //Sigals the game ended in a draw
        }else{
            player = null; // Signals player 2 is the winner
        }

        return player;
    }

    /**
     * If game is not over, sets the amount of energy to use
     * for each player. When both are set, then call evaluate method.
     * @param player player
     * @param energyUse energyUse
     */
    public void listenToPlayerMove(Player player, int energyUse) { //TODO Listen for player move every 2 seconds

        if (!gameOver) { // Game Not Over
            if (player1.getPulse() && player2.getPulse()) { // Both players are human -> MultiplayerGame
                Queries.updateMove(this, player);


            }
            else {
                if (player.equals(player1)) {
                    this.p1_energyUse = energyUse;  //TODO viska ut for at singleplayer skal fungere(player 1 ikke skal bli trukket 2x energy)
                    System.out.println(player.getName() + " Used " + p1_energyUse + " -----------########");
                } else {
                    this.p2_energyUse = energyUse;
                    System.out.println(player.getName() + " Used " + p2_energyUse + " -----------########");
                }

                if ((this.p1_energyUse > -1 && this.p2_energyUse > -1) ){//(hasMoved(gameID))) { // if both players has made a move
                    evaluateTurn();
                }
            }

        }
    }

    /**
     * Refreshes the game, by retrieving from the game_in_progress table, and determines whether the player is the host or not.
     * and returns the GameMaster needed accordingly.
     * @param player - The player loading the game
     * @return - Updated GameMaster from the game_in_progress table.
     */
    /*public GameMaster gameProcessor(Player player){
        String[] playerMoves = Queries.getPlayerMove(gameID);
        GameMaster nextRound = this.getGameInProgress(gameID);
        if (hasMoved(gameID) ) {
            int player1Move = Integer.valueOf(playerMoves[0]);
            int player2Move = Integer.valueOf(playerMoves[1]);

            isUpdated = false;


            nextRound.listenToPlayerMove(player1, player1Move);
            nextRound.listenToPlayerMove(player2, player2Move);
        }

//        if(player.getHost()){
//            listenToPlayerMove(player, player1Move);
//
//            Player player2 = nextRound.getSpecificPlayer(2);
//            nextRound.setPlayers(player, player2);
//        }else{
//            listenToPlayerMove(player, player2Move);
//            Player player1 = nextRound.getSpecificPlayer(1);
//            nextRound.setPlayers(player1, player);
//        }
//
//        nextRound = getGameInProgress(gameID);
//        updateGameInProgress(gameID);

        return nextRound;

    }*/

    public void gameProcessor(Player player, int energyUsed) {
        player.makeNextMove(gamePosition, energyUsed, 0);

        isUpdated = false;

        GameMaster oldGame = getGameInProgress(getGameID());

        timer = new Timer(2000, e -> {
            if(hasMoved(gameID)){
                ((Timer) e.getSource()).stop();
                GameMaster newGame = getGameInProgress(gameID);
                if(player.getHost()) {
                    updateGameInProgress(gameID);
                    isUpdated = true;
                }
                else {
                    if(oldGame.equals(newGame)) {
                        isUpdated = true;
                    }
                }
            }
        });

        timer.start();
    }



    /**
     * If the game is not over, use the information from
     * listenToPlayerMove() to figure out who won the round.
     * When game is over, it runs the updateRanking method
     */
    public void evaluateTurn() {

        gameRounds++; // Increase the number of rounds played

        setGameOver(isGameOver());

        if(!gameOver) {


            if(getP1_energyUse() > getP2_energyUse()) { // if player 1 use more energy
                gamePosition++; // Move game one step closer toward player 1's goal

            }else if(getP1_energyUse() < getP2_energyUse()) { // if energy usage is equal
                gamePosition--; // Move game one step closer toward player 2's goal
            }

            // Prints out message to the debugging console
            Debugger.print("Round: " + gameRounds + ", Game Position: " + gamePosition);

            // Reset the energy usage and prepare for a new round
            this.p1_energyUse = -1;
            this.p2_energyUse = -1;

            if(player1.getPulse() && player2.getPulse()) {
                updateGameInProgress(gameID); // Updates game_position, move_number
                isUpdated = true;

            }
            if(isGameOver()) {
                gameOver = true;
                if(player1.getPulse()) {
                    updateRanking();
                }
            }
        }
        else {
            if(player1.getPulse())
                updateRanking(); // Update the database
        }

    }




    public boolean isGameOver() {
        // if the current gamePosition lays in the GOAL array or both players energy is at zero
        return (GOAL.contains(gamePosition) || (player1.getCurrentEnergy() == 0 && player2.getCurrentEnergy() == 0));
    }

    public int fetchIntInString(String string) {
        //TODO feilmelding på fetchIntInString  - StringIndexOutOfBoundsException
        //TODO return -1 if gameID don't contain ¿ and |
        String d = string.substring(string.indexOf("¿")+1, string.indexOf("|"));
        return Integer.parseInt(d);
    }

    // ----------- Manipulate game data ---------- //

    /**
     * Loads the state of the saved game into the gameMaster
     * @param gameID the id of the game
     */
    public GameMaster loadGame(String gameID, Player player){

        return Queries.loadSaved(gameID, player);
    }

    public void startMultiplayerGame(Player player1, String player2Name, String player2ID){


        //add joinedPlayer
        Player player2 = new HumanPlayer(player2Name);
        player2.setPlayerID(player2ID);
        setPlayers(player1, player2);

        player1.setCurrentEnergy(100);
        player2.setCurrentEnergy(100);

        //reset the GameMaster
        gamePosition = 0;
        gameOver = false;
        p1_energyUse = -1;
        p2_energyUse = -1;
        gameRounds = 1;

        //Creates a new game
        Queries.createGame(this);
        
    }


    /**
     * Runs when the game is over and updates the database
     */
    private void updateRanking() {

        float pointsPlayer1 = getPointsFromPosition(gamePosition);
        float pointsPlayer2 = getPointsFromPosition(gamePosition*-1);

        System.out.println("There have been played " + gameRounds + " rounds!"); //SOUT

        Debugger.print("There have been played " + gameRounds + " rounds!");

        if(pointsPlayer1 > pointsPlayer2) {
            System.out.println(player1Name + " won the game by " + pointsPlayer1 + " to " + pointsPlayer2); //SOUT
            Debugger.print(player1Name + " won the game by " + pointsPlayer1 + " to " + pointsPlayer2);
        }
        else if(pointsPlayer2 > pointsPlayer1) {
            System.out.println(player2Name + " won the game by " + pointsPlayer2 + " to " + pointsPlayer1); //SOUT
            Debugger.print(player2Name + " won the game by " + pointsPlayer2 + " to " + pointsPlayer1);
        }
        else {
            System.out.println("The game ended in a draw"); //SOUT
            Debugger.print("Nice Tie - The game ended in a draw");
        }


        Queries.updateRanking(player1, pointsPlayer1);
        Queries.updateRanking(player2, pointsPlayer2);

    }

    /**
     *
     */
    public void saveGame(){
        Queries.updateSavedGame(this);
    }

    public void hostGame(Player player1){


        //TODO When a HumanPlayer creates a new multiplayer game, he is "hosting" the game.
        Queries.openGame(player1);


        final boolean[] hasFoundOpponent = {false};
        final boolean[] rowDeleted = {false};
        Timer t = new Timer(2000, e -> {
            hasFoundOpponent[0] = Queries.hasJoined(player1.getRandom());
            rowDeleted[0] = Queries.rowDeleted(player1.getRandom());
            System.out.println("hasFoundOpponent - " + hasFoundOpponent[0]);//SOUT
            if (hasFoundOpponent[0]){
                ((Timer) e.getSource()).stop();
                String[] p2 = Queries.getPlayerValues();
                this.startMultiplayerGame(player1, p2[0], p2[1]);
                System.out.println(this);

                this.player1.setHost(true);
                System.out.println("You are the host: " + player1.getHost());
            }
            if(rowDeleted[0]) {
                ((Timer) e.getSource()).stop();
            }
        });
        t.start();
    }


    public boolean hasJoined(String hostId){

        return Queries.hasJoined(hostId);
    }

    public void removeGameInProgress(String gameID){
        Queries.removeGameInProgress(gameID);
    }

    public static Boolean hasConnection(){
        return Queries.hasConnection();
    }

    public void listGames(){
        //TODO displays the available games that players can join in the multiplayer section.
    }

    public boolean joinGame(String player1_random, Player player2){
        //TODO Whe the player joins the game, a new game should start with the host as player one
        if(!Queries.joinGame(player1_random, player2)){

            Debugger.print("Could not join game");
            return true;
        }
        return false;
    }

    public void removeOpenGame(String player1Random) {
        Queries.removeOpenGame(player1Random);
    }


    public void updateMove(Player player) {
        Queries.updateMove(this, player);
    }

    public void resign(Player player1) {
        int gamePos;
        timer.stop();
        if(player1.equals(this.player1)){
            gamePos = -3;
        }
        else {
            gamePos = 3;
        }
        setGamePosition(gamePos);
        setGameOver(true);
        updateGameInProgress(gameID);
        updateRanking();
        Debugger.print("Player Resigned");
    }



    // --------- Getters And Setters ---------- //

    /**
     * Assigns the players that are going to play against each other
     * @param player1 player1
     * @param player2 player2
     */
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        player1Name = player1.getName();
        player2Name = player2.getName();
        player1.registerGameMaster(this);
        player2.registerGameMaster(this);
        setGameID();
    }

    public Player getSpecificPlayer(int playerNumber){
        if(playerNumber == 2){
            return this.player2;
        }else if(playerNumber == 1){
            return this.player1;
        }
        else {
            Debugger.print("There is no such player");
            return null;
        }
    }

    public void setP1_energyUse(int p1_energyUse) {
        this.p1_energyUse = p1_energyUse;
    }

    public void setP2_energyUse(int p2_energyUse) {
        this.p2_energyUse = p2_energyUse;
    }

    public String getPlayerName(Player player) {

        if(player.equals(player1)) {
            return player1.getName();
        }
        else {
            return player2.getName();
        }
    }

    public boolean isUpdated(){
        return isUpdated;
    }

    private int getP1_energyUse() {
        return p1_energyUse;
    }

    private int getP2_energyUse() {
        return p2_energyUse;
    }

    public GameMaster getGameInProgress(String gameId){

        return Queries.getGameInProgress(gameId);
    }

    public boolean hasMoved(String gameID) {
        return Queries.hasMoved(gameID);
    }

    public void updateGameInProgress(String gameID) {
        Queries.updateGameInProgress(gameID, this);
    }

    public boolean gameExists(String gameID){
        return Queries.gameExists(gameID);
    }

    public void resetMoves(){
        Queries.resetMoves(gameID);
    }



    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getGameID() {
        return gameID;
    }

    // TODO GameID må ikkje være lenger enn 20 på game_in_progress
    private void setGameID() {
        if(player1.hasPulse && player2.hasPulse) {
            gameID = player1.getRandom() + player2.getRandom();
        } else {
            gameID = player1.getRandom() + "¿" + gameRounds + "|" + player2.getRandom();
        }
    }

    public void updateGameID(int gameRounds) {
        gameID = player1.getRandom() + "¿" + gameRounds + "|" + player2.getRandom();
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public int getGamePosition() {
        return gamePosition;
    }

    public void setGamePosition(int gamePosition){
        this.gamePosition = gamePosition;
    }

    public int getGameRounds() {
        return gameRounds;
    }

    /**
     * Translates the position of the players into points
     * @param currentPosition position of the player
     * @return a float value with the player score
     */
    public float getPointsFromPosition(int currentPosition) {
        float points;
        switch (currentPosition) {
            case -3:
                points = -1f;
                break;
            case -2:
                points = 0f;
                break;
            case -1:
                points = 0.25f;
                break;
            case 0:
                points = 0.5f;
                break;
            case 1:
                points = 0.75f;
                break;
            case 2:
                points = 1f;
                break;
            default:
                points = 2f;
                break;
        }

        return points;
    }

    /**
     * A Bolean test to check if both players has made a move in the current
     * round of the game.
     * @return True if both players have made move this round.
     */
//    public boolean moveChecker(){
//
//       int playerMoves[] = Queries.getPlayerMove(gameID); //Gather fresh info of the current moves
//
//       int player1Move = playerMoves[0];
//       int player2Move = playerMoves[1];
//
//       boolean isNewRound = false;
//       if (player1Move == player2Move){ // Checks if both players has made a move, this round.
//           isNewRound = true;
//       }
//
//       return isNewRound;
//    }

    public void setGameRound(int gameRound) {
        this.gameRounds = gameRound;
    }


    @Override
    public String toString() {
        return "GameMaster{" +
                "gameID='" + gameID + '\'' +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", GOAL=" + GOAL +
                ", gamePosition=" + gamePosition +
                ", p1_energyUse=" + p1_energyUse +
                ", p2_energyUse=" + p2_energyUse +
                ", player1Name='" + player1Name + '\'' +
                ", player2Name='" + player2Name + '\'' +
                ", gameOver=" + gameOver +
                ", gameRounds=" + gameRounds +
                '}';
    }

    public void setIsUpdated(boolean isUpdated) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameMaster)) return false;

        GameMaster that = (GameMaster) o;

        if (gamePosition != that.gamePosition) return false;
        if (p1_energyUse != that.p1_energyUse) return false;
        if (p2_energyUse != that.p2_energyUse) return false;
        if (gameOver != that.gameOver) return false;
        if (gameRounds != that.gameRounds) return false;
        if (gameID != null ? !gameID.equals(that.gameID) : that.gameID != null) return false;
        if (player1 != null ? !player1.equals(that.player1) : that.player1 != null) return false;
        if (player2 != null ? !player2.equals(that.player2) : that.player2 != null) return false;
        if (player1Name != null ? !player1Name.equals(that.player1Name) : that.player1Name != null) return false;
        return player2Name != null ? player2Name.equals(that.player2Name) : that.player2Name == null;
    }

    @Override
    public int hashCode() {
        int result = gameID != null ? gameID.hashCode() : 0;
        result = 31 * result + (player1 != null ? player1.hashCode() : 0);
        result = 31 * result + (player2 != null ? player2.hashCode() : 0);
        result = 31 * result + gamePosition;
        result = 31 * result + p1_energyUse;
        result = 31 * result + p2_energyUse;
        result = 31 * result + (player1Name != null ? player1Name.hashCode() : 0);
        result = 31 * result + (player2Name != null ? player2Name.hashCode() : 0);
        result = 31 * result + (gameOver ? 1 : 0);
        result = 31 * result + gameRounds;
        return result;
    }

    public void stopTimer() {
        timer.stop();
    }
    public Timer getTimer() {
        return timer;
    }
}
