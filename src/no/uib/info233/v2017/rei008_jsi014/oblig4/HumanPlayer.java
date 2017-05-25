package no.uib.info233.v2017.rei008_jsi014.oblig4;


import java.util.Scanner;

public class HumanPlayer extends Player {

    /**
     * Constructor for player
     * sets currentPosition as 0 by default
     * sets currentEnergy as 100 by default
     *
     * @param name name
     */
    public HumanPlayer(String name) {
        super(name);
        hasPulse = true;
    }

    @Override
    public void makeNextMove(int currentPosition, int yourEnergy, int opponentEnergy) {
        Scanner input = new Scanner(System.in);
        String attack = input.next();
        int energyUse;

        switch (attack.toLowerCase()) {
            case "stab":
                energyUse = stab(yourEnergy);
                break;
            case "slash":
                energyUse = slash(yourEnergy);
                break;
            default:
                energyUse = overheadSwing(yourEnergy);
                break;
        }

        updateEnergy(-energyUse);
        getGameMaster().listenToPlayerMove(this, energyUse);
    }
}
