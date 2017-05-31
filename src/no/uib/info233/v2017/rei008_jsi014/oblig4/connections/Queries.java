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
            Debugger.printException(e.getMessage());
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
            Debugger.printException(e.getMessage());
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
                    "INSERT INTO oblig4.game_in_progress(game_id, player_1, player_2, game_position, player_1_energy, player_2_energy, move_number) " +
                            "VALUES (?,?,?,?,?,?,?)");

            statement.setString(1, gameMaster.getGameID());
            statement.setString(2, player1.getName());
            statement.setString(3, player2.getName());
            statement.setInt(4, gameMaster.getGamePosition());
            statement.setInt(5, player1.getCurrentEnergy());
            statement.setInt(6, player2.getCurrentEnergy());
            statement.setInt(7, gameMaster.getGameRounds());
            statement.executeUpdate();

            conn.close();//Close connection

        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
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
                Debugger.printException(e.getMessage());
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

            gameMaster.updateGameID(gameMaster.getGameRounds());

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
            Debugger.printException(e.getMessage());
        } catch (Exception e) {
            e.getMessage();
            Debugger.printException(e.getMessage());
        }

        return updated;
    }


    /**
     * Loads a singleplayer game from the database
     * @param gameID ID of the game
     * @return A GameMaster containing the information needed to play
     */
    public static GameMaster loadSaved(String gameID, Player player) {

        GameMaster gameMaster = null;
        try {
            Connection conn = Connector.getConnection(); // Make connection

            // Execute query
            statement = conn != null ? conn.prepareStatement("SELECT * FROM oblig4.saved_games WHERE game_id = ?") : null;
            statement.setString(1, gameID);
            ResultSet rs = statement.executeQuery();

            // Gather information into local variables
            while (rs.next()) {
                String id = rs.getString("game_id");

                String p1 = rs.getString("player_1");
                String p2 = rs.getString("player_2");

                int gamePos = rs.getInt("game_position");

                int p1Energy = rs.getInt("player_1_energy");
                int p2Energy = rs.getInt("player_2_energy");


                // Set players based on information from saved_games table
                Player player2;
                player.setPlayerName(p1);

                if (id.substring(id.length()).equals('1')) {
                    player2 = new PassivePlayer(p2);
                } else {
                    player2 = new AggressivePlayer(p2);
                }

                player.setCurrentEnergy(p1Energy);
                player2.setCurrentEnergy(p2Energy);

                // Prepare gameMaster for battle
                gameMaster = new GameMaster();
                gameMaster.setGameID(id);
                gameMaster.setGamePosition(gamePos);
                //gameMaster.setGameRound(gameMaster.fetchIntInString(id));

                gameMaster.setPlayers(player, player2);

                Debugger.print("Success: The game has been successfully loaded!");
            }

            conn.close();
        } catch (Exception e) {
            Debugger.printException(e.getMessage());
        }

        if(gameMaster == null) {
            Debugger.printError("gameMater == null - The game was not loaded.");
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
            Debugger.printException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
        }

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
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());

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
            System.out.println("The Player score is: " + score);

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
            Connection conn = Connector.getConnection();
            hasConnection = Connector.hasConnection();
            System.out.println("I'm connected");
            conn.close();
        } catch (Exception e) {
            for(StackTraceElement ste : e.getStackTrace()) {
                Debugger.printError(ste.toString());
                System.out.println("I have no connection");
            }
        }
        return hasConnection;
    }


    public static String[] getPlayerMove(String gameID) {

        String[] playerMoves = new String[2];


        try {

            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT player_1_move, player_2_move FROM oblig4.game_in_progress WHERE game_id = ?");
            statement.setString(1, gameID);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                String player1Move = ( resultSet.getString(1));
                String player2Move = resultSet.getString(2);

                 playerMoves[0] = player1Move;
                 playerMoves[1] = player2Move;
                System.out.println("Player 1 moved: " + player1Move + "\nPlayer 2 moved: " + player2Move);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
        } catch (Exception e){
            Debugger.printException(e.getMessage());
        }

        return playerMoves;
    }

    public static boolean hasJoined(String hostID){

        boolean hasJoined = false;

        try {
            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT player_2, player_2_random FROM oblig4.open_games WHERE player_1_random= ?");
            statement.setString(1, hostID);

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
                System.out.println(player2 + " - Joined");//SOUT
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
            System.out.println(e.getMessage()); //SOUT
        }catch (Exception e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
            System.out.println(e.getMessage());//SOUT
        }
        return hasJoined;

    }

    public static void updateMove(GameMaster gameMaster, Player player) {
        try {
            Connection conn = Connector.getConnection();

            String playerMove;
            String playerEnergy;

            if(player.getHost()) {
                playerMove = "player_1_move";
                playerEnergy = "player_1_energy";
            }
            else {
                playerMove = "player_2_move";
                playerEnergy = "player_2_energy";
            }

            System.out.println("Player Move : " + player.getPlayerMove()); //SOUT
            statement = conn.prepareStatement("UPDATE game_in_progress SET "+ playerMove +" = ? , "+playerEnergy+"= ? WHERE game_id = ? ");
            statement.setInt(1, player.getPlayerMove());
            statement.setInt(2, player.getCurrentEnergy());
            statement.setString(3, gameMaster.getGameID());
            statement.executeUpdate();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static boolean removeOpenGame(String player1Random) {
        boolean removed = false;
        try {
            Connection conn = Connector.getConnection();

            statement = conn.prepareStatement("DELETE FROM open_games WHERE player_1_random = ?");
            statement.setString(1, player1Random);
            statement.executeUpdate();

            conn.close();
            removed = true;
            Debugger.print("The game was removed from open_games table");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return removed;
    }

    public static void updateGameInProgress(String gameID, GameMaster gameMaster) {
        try {
            Connection conn = Connector.getConnection();

            statement = conn.prepareStatement("UPDATE game_in_progress SET game_position = ?,  move_number = ? WHERE game_id = ?");
            statement.setInt(1, gameMaster.getGamePosition());
            statement.setInt(2, gameMaster.getGameRounds());
            statement.setString(3, gameID);
            statement.executeUpdate();
            Debugger.print("Game Updated in database");
            System.out.println("Game is updated in database");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetMoves(String gameID) {
        try {
            Connection conn = Connector.getConnection();

            statement = conn.prepareStatement("UPDATE game_in_progress SET player_1_move = NULL, player_2_move = NULL WHERE game_id = ?");
            statement.setString(1, gameID);
            statement.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasMoved(String gameID) {
        boolean hasMoved = false;
        try {
            Connection conn = Connector.getConnection();

            statement = conn.prepareStatement("SELECT player_1_move, player_2_move FROM game_in_progress WHERE game_id = ?");
            statement.setString(1, gameID);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String p1_move = rs.getString(1);
                String p2_move = rs.getString(2);
                System.out.println(p1_move + " - - - " + p2_move); //sout
                if(p1_move != null && p2_move != null) {
                    hasMoved = true;
                }
            }

            System.out.println("The players moved: " + hasMoved);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasMoved;
    }

    public static GameMaster getGameInProgress(String gameId){

        GameMaster gameMaster = new GameMaster();
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
                int round = result.getInt(9);

                Player player1 = new HumanPlayer(player1Name);
                Player player2 = new HumanPlayer(player2Name);

                player1.setPlayerID(gameId.substring(0,10));
                player2.setPlayerID(gameId.substring(10, gameId.length()));
                player1.setCurrentEnergy(p1Energy);
                player2.setCurrentEnergy(p2Energy);

                gameMaster.setGamePosition(gamePos);
                gameMaster.setGameRound(round);
                gameMaster.setPlayers(player1,player2);

                gameMaster.isGameOver();
                Debugger.print("Game is fetched from database");
                System.out.println("Game is fetched");//SOUT
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
        }
        return gameMaster;
    }

    public static void removeGameInProgress(String gameID) {

        try {
            Connection conn = Connector.getConnection();
            statement = conn.prepareStatement("DELETE FROM oblig4.game_in_progress WHERE game_id = ?");
            statement.setString(1, gameID);
            statement.executeUpdate();

            System.out.println("The game is removed from database");

            conn.close();
        }
        catch (SQLException e){

        }
        catch (Exception e){

        }
    }

    public static boolean gameExists(String gameID){

        boolean exist = false;

        try {
            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT game_id FROM oblig4.game_in_progress WHERE game_id= ?");
            statement.setString(1, gameID);

            ResultSet resultSet;
            resultSet = statement.executeQuery();

            if(resultSet.next()) {
                String gameId = resultSet.getString(1);

                if (gameID != null){
                    exist = true;
                }
            }

            System.out.println("The game " + gameID + " exists: " + exist);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
            System.out.println(e.getMessage()); //SOUT
        }catch (Exception e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
            System.out.println(e.getMessage());//SOUT
        }
        return exist;
    }

    public static boolean rowDeleted(String p1Random) {

        boolean exist = false;

        try {
            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT player_1_random FROM oblig4.open_games WHERE player_1_random= ?");
            statement.setString(1, p1Random);

            ResultSet resultSet;
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                exist = true;
            }

            System.out.println("Row was deleted: " + exist);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
            System.out.println(e.getMessage()); //SOUT
        } catch (Exception e) {
            e.printStackTrace();
            Debugger.printException(e.getMessage());
            System.out.println(e.getMessage());//SOUT
        }
        return exist;
    }


    public static int getGamePosition(String gameID) {
        int gamePosition = 0;
        try {
            Connection conn = Connector.getConnection(); // Make connection

            statement = conn.prepareStatement("SELECT game_position FROM oblig4.game_in_progress WHERE game_id = ?");
            statement.setString(1, gameID);

            ResultSet resultSet;
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                gamePosition = resultSet.getInt(1);
            }
        }
        catch (Exception e) {
            e.getStackTrace();
        }

        return gamePosition;
    }
}
