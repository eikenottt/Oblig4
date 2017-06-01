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
    private String playerID;
    private int currentEnergy;
    Random rand = new Random();
    boolean hasPulse;
    int playerMove;
    boolean isHost;

    private GameMaster gameMaster;

    /**
     * Constructor for player
     * sets currentPosition as 0 by default
     * sets currentEnergy as 100 by default
     * @param name name
     */
    Player(String name) {
        this.name = name;
        this.currentEnergy = 100;
        makePlayerID();
        hasPulse = false;
        playerMove = 0;
        isHost = false;
    }

    /**
     * Sets the gameMaster
     * @param gameMaster the game to register for
     */
    void registerGameMaster(GameMaster gameMaster) {
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
    void updateEnergy(int value) {
        this.playerMove = value * -1;
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

    public void setPlayerName(String playerName) {
        this.name = playerName;
    }

    public String getRandom() {
        return playerID;
    }

    public void setPlayerID(String playerID){
        this.playerID = playerID;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    GameMaster getGameMaster() {
        return gameMaster;
    }

    public boolean getPulse() {
        return hasPulse;
    }

    public int getPlayerMove() {
        return playerMove;
    }

    public void setPlayerMove(int playerMove) {
        this.playerMove = playerMove;
    }

    public void setHost(boolean isHost){
        this.isHost = isHost;
    }



    public boolean getHost(){
        return this.isHost;
    }

    // ------------ Attacks ----------- //

    /**
     * A high energy usage basic attack all players can perform.
     * implements some randomness, if the player have less than 36 energy,
     * this attack will drain all energy which is left
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    public int overheadSwing(int yourEnergy){
        int randNumber;
        randNumber = rand.nextInt(15);
        int energyUsage;

        if(yourEnergy > 36){
            energyUsage = 20 + randNumber;
        }else {
            energyUsage =  getCurrentEnergy();
        }
        updateEnergy(-energyUsage);
        Debugger.print(this.getName() + " used overhead swing, with the force of " + energyUsage + " energy.\n"
                +this.getName()+" has "+this.getCurrentEnergy()+" energy left.");//message to debugger
        return energyUsage;
    }

    /**
     * Low energy attack, implements a lot of randomness
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    public int stab(int yourEnergy){

        int randNumber;
        int energyUsage = 1;
        randNumber = this.rand.nextInt(15);

        if (yourEnergy > 15){
            energyUsage += randNumber;
        }else if(yourEnergy > 5 && yourEnergy <= 15){
            energyUsage = 5;
        }
        if (yourEnergy <= 0){ energyUsage = 0;};
        updateEnergy(-energyUsage);

        Debugger.print(this.getName() + " used stab, with the force of " + energyUsage + " energy.\n"
                +this.getName()+" has "+this.getCurrentEnergy()+" energy left.");//message to debugger

        return energyUsage;
    }

    /**
     * The most basic attack midrange energy usage
     * Implements some randomness in return
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    public int slash(int yourEnergy){

        int energyUsage = 5;
        int randNumber = this.rand.nextInt(15);

        if (yourEnergy >= 20) {
            energyUsage = energyUsage + randNumber;
        }
        if (yourEnergy<=0){energyUsage = 0;}
        updateEnergy(-energyUsage);
        Debugger.print(this.getName() + " used slash, with the force of " + energyUsage + " energy.\n"
                +this.getName()+" has "+this.getCurrentEnergy()+" energy left.");//message to debugger
        return energyUsage;
    }

    // Checker to see if the player has enough energy to perform a specific attack
    public boolean useOverheadSwing(){
        boolean available = false;
        if (currentEnergy >= 15){
            available = true;

        }
        if (!available){
            Debugger.print("You don't have enough energy to use overhead swing!");
        }

        return available;
    }

    // Checker to see if the player has enough energy to perform a specific attack
    public boolean useStab(){
        boolean available = false;
        if (currentEnergy >= 1){
            available = true;
        }
        if (!available){
            Debugger.print("You don't have enough energy to use stab!");
        }

        return available;
    }

    // Checker to see if the player has enough energy to perform a specific attack
    public boolean useSlash(){
        boolean available = false;
        if(currentEnergy>=5){
            available = true;
        }
        if (!available){
            Debugger.print("You don't have enough energy to use slash!");
        }

        return available;
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
