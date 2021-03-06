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
        System.out.println("Min posisjon er " + currentPosition);
        switch (currentPosition){
            case -3: case -2: case -1: // 50% overheadSwing() 50% stab() for position 0, 1 and 2
                if ((randMove == 0 || randMove == 1)) {
                    System.out.println("Eg kjører overhead i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy = overheadSwing(yourEnergy);
                }else if (yourEnergy == 0){
                    System.out.println("Eg kjører alt i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy = yourEnergy;
                }else {
                    System.out.println("Eg kjører stab i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy = stab(yourEnergy);
                }
                break;
            case 0: // 25% stab() 75% slash in position 3
                if (randMove == 0 ){
                    System.out.println("Eg kjører stab i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy = stab(yourEnergy);
                }else{
                    System.out.println("Eg kjører slash i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy= slash(yourEnergy);
                }
                if(yourEnergy == 0){
                    System.out.println("Eg kjører alt i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy = yourEnergy;}
                break;
            case 1: case 2: case 3: // 25% slash 75% swordPoke in position 4, 5 and
                if(randMove == 3){
                    System.out.println("Eg kjører slash i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy = slash(yourEnergy);
                }else{
                    System.out.println("Eg kjører sword i posisjon " + currentPosition + "Og bruker " + yourEnergy + " Energi");
                    useEnergy = swordPoke(yourEnergy);
                }

                break;
            default:
                if(getCurrentEnergy() > 0) {
                    System.out.println("Eg kjører overhead i posisjon " + currentPosition);
                    useEnergy = overheadSwing(yourEnergy);
                } else{
                    System.out.println("Eg kjører nothing i posisjon " + currentPosition);
                    useEnergy = 0;}
                break;
        }

        playerMove = useEnergy;
        this.getGameMaster().listenToPlayerMove(this, useEnergy);
    }

    /**
     * Special attack for the PassivePlayer
     * Low output, with small amount of randomness
     * @param yourEnergy - the energy the player has available
     * @return int energy to spend
     */
    private int swordPoke(int yourEnergy){
        int energyUsage = 5;
        int randNumber = this.rand.nextInt(10);
        if (yourEnergy >=15){
            energyUsage += randNumber;
        } else if (yourEnergy > 10){
            energyUsage = randNumber;
        }else
            energyUsage =  yourEnergy;

        updateEnergy(-energyUsage);
        Debugger.print(this.getName() + " used swordPoke, with the force of " + energyUsage + " energy.\n"
                +this.getName()+" has "+this.getCurrentEnergy()+" energy left.");//message to debugger
        return energyUsage;
    }

    // --------- Generated Code --------- //

    @Override
    protected String makePlayerID() {
        String pasID = super.makePlayerID();
        char last = pasID.charAt(pasID.length()-1);
        pasID = pasID.replace(last, '1');
        return pasID;
    }
}
