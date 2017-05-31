package no.uib.info233.v2017.rei008_jsi014.oblig4;


import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

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
        //updateEnergy(-yourEnergy);
        registerGameMaster(Queries.getGameInProgress(getGameMaster().getGameID()));
        System.out.println("gameMaster player2: " + getGameMaster()); //SOUT
        getGameMaster().listenToPlayerMove(this, yourEnergy);

    }

}
