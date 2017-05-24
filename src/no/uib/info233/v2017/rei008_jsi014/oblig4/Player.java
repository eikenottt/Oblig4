package no.uib.info233.v2017.rei008_jsi014.oblig4;

import java.util.Random;
import java.util.UUID;

/**
 * Describes the super class of Player Subclasses
 * The class introduces commonalities between all playes
 * and introduces some basic attacks.
 * @author rei008
 * @author js014
 * @version 0.2
 *
 */
public abstract class Player {

    private String name;
    protected String playerID;
    private int currentEnergy;
    protected Random rand = new Random();
    protected boolean hasPulse;

    private GameMaster gameMaster;

    /**
     * Constructor for player
     * sets currentPosition as 0 by default
     * sets currentEnergy as 100 by default
     * @param name name
     */
    public Player(String name) {
        this.name = name;
        this.currentEnergy = 100;
        makePlayerID();
    }

    /**
     * Sets the gameMaster
     * @param gameMaster
     */
    public void registerGameMaster(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
    }


    /**
     * Takes in parameters of currentPosistion, currentEnergy and opponentEnergy
     * and uses the information to strategize the next move the player wants to make.
     * When the player has decided, it informs the gameMaster of how much energy it wants to spend,
     * @param currentPosition player position
     * @param yourEnergy player energy
     * @param opponentEnergy opponent energy
     */
    public abstract void makeNextMove(int currentPosition, int yourEnergy, int opponentEnergy);

    /**
     * Updates the energy value for player
     * @param value - adds value to the currentEnergy
     */
    public void updateEnergy(int value) {
        this.currentEnergy += value;
        if (currentEnergy < 0) {
            currentEnergy = 0;
        }
    }

    /**
     * Makes an unique ID combining the first four letters in the playername
     * and a random UUID string
     *
     * @return The random player ID
     */
    protected String makePlayerID() {

        int preferred_length = 10;

        // Generates the random alphanumeric code
        String uuid_raw = UUID.randomUUID().toString();
        String uuid = uuid_raw.replace("-", "");

        // Gets the first four letters of the playername
        String subName = (name.length() <= 4) ? name : name.substring(0,4);

        // Combines the two
        String player_id = subName + "_" + uuid;

        // Calculate the right string size
        int stringLength = subName.length() + uuid.length();
        int diff = stringLength - preferred_length;

        // Create the player id with preffered size
        playerID = player_id.substring(0, stringLength - diff);

        return playerID;
    }


    // ----------- Getters And Setters ----------- //
    public String getName() {
        return name;
    }

    public String getRandom() {
        return playerID;
    }

    public void setRandom(String playerID) {
        this.playerID = playerID;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public GameMaster getGameMaster() {
        return gameMaster;
    }

    public boolean getPulse() {
        return hasPulse;
    }



    // ------------ Attacks ----------- //

    /**
     * A high energy usage basic attack all players can perform.
     * implements some randomness
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    public int overheadSwing(int yourEnergy){
        int randNumber;
        randNumber = rand.nextInt(15);

        if(yourEnergy > 35){
            return 20 + randNumber;
        }else {
            return getCurrentEnergy();
        }
    }

    /**
     * Low predictability attack, implements a lot of randomness
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    public int stab(int yourEnergy){

        int randNumber;
        randNumber = this.rand.nextInt(50);

        if (yourEnergy > 50){
            return randNumber;
        }else if(yourEnergy>11){
            return randNumber/10 + 1;
        }else{
            return 0;
        }
    }

    /**
     * The most basic attack midrange energy usage
     * Implements some randomness in return
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    public int slash(int yourEnergy){

        int randNumber = this.rand.nextInt(15);
        if (yourEnergy > 20) {
            return 5 + randNumber;
        }else if( yourEnergy >= 5){
            return 5;
        }else return 0;

    }


    // --------- Generated Code --------- //

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
