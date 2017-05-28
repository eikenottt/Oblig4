package no.uib.info233.v2017.rei008_jsi014.oblig4;


public class PassivePlayer extends Player{
    /**
     * Constructor for player
     * sets currentPosition as 0 by default
     * sets currentEnergy as 100 by default
     *
     * @param name name
     */
    public PassivePlayer(String name) {
        super(name + "Passive");
    }

    @Override
    public void makeNextMove(int currentPosition, int yourEnergy, int opponentEnergy) {
        int randMove = rand.nextInt(4);
        int useEnergy;
        switch (currentPosition){
            case -3: case -2: case -1: // 50% overheadSwing() 50% stab() for position 0, 1 and 2
                if ((randMove == 0 || randMove == 1)) {
                    useEnergy = overheadSwing(yourEnergy);
                }else
                    useEnergy = stab(yourEnergy);
                break;
            case 0: // 25% stab() 75% slash in position 3
                if (randMove == 0 ){
                    useEnergy = stab(yourEnergy);
                }else{
                    useEnergy= slash(yourEnergy);
                }
                break;
            case 1: case 2: case 3: // 25% slash 75% swordPoke in position 4, 5 and 6
                if(randMove == 3){
                    useEnergy = slash(yourEnergy);
                }else{
                    useEnergy = swordPoke(yourEnergy);
                }
                break;
            default:
                if(getCurrentEnergy() > 0) {
                useEnergy = overheadSwing(yourEnergy);
                } else{
                useEnergy = 0;}
                break;
        }

        playerMove++;
        this.updateEnergy(-useEnergy);
        this.getGameMaster().listenToPlayerMove(this, useEnergy);
    }

    @Override
    protected String makePlayerID() {
        String pasID = super.makePlayerID();
        char last = pasID.charAt(pasID.length()-1);
        pasID = pasID.replace(last, '1');
        return pasID;
    }

    /**
     * Special attack for the PassivePlayer
     * Low output, with small amount of randomness
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    private int swordPoke(int yourEnergy){
        int randNumber = this.rand.nextInt(10);
        if (yourEnergy >=15){
            return 5 + randNumber;
        } else if (yourEnergy > 10){
            return randNumber;
        }else
            return yourEnergy;
    }
}
