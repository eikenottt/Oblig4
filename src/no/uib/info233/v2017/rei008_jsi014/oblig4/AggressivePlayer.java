package no.uib.info233.v2017.rei008_jsi014.oblig4;

/**
 * Created by Rune on 25.05.2017.
 */
public class AggressivePlayer extends Player {

    /**
     * Constructor for player
     * sets currentPosition as 0 by default
     * sets currentEnergy as 100 by default
     *
     * @param name name
     */
    public AggressivePlayer(String name) {
        super(name);
    }

    public void makeNextMove(int currentPosition, int yourEnergy, int opponentEnergy) {
        int randMove = rand.nextInt(4);
        int useEnergy;
        switch (currentPosition) {
            case -3:
            case -2: //25% whirlwind(), 75% slash() for position 0 and 1
                if (randMove == 0) {
                    useEnergy = whirlwind(yourEnergy);
                } else
                    useEnergy = slash(yourEnergy);
                break;
            case -1:
                useEnergy = overheadSwing(yourEnergy);
                break;
            case 0:
            case 1: // 50% chance of using stab(),50% of using overheadSwing in position 3 and 4
                if (randMove == 0 || randMove == 1) {
                    useEnergy = stab(yourEnergy);
                } else {
                    useEnergy = overheadSwing(yourEnergy);
                }
                break;
            case 2:
            case 3: // 25% of using slash(), 25% stab(), 50% whirilwind()
                if (randMove == 0) {
                    useEnergy = slash(yourEnergy);
                } else if (randMove == 1) {
                    useEnergy = stab(yourEnergy);
                } else
                    useEnergy = whirlwind(yourEnergy);
                break;
            default:
                useEnergy = 0;
                break;

        }
        this.updateEnergy(-useEnergy);
        this.getGameMaster().listenToPlayerMove(this, useEnergy);
    }

    /**
     * Special attack for the AggressivePlayer
     * High output, with relative high amount of randomness
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    private int whirlwind(int yourEnergy){
        int randNumber = this.rand.nextInt(25);
        if (yourEnergy >=30){
            return 5 + randNumber;
        } else if (yourEnergy > 25){
            return randNumber;
        }else
            return yourEnergy;
    }
}
