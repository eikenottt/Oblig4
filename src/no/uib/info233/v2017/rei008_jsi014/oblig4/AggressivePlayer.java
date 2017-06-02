package no.uib.info233.v2017.rei008_jsi014.oblig4;

/**
 * Defines a BOT with a more agressive playing style
 *
 * @author rei008
 * @author jsi014
 * @version 0.2
 */

public class AggressivePlayer extends Player {

    /**
     * Constructor for player
     * sets currentPosition as 0 by default
     * sets currentEnergy as 100 by default
     * @param name name
     */
    public AggressivePlayer(String name) {
        super(name);
    }

    public void makeNextMove(int currentPosition, int yourEnergy, int opponentEnergy) {
        int randMove = rand.nextInt(4);
        int useEnergy;
        System.out.println("Min posisjon er " + currentPosition);
        switch (currentPosition) {
            case -3:
            case -2: //25% whirlwind(), 75% slash() for position 0 and 1
                if (randMove == 0) {
                    System.out.println("Eg kjører virvel i posisjon " + currentPosition);
                    useEnergy = whirlwind(yourEnergy);
                } else
                    if (useSlash()){
                        System.out.println("Eg kjører slash i posisjon " + currentPosition);
                        useEnergy = slash(yourEnergy);
                    } else{
                        System.out.println("Eg kjører stab i posisjon " + currentPosition);
                        useEnergy = stab(yourEnergy);
                    }

                break;
            case -1:
                System.out.println("Eg kjører overhead i posisjon " + currentPosition);
                useEnergy = overheadSwing(yourEnergy);
                break;
            case 0:
            case 1: // 50% chance of using stab(),50% of using overheadSwing in position 3 and 4
                if (randMove == 0 || randMove == 1) {
                    System.out.println("Eg kjører stab i posisjon " + currentPosition);
                    useEnergy = stab(yourEnergy);
                } else {
                    System.out.println("Eg kjører overhead i posisjon " + currentPosition);
                    useEnergy = overheadSwing(yourEnergy);
                }
                break;
            case 2: // 25% Slash(), 25% Stab() and 50% Whirlwind()
                if (randMove == 0) {
                    System.out.println("Eg kjører slash i posisjon 2");
                    useEnergy = slash(yourEnergy);
                } else if (randMove == 1) {
                    System.out.println("Eg kjører stab i posisjon 2");
                    useEnergy = stab(yourEnergy);
                } else {
                    System.out.println("Eg kjører virvelvind i posisjon 2");
                    useEnergy = whirlwind(yourEnergy);
                }
                break;
            default:
                useEnergy = 1;
                break;

        }
        playerMove = useEnergy;
        this.getGameMaster().listenToPlayerMove(this, useEnergy);
    }

    /**
     * Special attack for the AggressivePlayer
     * High output, with relative high amount of randomness
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    private int whirlwind(int yourEnergy){
        int energyUsage = 5;
        int randNumber = this.rand.nextInt(25);
        if (yourEnergy >=30){
            energyUsage += randNumber;
        } else if (yourEnergy > 25){
            energyUsage = randNumber;
        }else
            energyUsage =  yourEnergy;

        updateEnergy(-energyUsage);
        Debugger.print(this.getName() + " used Whirlwind, with the force of " + energyUsage + " energy.\n"
                +this.getName()+" has "+this.getCurrentEnergy()+" energy left.");//message to debugger
        return energyUsage;
    }

    // --------- Generated Code --------- //

    @Override
    protected String makePlayerID() {
        String aggID = super.makePlayerID();
        char last = aggID.charAt(aggID.length()-1);
        aggID = aggID.replace(last, '0');
        return aggID;
    }
}
