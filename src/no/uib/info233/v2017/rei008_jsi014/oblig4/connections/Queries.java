package no.uib.info233.v2017.rei008_jsi014.oblig4.connections;

import no.uib.info233.v2017.rei008_jsi014.oblig4.GameMaster;
import no.uib.info233.v2017.rei008_jsi014.oblig4.HumanPlayer;
import no.uib.info233.v2017.rei008_jsi014.oblig4.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by runeeikemo on 24.05.2017.
 */
public final class Queries {

    private static PreparedStatement statement;

    /**
     * Sets player 1 as a host for a game
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
            if(rowCount > 0) {
                tableUpdated = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableUpdated;
    }

    /**
     * Handles the SQL-queries and manipulations in the database
     * @param player the player to be updated
     * @param score player score
     */
    public static boolean updateRanking(Player player, float score) {
        float prevScore;
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

                statement = conn.prepareStatement("DELETE FROM oblig4.ranking WHERE player = ? AND score = ?");
                statement.setString(1, player.getName());
                statement.setFloat(1, score);
                statement.executeQuery();

                // Replace into is used for replacing old values in tables with primary keys
                statement = conn.prepareStatement("REPLACE INTO ranking(player, score) VALUES (?, ?)");
                statement.setString(1, player.getName());
                statement.setFloat(2, score);
                statement.executeUpdate();

                conn.close(); // Close connection

                updated = true; // Success
            } catch (Exception e) {
                e.printStackTrace();
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
            statement = conn.prepareStatement("DELETE FROM oblig4.ranking WHERE game_id = ? AND game_position = ?");
            statement.setString(1, gameMaster.getGameID());
            statement.setInt(2, gameMaster.getGamePosition());
            statement.executeQuery();

            // Insert new
            statement = conn.prepareStatement( "REPLACE INTO saved_games (game_id, player_1, player_2, game_position, player_1_energy, player_2_energy) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1,gameMaster.getGameID());
            statement.setString(2,player1.getName());
            statement.setString(3,player2.getName());
            statement.setInt(4,gameMaster.getGamePosition());
            statement.setInt(5,player1.getCurrentEnergy());
            statement.setInt(6,player2.getCurrentEnergy());
            statement.executeUpdate();



            conn.close(); // Close connection

            updated = true; // Success

        } catch (Exception e) {
            e.printStackTrace();
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
            statement = conn.prepareStatement("SELECT * FROM saved_games WHERE game_id = ?");
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
            Player player2 = new HumanPlayer(p2); //TODO generate computerplayer based on last digit in game_id

            player1.setCurrentEnergy(p1Energy);
            player2.setCurrentEnergy(p2Energy);

            // Prepare gameMaster for battle
            gameMaster = new GameMaster();
            gameMaster.setGameID(id);
            gameMaster.setGamePosition(gamePos);

            gameMaster.setPlayers(player1, player2);


        } catch (SQLException e) { // TODO make ready for Debugger
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(gameMaster == null) {
            // TODO Debugger message gamemaster is null, break method
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
                score.add(result.getString(4));
                // Player Name code
                score.add(result.getString(1));
                // Player Random and everything else
                playersMap.put(result.getString(2), score);
            }

            conn.close(); // Close Connection

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO if Map is Empty

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

}