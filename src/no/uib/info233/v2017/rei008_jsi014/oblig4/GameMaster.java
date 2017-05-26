package no.uib.info233.v2017.rei008_jsi014.oblig4;

import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

import java.util.ArrayList;

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
        gameRounds = 0;
        GOAL.add(-3);
        GOAL.add(3);
    }



    /**
     * Tells the players to make their first move
     */
    void startGame(){
        setGameOver(false);
        Debugger.print(player1Name + " vs " + player2Name + "\n");
        player1.makeNextMove(gamePosition, player1.getCurrentEnergy(), player2.getCurrentEnergy());
        player2.makeNextMove(gamePosition, player2.getCurrentEnergy(), player1.getCurrentEnergy());
    }

    /**
     * If game is not over, sets the amount of energy to use
     * for each player. When both are set, then call evaluate method.
     * @param player player
     * @param energyUse energyUse
     */
    void listenToPlayerMove(Player player, int energyUse) {

        boolean bothPlayersMoved = false;

        if(!gameOver) { // Game Not Over

            if(player.equals(player1)) {
                this.p1_energyUse = energyUse;
            } else {
                this.p2_energyUse = energyUse;
            }
        }

        if(this.p1_energyUse > -1 && this.p2_energyUse > -1) { // if both players has made a move
            bothPlayersMoved = true;
            evaluateTurn();
        }

    }



    /**
     * If the game is not over, use the information from
     * listenToPlayerMove() to figure out who won the round.
     * When game is over, it runs the updateRanking method
     */
    private void evaluateTurn() {

        //TODO insert isGameOver somewhere

        gameRounds++; // Increase the number of rounds played

        setGameOver(isGameOver());

        if(!gameOver) {


            if(getP1_energyUse() > getP2_energyUse()) { // if player 1 use more energy
                gamePosition++; // Move game one step closer toward player 1's goal

            }else if(getP1_energyUse() < getP2_energyUse()) { // if energy usage is equal
                gamePosition--; // Move game one step closer toward player 2's goal
            }



            System.out.println(player1Name + " used " + getP1_energyUse() + " energy and has "+player1.getCurrentEnergy()+" left.");
            System.out.println(player2Name + " used " + getP2_energyUse() + " energy and has "+player2.getCurrentEnergy()+" left.");
            System.out.println("Games Played: " + gameRounds + ", Game Position: " + gamePosition);

            // Prints out messages to the debugging console
            Debugger.print(player1Name + " used " + getP1_energyUse() + " energy and has "+player1.getCurrentEnergy()+" left.");
            Debugger.print(player2Name + " used " + getP2_energyUse() + " energy and has "+player2.getCurrentEnergy()+" left.");
            Debugger.print("Round: " + gameRounds + ", Game Position: " + gamePosition);

            // Reset the energy usage and prepare for a new round
            this.p1_energyUse = -1;
            this.p2_energyUse = -1;

            if(!isGameOver()) {
                // Players makes their next move
                player1.makeNextMove(gamePosition, player1.getCurrentEnergy(), player2.getCurrentEnergy());
                player2.makeNextMove(gamePosition, player2.getCurrentEnergy(), player1.getCurrentEnergy());
            }
            else {
                updateRanking();
            }
        }
        else {
            updateRanking(); // Update the database
        }

    }


    private boolean isGameOver() {
        // if the current gamePosition lays in the GOAL array or both players energy is at zero
        return (GOAL.contains(gamePosition) || (player1.getCurrentEnergy() == 0 && player2.getCurrentEnergy() == 0));
    }

    private int fetchIntInString(String string) {
        String d = string.substring(string.indexOf("¿")+1, string.indexOf("|"));
        return Integer.parseInt(d);
    }

    // ----------- Manipulate game data ---------- //

    /**
     * Loads the state of the saved game into the gameMaster
     * @param gameID the id of the game
     * @return True if the game loads successfully
     */
    public boolean loadGame(String gameID){

        GameMaster loadedGameMaster = Queries.loadSaved(gameID);

        boolean gameLoaded = false;

        if(loadedGameMaster != null) {

            this.gameID = loadedGameMaster.gameID;
            this.gameRounds = fetchIntInString(gameID);
            this.player1 = loadedGameMaster.player1;
            this.player2 = loadedGameMaster.player2;
            this.gamePosition = loadedGameMaster.gamePosition;

            gameLoaded = true;

            // TODO Debugger
            System.out.println("Loaded:  \n ID: " + gameID + " \n Player 1: " + player1.getName() + " With " +player1.getCurrentEnergy()+ " Energy." +"\n Player 2: " +player2.getName()+ " With " + player2.getCurrentEnergy() + " Energy. Game Position is " + gamePosition + "\n Round: " + gameRounds);

            Debugger.print("Loaded:  \n ID: " + gameID + " \n Player 1: " + player1.getName() + " With " +player1.getCurrentEnergy()+ " Energy." +"\n Player 2: " +player2.getName()+ " With " + player2.getCurrentEnergy() + " Energy. Game Position is " + gamePosition + "\n Round: " + gameRounds);
        }
        if(!gameLoaded){
            Debugger.print("There was an error loading the game.");
        }

        return gameLoaded;
    }


    /**
     * Runs when the game is over and updates the database
     * @return True if the database was updated
     */
    private void updateRanking() {

        float pointsPlayer1 = getPointsFromPosition(gamePosition);
        float pointsPlayer2 = getPointsFromPosition(gamePosition*-1);

        System.out.println("There have been played " + gameRounds + " rounds!");

        Debugger.print("There have been played " + gameRounds + " rounds!");

        if(pointsPlayer1 > pointsPlayer2) {
            System.out.println(player1Name + " won the game by " + pointsPlayer1 + " to " + pointsPlayer2);
            Debugger.print(player1Name + " won the game by " + pointsPlayer1 + " to " + pointsPlayer2);
        }
        else if(pointsPlayer2 > pointsPlayer1) {
            System.out.println(player2Name + " won the game by " + pointsPlayer2 + " to " + pointsPlayer1);
            Debugger.print(player2Name + " won the game by " + pointsPlayer2 + " to " + pointsPlayer1);
        }
        else {
            System.out.println("The game ended in a draw");
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

    public void hostGame(){
        //TODO When a HumanPlayer creates a new multiplayer game, he is "hosting" the game.
        Queries.openGame(player1);
    }

    public void listGames(){
        //TODO displays the available games that players can join in the multiplayer section.
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
        }else{
            return this.player1;
        }
    }

    public String getPlayerName(Player player) {

        if(player.equals(player1)) {
            return player1.getName();
        }
        else {
            return player2.getName();
        }
    }

    private int getP1_energyUse() {
        return p1_energyUse;
    }

    private int getP2_energyUse() {
        return p2_energyUse;
    }



    private void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getGameID() {
        return gameID;
    }

    private void setGameID() {
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

    /**
     * Translates the position of the players into points
     * @param currentPosition position of the player
     * @return a float value with the player score
     */
    private float getPointsFromPosition(int currentPosition) {
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



}
