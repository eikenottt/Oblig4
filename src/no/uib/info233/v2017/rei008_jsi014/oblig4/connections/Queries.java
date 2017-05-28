package no.uib.info233.v2017.rei008_jsi014.oblig4.connections;

import no.uib.info233.v2017.rei008_jsi014.oblig4.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by runeeikemo on 24.05.2017.
 */
public final class Queries {

    private static String[] playerValues = new String[2];

    private static PreparedStatement statement;

    /**
     * Sets player 1 as a host for a game
     *
     * @param player hosting player
     * @return true if a row was inserted
     */
    public static boolean openGame(Player player) {

        boolean tableUpdated = false;

        try {
            Connection conn = Connector.getConnection();

            statement = conn.prepareStatement("INSERT INTO oblig4.open_games(player_1, player_1_random) VALUES (?, ?)");
            statement.setString(1, player.getName());
            statement.setString(2, player.getRandom());
            int rowCount = statement.executeUpdate();
            if (rowCount > 0) {
                tableUpdated = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        }

        return tableUpdated;
    }

    public static boolean joinGame(String p1_random, Player player2) {

        boolean tableUpdated = false;

        try {
            Connection conn = Connector.getConnection();

            statement = conn.prepareStatement("UPDATE oblig4.open_games SET player_2 = ?, player_2_random = ? WHERE  player_1_random = ?");
            statement.setString(1, player2.getName());
            statement.setString(2, player2.getRandom());
            statement.setString(3, p1_random);
            int rowCount = statement.executeUpdate();
            if (rowCount > 0) {
                tableUpdated = true;
            }
            Debugger.print("Open_Games Table was updated");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        }

        return tableUpdated;
    }


    public static void createGame(GameMaster gameMaster) {

        Connection conn = null;

        //Create Player objects to hold the players, for aesthetic purposes
        Player player1 = gameMaster.getSpecificPlayer(1);
        Player player2 = gameMaster.getSpecificPlayer(2);

        try {
            conn = Connector.getConnection();

            statement = conn.prepareStatement(
                    "INSERT INTO oblig4.game_in_progress(game_id, player_1, player_2, game_position, player_1_energy, player_2_energy, player_1_move, player_2_move, move_number) " +
                            "VALUES (?,?,?,?,?,?,?,?,?)");

            statement.setString(1, gameMaster.getGameID());
            statement.setString(2, player1.getName());
            statement.setString(3, player2.getName());
            statement.setInt(4, gameMaster.getGamePosition());
            statement.setInt(5, player1.getCurrentEnergy());
            statement.setInt(6, player2.getCurrentEnergy());
            statement.setInt(7, player1.getPlayerMove());
            statement.setInt(8, player2.getPlayerMove());
            statement.setInt(9, gameMaster.getGameRounds());
            statement.executeUpdate();

            conn.close();//Close connection

        } catch (SQLException e1) {
            e1.printStackTrace();
            Debugger.print("EXCEPTION: " + e1.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        }
        Debugger.print("Game inserted in game_in_progress table");

    }


    /**
     * Handles the SQL-queries and manipulations in the database
     * @param player the player to be updated
     * @param score player score
     */
    public static boolean updateRanking(Player player, float score) {
        float prevScore = 0f;
        boolean updated = false;
        if(player.getPulse()) {
            try {
                Connection conn = Connector.getConnection();
                statement = conn.prepareStatement("SELECT score FROM ranking WHERE player = ?");
                statement.setString(1, player.getName());
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    prevScore = result.getFloat(1);
                    score += prevScore;
                }

                // Deletes the player row if it exists
                statement = conn.prepareStatement("DELETE FROM oblig4.ranking WHERE player = ? AND score = ? LIMIT 1");
                statement.setString(1, player.getName());
                statement.setFloat(2, prevScore);
                statement.executeUpdate();

                // Replace into is used for replacing old values in tables with primary keys
                statement = conn.prepareStatement("REPLACE INTO ranking(player, score) VALUES (?, ?)");
                statement.setString(1, player.getName());
                statement.setFloat(2, score);
                statement.executeUpdate();

                conn.close(); // Close connection

                updated = true; // Success
                if(updated){
                    Debugger.print("Success: " + player + "'s rank has been updated!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Debugger.print("EXCEPTION: " + e.getMessage());
            }
        }

        return updated;
    }


    /**
     * Updates the saved_games table with the current values
     *
     * @param gameMaster the gameMaster that hold the information about the game
     */
    public static boolean updateSavedGame(GameMaster gameMaster){

        boolean updated = false;
        try {
            Player player1 = gameMaster.getSpecificPlayer(1);
            Player player2 = gameMaster.getSpecificPlayer(2);

            Connection conn = Connector.getConnection();

            // Remove old values
            statement = conn.prepareStatement("DELETE FROM oblig4.saved_games WHERE game_id = ? AND game_position = ?");
            statement.setString(1, gameMaster.getGameID());
            statement.setInt(2, gameMaster.getGamePosition());
            statement.executeUpdate();

            // Insert new
            statement = conn.prepareStatement( "REPLACE INTO oblig4.saved_games (game_id, player_1, player_2, game_position, player_1_energy, player_2_energy) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1,gameMaster.getGameID());
            statement.setString(2,player1.getName());
            statement.setString(3,player2.getName());
            statement.setInt(4,gameMaster.getGamePosition());
            statement.setInt(5,player1.getCurrentEnergy());
            statement.setInt(6,player2.getCurrentEnergy());
            statement.executeUpdate();



            conn.close(); // Close connection

            updated = true; // Success
            if (updated){
                Debugger.print("Success: Game has been saved");
            }

        } catch (SQLException e) {
            Debugger.print("EXCEPTION: " + e.getMessage() + e.getErrorCode());
        } catch (Exception e) {
            e.getMessage();
            Debugger.print("EXCEPTION: " + e.getMessage());
        }

        return updated;
    }


    /**
     * Loads a singleplayer game from the database
     * @param gameID ID of the game
     * @return A GameMaster containing the information needed to play
     * @throws //TODO CustomException
     */
    public static GameMaster loadSaved(String gameID) {

        GameMaster gameMaster = null;
        try {
            Connection conn = Connector.getConnection(); // Make connection

            // Execute query
            statement = conn.prepareStatement("SELECT * FROM oblig4.saved_games WHERE game_id = ?");
            statement.setString(1, gameID);
            ResultSet rs = statement.executeQuery();

            // Gather information into local variables
            rs.next();
            String id = rs.getString("game_id");

            String p1 = rs.getString("player_1");
            String p2 = rs.getString("player_2");

            int gamePos = rs.getInt("game_position");

            int p1Energy = rs.getInt("player_1_energy");
            int p2Energy = rs.getInt("player_2_energy");

            conn.close(); // Close connection

            // Set players based on information from saved_games table
            Player player1 = new HumanPlayer(p1);
            Player player2;

            if(id.substring(id.length()).equals('1')) {
                player2 = new PassivePlayer(p2);
            }
            else {
                player2 = new AggressivePlayer(p2);
            }

            player1.setCurrentEnergy(p1Energy);
            player2.setCurrentEnergy(p2Energy);

            // Prepare gameMaster for battle
            gameMaster = new GameMaster();
            gameMaster.setGameID(id);
            gameMaster.setGamePosition(gamePos);

            gameMaster.setPlayers(player1, player2);

            Debugger.print("Success: The game has been successfully loaded!");


        } catch (Exception e) {
            Debugger.print("EXEPTION: " + e.getMessage());
        }

        if(gameMaster == null) {
            Debugger.print("Error:  gameMater == null - The game was not loaded.");
        }
        return gameMaster;

    }

    /**
     * Get the open multiplayergames from server
     *
     * @param playersMap
     * @return
     */
    public static TreeMap<String, ArrayList<String>> getMultiplayerMap(TreeMap<String, ArrayList<String>> playersMap){
        try {
            Connection conn = Connector.getConnection(); // Make connection

            // Execute query
            statement = conn.prepareStatement("SELECT player_1, player_1_random, player, score FROM oblig4.open_games Left JOIN oblig4.ranking ON player_1 = player ORDER BY score DESC");
            ResultSet result = statement.executeQuery();

            // Place information in a TreeMap - PlayerRandom is key
            while(result.next()){
                ArrayList<String> score = new ArrayList<>();
                // Player score
                Float playerScore = result.getFloat(4);
                score.add(playerScore.toString());
                // Player Name code
                score.add(result.getString(1));
                // Player Random and everything else
                playersMap.put(result.getString(2), score);
            }

            conn.close(); // Close Connection

        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        }

        // TODO if Map is Empty

        return playersMap;
    }

    public static TreeMap<String, ArrayList<String>> getGameMap(TreeMap<String, ArrayList<String>> playersMap){
        try {

            Connection conn = Connector.getConnection(); // Make connection
            statement = conn.prepareStatement("SELECT * FROM oblig4.saved_games");
            ResultSet result = statement.executeQuery();

            while(result.next()){
                ArrayList<String> score = new ArrayList<>();
                // Player 1 name
                score.add(result.getString(2));
                // Player 2 name
                score.add(result.getString(3));
                // Game Position
                score.add(result.getString(4));
                // Player 1 Energy
                score.add(result.getString(5));
                // Player 2 Energy
                score.add(result.getString(6));
                // Game ID and everything else
                playersMap.put(result.getString(1), score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return playersMap;
    }


    /**
     * Gets the score of a specified player
     * @param playername Name of player
     * @return Score
     */
    public static Float getScore(String playername) {

        Float score = 0f;

        try {
            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT * FROM oblig4.ranking WHERE player = ?");
            statement.setString(1, playername);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) ;
            score = resultSet.getFloat(2);

            conn.close(); // Close connection

        } catch (Exception e) {
            score = 0f; // If the player doesn't exist in table
        }

        return score;

    }

    public static String[] getPlayerValues() {
        return playerValues;
    }

    public static String getPlayerRandom(String playerName){

        String pRandom = "";

        try {
            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT player_2,player_2_random FROM oblig4.open_games WHERE player_1 = ?");
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) ;
            pRandom = resultSet.getString(1);

            conn.close(); // Close connection

        } catch (Exception e) {

        }

        return pRandom;

    }

    public static boolean hasConnection() {
        boolean hasConnection = false;
        try {
            Connector.getConnection();
            hasConnection = Connector.hasConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasConnection;
    }


    public static int[] getPlayerMove(String gameID) {

        int[] playerMoves = new int[2];

        try {

            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT player_1_move, player_2_move FROM oblig4.game_in_progress WHERE game_id = ?");
            statement.setString(1, gameID);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                int player1Move = resultSet.getInt(1);
                int player2Move = resultSet.getInt(2);

                 playerMoves[0] = player1Move;
                 playerMoves[1] = player2Move;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        } catch (Exception e){
            Debugger.print("EXCEPTION: " + e.getMessage());
        }

        return playerMoves;
    }

    public static boolean hasJoined(Player playerHost){

        boolean hasJoined = false;

        try {
            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT player_2, player_2_random FROM oblig4.open_games WHERE player_1_random= ?");
            statement.setString(1, playerHost.getRandom());

            ResultSet resultSet;
            resultSet = statement.executeQuery();

            if(resultSet.next()) {
                String player2 = resultSet.getString(1);
                String player2ID = resultSet.getString(2);

                if (player2 != null){
                    playerValues[0] = player2;
                    playerValues[1] = player2ID;
                    hasJoined = true;
                }
                System.out.println(player2);//SOUT
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
            System.out.println(e.getMessage()); //SOUT
        }catch (Exception e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
            System.out.println(e.getMessage());//SOUT
        }
        return hasJoined;



    }

    public static void updateMove(GameMaster gameMaster, Player player) {
        try {
            Connection conn = Connector.getConnection();

            String playerMove;
            String playerEnergy;

            if(player.equals(gameMaster.getSpecificPlayer(1))) {
                playerMove = "player_1_move";
                playerEnergy = "player_1_energy";
            }
            else {
                playerMove = "player_2_move";
                playerEnergy = "player_2_energy";
            }

            statement = conn.prepareStatement("UPDATE game_in_progress SET "+ playerMove +" = ? WHERE game_id = ? ");
            statement.setInt(1, player.getPlayerMove());
            statement.setString(2, gameMaster.getGameID());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GameMaster getGameInProgress(String gameId){

        try {
            Connection conn = Connector.getConnection(); // Make connection

            // Execute query
            statement = conn.prepareStatement("SELECT * FROM oblig4.game_in_progress WHERE game_id = ?");
            statement.setString(1, gameId);

            ResultSet result = statement.executeQuery();

            while(result.next()){
                String player1Name = result.getString(2);
                String player2Name = result.getString(3);
                int gamePos = result.getInt(4);
                int p1Energy = result.getInt(5);
                int p2Energy= result.getInt(6);
                int p1Move = result.getInt(7);
                int p2Move = result.getInt(8);
                int round = result.getInt(9);
                String player1Id = gameId.substring(0,9);

                Player player1 = new HumanPlayer(player1Name);
                Player player2 = new HumanPlayer(player2Name);
                player1.setPlayerID(gameId.substring(0,10));
                player2.setPlayerID(gameId.substring(10,20));
                player1.setCurrentEnergy(p1Energy);
                player2.setCurrentEnergy(p2Energy);
                player1.setPlayerMove(p1Move);
                player2.setPlayerMove(p2Move);

                GameMaster gameMaster = new GameMaster();
                gameMaster.setGamePosition(gamePos);
                gameMaster.setGameRound(round);
                gameMaster.setPlayers(player1,player2);

                return gameMaster;

            }
        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.print("EXCEPTION: " + e.getMessage());
        }
        return null;
    }

}
