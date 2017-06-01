package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import no.uib.info233.v2017.rei008_jsi014.oblig4.*;
import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;

/**
 * Provides the user interface for this specific program.
 * This class contains multiple inner classes.
 * @author rei008
 * @author jsi014
 * @version 0.2
 */
public class GUI{

    private long time = System.currentTimeMillis(); //Start time

    private String player1Name = "Player 1"; //Default player name

    private GameMaster gameMaster;

    private Player player, player2;

    private MainFrame mainFrame = new MainFrame("Sword Fighter"); // Title of the game

    // Buttonpanels used in the program are made here
    private ButtonPanel menuButtons = new ButtonPanel("Singleplayer", "Multiplayer", "Quit Game"),
                singleplayerButtons = new ButtonPanel("New Game", "Load Game", "Back To Menu"),
                loadButtons = new ButtonPanel("Back To Menu"),
                gameButtonsSingleplayer = new ButtonPanel("Stab", "Slash", "Overhead Swing", "Save Game", "Quit To Menu"),
                gameButtonsMultiplayer = new ButtonPanel("Stab", "Slash", "Overhead Swing", "Resign"),
                multiplayerButtons = new ButtonPanel("Host Game", "Back To Menu", "Refresh");

    private GamePanel gamePanel = new GamePanel();

    private LoadingPanel waitingPanel;

    private LabelPanel labelPanel;

    private static Timer timer, timerJoin;

    private ImagePanel imagePanel = new ImagePanel("icon");

    private MenuPanel menuPanel = new MenuPanel(player1Name, Queries.getScore(player1Name), menuButtons);

    public GUI() {

        mainFrame.add(menuPanel);
        System.out.println("The startup of the program was " + (System.currentTimeMillis() - time) + " milliseconds");

        // Input of the players name
        JOptionPane optionPane = new JOptionPane();
        player1Name = optionPane.showInputDialog("Player Name:");
        player1Name = (player1Name != null && !player1Name.equals("")) ? player1Name : "Player 1"; // set player name to "Player 1" if the input is empty or null
        player = new HumanPlayer(player1Name); // Make a player instance
        menuPanel.setPlayerName(player.getName());

    }

    /**
     * Updates the table if in game, and closes the program
     */
    private void exitProgram() {
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit Game", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            if(gameMaster != null) {
                int gamePos;
                if (timer != null)
                    timer.stop();

                if (player.equals(gameMaster.getSpecificPlayer(1))) {
                    gamePos = -3;
                } else {
                    gamePos = 3;
                }
                gameMaster.setGamePosition(gamePos);
                gameMaster.setGameOver(true);

            }
            System.exit(0);
        }
    }

    /**
     * Exits to menu from the current screen
     * @param buttonPanel - the new button panel
     * @param question - true: shows a question about leaving the screen, false: no questions asked
     * @return - true if the user pressed yes.
     */
    private boolean quitToMenu(ButtonPanel buttonPanel, boolean question){
        boolean quit = false;
        if(question) {
            int choice = JOptionPane.showOptionDialog(mainFrame, "Are you sure you want to quit the current game?", "Quit Current Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (choice == JOptionPane.YES_OPTION) {
                updatingPanelsToMenu(buttonPanel);
                quit = true;
            }
        }
        updatingPanelsToMenu(buttonPanel);
        return quit;
    }

    /**
     * Loads a list from the database. Load Game if the input is "Singleplayer", else "Multiplayer"
     * @param playerType - "Singleplayer" or "Multiplayer"
     * @return - The ListPanel as a JPanel
     */
    private JPanel loadList(String playerType) {
        if (GameMaster.hasConnection()) { //Fixme gameMaster .hasConnection
            ListPanel listPanel = new ListPanel(playerType);
            listPanel.populateGameTable(playerType);
            JPanel panel;
            if(playerType.equals("Singleplayer")){
                panel = listPanel.getPanel("Load");
                Debugger.print("Saved games successfully retrieved");
            }
            else {
                panel = listPanel.getPanel("Join");
                Debugger.print("Multiplayer games successfully retrieved");
            }
            return panel;
        } else {
            notConnected();
        }
        return null;
    }

    /**
     * Removes the gamePanel from the frame and shows the menu screen. This method also makes the buttons clickable.
     */
    private void updatingPanelsToMenu(ButtonPanel buttonPanel) {
        mainFrame.remove(gamePanel);
        labelPanel = new LabelPanel(player.getName(), Queries.getScore(player.getName()));
        imagePanel.removeImage();
        menuPanel.updateSection(imagePanel, 2);
        menuPanel.updateSection(labelPanel, 0);
        menuPanel.updateSection(menuButtons, 1);
        mainFrame.changePanel(menuPanel);
        buttonPanel.makeClickable();
    }

    /**
     * Shows a dialog if the program cant communicate with the database
     */
    private void notConnected() {
        JOptionPane.showMessageDialog(mainFrame, "You are not connected to Wildboy", "Connection error", JOptionPane.ERROR_MESSAGE);
        Debugger.print("There was a problem connecting to the server");
    }

    /**
     * Checks if the user has enough energy to press the attack buttons
     * @param buttonPanel - the buttonpanel to check
     */
    private void restrictor(ButtonPanel buttonPanel) {
        if(!player.useStab()){
            buttonPanel.makeUnclickable("Stab");
        }
        if(!player.useOverheadSwing()) {
            buttonPanel.makeUnclickable("Overhead Swing");
        }
        if(!player.useSlash()){
            buttonPanel.makeUnclickable("Slash");
        }
    }

    /**
     * This inner class makes the main frame for the program
     */
    private class MainFrame extends JFrame {

        MainFrame(String title) {
            super(title);
            setUI();
            setResizable(false);
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            setSize(1000, 500);

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

            // FileMenu
            JMenuBar menuBar = new DebuggerMenu();

            setJMenuBar(menuBar);

            setVisible(true);

            addWindowListener(new WindowListener() {
                @Override
                public void windowClosed(WindowEvent e) {
                }

                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    exitProgram();
                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });

        }

        /**
         * A method to change the panel shown in the Main frame
         * @param newPanel - the new panel
         */
        void changePanel(JPanel newPanel) {
            add(newPanel);
            revalidate();
            repaint();
            setVisible(true);
        }

        /**
         * updates the frame when needed
         */
        void updateFrame() {
            revalidate();
            repaint();
        }
    }

    /**
     * This inner class makes the application's menu screens
     */
    private class MenuPanel extends JPanel {
        String playerName;
        Float score;

        MenuPanel(String playerName, Float score, ButtonPanel buttonPanel) {

            this.playerName = playerName;
            this.score = score;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            add(new LabelPanel(playerName, score));

            add(buttonPanel);

            add(imagePanel);
        }


        /**
         * To update the section within the MenuPanel
         * @param panel - the panel to add to MenuPanel
         * @param sectionNr - in witch position to remove from and add to
         */
        void updateSection(JPanel panel, int sectionNr) {
            remove(sectionNr);
            add(panel, sectionNr);
        }

        /**
         * Update the playername in the gui
         * @param playerName - The new player name
         */
        void setPlayerName(String playerName) {
            this.playerName = playerName;
            this.score = Queries.getScore(playerName);

            remove(0);
            add(new LabelPanel(playerName, score), 0);
            validate();
            repaint();
        }

    }

    /**
     * This class places the information about the players in the gui
     */
    private class LabelPanel extends JPanel {
        GridBagConstraints gbc = new GridBagConstraints();
        JProgressBar player1EnergyBar, player2EnergyBar;
        JLabel numRoundsLabel;

        Float score;

        GameMaster gameMaster;

        /**
         * This Constructor is for making the score and name panel used in the MenuPanel
         * @param playerName - name of the player
         * @param score - the player's score
         */
        public LabelPanel(String playerName, Float score) {

            this.score = score;

            setLayout(new GridBagLayout());
            JLabel playerNameLabel = new JLabel(playerName, JLabel.CENTER);
            JLabel scoreLabel = new JLabel("Score: "+this.score);

            gbc.weightx = 0.5;
            gbc.weighty = 10;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            add(playerNameLabel, gbc);

            gbc.anchor = GridBagConstraints.FIRST_LINE_END;
            gbc.gridx = 2;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            add(scoreLabel, gbc);

        }

        /**
         * This Constructor makes the heading for the GamePanel. With the player names, energy bars and round count
         * @param gameMaster - the GameMaster that holds the information about the game
         */
        public LabelPanel(GameMaster gameMaster) {

            Player player1 = gameMaster.getSpecificPlayer(1);
            Player player2 = gameMaster.getSpecificPlayer(2);

            setLayout(new GridBagLayout());

            // Labels
            JLabel player1NameLabel = new JLabel(player1.getName());
            JLabel player2NameLabel = new JLabel(player2.getName());
            JLabel energyLabel = new JLabel("Energy", JLabel.CENTER);
            numRoundsLabel = new JLabel("Round " + gameMaster.getGameRounds(), JLabel.CENTER);

            // EnergyBars
            player1EnergyBar = new JProgressBar(0,100);
            player2EnergyBar = new JProgressBar(0,100);
            player1EnergyBar.setForeground(Color.RED);
            player2EnergyBar.setBackground(Color.BLUE);
            player2EnergyBar.setForeground(new Color(70,70,70));
            player2EnergyBar.setStringPainted(true);
            player1EnergyBar.setUI(new EnergyBarUI());
            player2EnergyBar.setUI(new EnergyBarUI());
            player1EnergyBar.setStringPainted(true);
            player1EnergyBar.setValue(player1.getCurrentEnergy());
            player2EnergyBar.setValue(100-player2.getCurrentEnergy());
            player1EnergyBar.setString(player1.getCurrentEnergy()+"");
            player2EnergyBar.setString(player2.getCurrentEnergy()+"");

            gbc.weightx = 0.5;
            gbc.weighty = 0.5;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(player1NameLabel, gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            add(player1EnergyBar, gbc);

            gbc.gridx = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            add(energyLabel, gbc);

            gbc.gridx = 3;
            gbc.anchor = GridBagConstraints.LINE_END;
            add(player2EnergyBar, gbc);

            gbc.gridx = 4;
            gbc.anchor = GridBagConstraints.LINE_START;
            add(player2NameLabel,gbc);

            gbc.gridx = 2;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            add(numRoundsLabel, gbc);

        }

        /**
         * Sets the energy bars to the given numbers
         * @param player1energy - the energy of player 2
         * @param player2energy - the energy of player 2
         */
        public void setProgressbarEnergy(int player1energy, int player2energy) {
            player1EnergyBar.setValue(player1energy);
            player1EnergyBar.setString(player1energy+"");
            player2EnergyBar.setValue(100- player2energy);
            player2EnergyBar.setString(player2energy+"");
        }

        /**
         * Sets the game round
         * @param rounds - the new round number
         */
        public void setRounds(int rounds){
            numRoundsLabel.setText("Round " + rounds);
        }

    }

    /**
     * This inner class holds the images
     */
    private class ImagePanel extends JPanel {
        GridBagConstraints gbc;

        private ImageIcon players = new ImageIcon(getClass().getResource("/img/Player-icons.png"));
        private ImageIcon icon = new ImageIcon(getClass().getResource("/img/icon.png"));
        private JLayeredPane layeredPane = new JLayeredPane();
        private JLabel playerLabel = new JLabel(players);
        private JLabel iconLabel = new JLabel(icon);

        /**
         * This constructor makes as many images as needed, by providing with the imagename. (this constructor only supports png files).
         * @param imgName - The name of the image
         */
        public ImagePanel(String... imgName) {
            gbc = new GridBagConstraints();

            layeredPane.setLayout(new GridBagLayout());
            setLayout(new GridBagLayout());
            int i = 0;

            for(String img  : imgName) {
                JLabel imageIconLabel = new JLabel(new ImageIcon(getClass().getResource("/img/"+img+".png")));

                gbc.weighty = 10;
                gbc.weightx = 2;
                gbc.gridx = i;
                gbc.gridy = 0;
                layeredPane.add(imageIconLabel, gbc, 5);


                i++;
            }
            add(layeredPane);

        }

        /**
         * This method adds a image on top of one of the other images
         * @param location - where to put the image
         */
        public void addImage(int location) {
            switch (location) {
                case -3:
                    location = 0;
                    break;
                case -2:
                    location = 1;
                    break;
                case -1:
                    location = 2;
                    break;
                case 0:
                    location = 3;
                    break;
                case 1:
                    location = 4;
                    break;
                case 2:
                    location = 5;
                    break;
                case 3:
                    location = 6;
                    break;
            }
            gbc.gridx = location;
            layeredPane.remove(playerLabel);
            layeredPane.add(playerLabel, gbc, 0);
            layeredPane.validate();
            layeredPane.repaint();
            add(layeredPane);
            validate();
            repaint();

        }

        /**
         * this removes the game panel images, and replaces it with the menu image
         */
        public void removeImage() {
            layeredPane.removeAll();
            layeredPane.add(iconLabel, gbc, 1);
            repaint();
        }

    }

    /**
     * This inner class contains a LabelPanel, ImagePanel and ButtonPanel used when playing in singleplayer mode or multiplayermode
     */
    private class GamePanel extends JPanel{

        /**
         * The constructor sets the layout to BoxLayout
         */
        public GamePanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        }

        /**
         * This is where the game panel gui is made
         * @param gameMaster - the information about the game
         * @param buttonPanel - the specific buttons to use, singleplayer or multiplayer
         * @return - the JPanel (GamePanel) to use in the MainFrame
         */
        public JPanel setGame(GameMaster gameMaster, ButtonPanel buttonPanel) {
            removeAll();
            labelPanel = new LabelPanel(gameMaster);
            add(labelPanel);
            imagePanel = new ImagePanel("redEnd", "redMark", "redMark", "middle", "blueMark", "blueMark", "blueEnd");
            imagePanel.addImage(gameMaster.getGamePosition());
            add(imagePanel);

            add(buttonPanel);

            return this;
        }



    }

    /**
     * This inner class makes the buttons used in the gui
     */
    private class ButtonPanel extends JPanel {

        JButton[] buttons;
        ImageIcon singleplayerIcon = new ImageIcon(getClass().getResource("/img/Singleplayer.png")),
                multiplayerIcon = new ImageIcon(getClass().getResource("/img/Multiplayer.png"));

        /**
         * Makes as many buttons as needed based on the amount of parameters
         * @param buttonNames - the names of the buttons
         */
        ButtonPanel(String... buttonNames) {
            buttons = new JButton[buttonNames.length];
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.weightx = 10;
            gbc.weighty = 0;

            gbc.fill = GridBagConstraints.BOTH;

            setLayout(new GridBagLayout());

            int x = 0;
            int y = 0;

            for(int i = 0; i < buttonNames.length; i++) {

                JButton button1;
                switch (buttonNames[i]){
                    case "Singleplayer":
                        button1 = new JButton(buttonNames[i], singleplayerIcon); // Sets icons on singleplayer button
                        break;
                    case"Multiplayer":
                        button1 = new JButton(buttonNames[i], multiplayerIcon); // Sets icons on multiplayer button
                        break;
                    case "Stab":
                        button1 = new JButton(buttonNames[i]);
                        button1.setToolTipText("1 - 15 Energy");
                        break;
                    case "Slash":
                        button1 = new JButton(buttonNames[i]);
                        button1.setToolTipText("5 - 20 Energy");
                        break;
                    case "Overhead Swing":
                        button1 = new JButton(buttonNames[i]);
                        button1.setToolTipText("15 - 35 Energy");
                        break;
                    default:
                        button1 = new JButton(buttonNames[i]);
                        break;
                }

                button1.addActionListener(new ButtonListener());
                buttons[i] = button1;

                if(x > 2) {
                    x=0;
                    y++;
                }
                gbc.weighty=10;
                gbc.gridx = x;
                gbc.gridy = y;
                add(button1, gbc);
                x++;
            }
        }

        /**
         * Makes the buttons unclickable
         * @param buttonName - the name of the button
         */
        void makeUnclickable(String buttonName) {
            for (JButton button : buttons) {
                if (button.getText().equals(buttonName)) {
                    button.setEnabled(false);
                }
            }
        }

        /**
         * Makes the attack buttons unclickable
         */
        void makeAttacksUnclickable() {
            String buttonName;
            for(JButton button : buttons){
                buttonName = button.getText();
                if(!buttonName.equals("Resign") && !buttonName.equals("Quit To Menu") && !buttonName.equals("Save Game")) {
                    button.setEnabled(false);
                }
            }
        }

        /**
         * Makes every button clickable
         */
        void makeClickable() {
            for (JButton button : buttons) {
                button.setEnabled(true);
            }
        }

        /**
         * This inner class is listens for a button click.
         */
        private class ButtonListener extends Component implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {

                Debugger.print(e.getActionCommand() + " was pressed!\n");

                switch (e.getActionCommand()){
                    case "Singleplayer":
                        menuPanel.updateSection(singleplayerButtons, 1);
                        mainFrame.updateFrame();
                        break;
                    case "Multiplayer":
                        if(Queries.hasConnection()) {
                            menuPanel.updateSection(multiplayerButtons, 1);
                            menuPanel.updateSection(loadList("Multiplayer"), 2);
                            mainFrame.remove(menuPanel);
                            mainFrame.changePanel(menuPanel);
                        }
                        else {
                            notConnected();
                        }

                        break;
                    case "New Game":
                        player = new HumanPlayer(player1Name);
                        Random random = new Random();
                        int randomNumber = random.nextInt(2);
                        System.out.println(randomNumber); //SOUT
                        if(randomNumber == 0) {
                            player2 = new AggressivePlayer("CPU");
                        }else {
                            player2 = new PassivePlayer("CPU");
                        }
                        gameMaster = new GameMaster();
                        gameMaster.setPlayers(player, player2);
                        mainFrame.remove(menuPanel);
                        mainFrame.changePanel(gamePanel.setGame(gameMaster, gameButtonsSingleplayer));
                        gameMaster.startGame();
                        break;
                    case "Back To Menu":
                        menuPanel.updateSection(menuButtons,1);
                        menuPanel.updateSection(imagePanel, 2);
                        imagePanel.removeImage();
                        mainFrame.updateFrame();
                        break;
                    case "Host Game":
                        gameMaster = new GameMaster();
                        gameMaster.hostGame(player);
                        waitingPanel = new LoadingPanel("Waiting for player...", true);

                        timer = new Timer(2000, evt -> {
                            if(!gameMaster.hasJoined(player.getRandom())){
                                waitingPanel.setVisible(true);
                                System.out.println("Waiting"); //SOUT
                            }
                            else {
                                SwingUtilities.invokeLater(() -> {
                                    System.out.println("Starting GamePanel"); //SOUT
                                    waitingPanel.dispose();
                                    mainFrame.remove(menuPanel);
                                    System.out.println("Hosting GameMaster: " + gameMaster); //SOUT
                                    mainFrame.changePanel(gamePanel.setGame(gameMaster, gameButtonsMultiplayer));
                                    mainFrame.setVisible(true);
                                    gameMaster.removeOpenGame(player.getRandom());
                                    gameMaster.startGame();
                                    ((Timer) evt.getSource()).stop();
                                });

                            }
                        });
                        timer.start();


                        break;
                    case "Save Game":
                        if(gameMaster != null) {
                            gameMaster.saveGame();
                        }else {
                            Debugger.print("GameMaster was not initialized");
                        }
                        break;
                    case "Load Game":
                        if(Queries.hasConnection()) {
                            menuPanel.updateSection(loadButtons, 1);
                            menuPanel.updateSection(loadList("Singleplayer"), 2);
                            mainFrame.updateFrame();
                        }
                        else {
                            notConnected();
                        }
                        break;
                    case "Refresh":
                        menuPanel.updateSection(loadList("Multiplayer"), 2);
                        mainFrame.remove(menuPanel);
                        mainFrame.changePanel(menuPanel);
                        break;
                    case "Stab":
                        doRound(player.stab(player.getCurrentEnergy()));
                        break;
                    case "Slash":
                        doRound(player.slash(player.getCurrentEnergy()));
                        break;
                    case "Overhead Swing":
                        doRound(player.overheadSwing(player.getCurrentEnergy()));
                        break;
                    case "Quit To Menu":
                        quitToMenu(ButtonPanel.this, true);
                        break;
                    case "Resign":
                        if(quitToMenu(ButtonPanel.this, true)) {
                            gameMaster.resign(player);
                        }
                        break;
                    case "Cancel":
                        System.out.println(this); //SOUT
                        waitingPanel.dispose();
                        if(player.getHost()) {
                            timer.stop();
                        }else {
                            timerJoin.stop();
                        }
                        Debugger.printError("The loading screen is Null");
                        break;
                    case "Quit Game":
                        exitProgram();
                        break;


                }
            }

        }

        /**
         * Runs when a user presses an attack button
         * @param energyUsed - int energy used represents the Attack, directly gathered from the button stab/slash/overhead swing
         */
        private void doRound(int energyUsed) {
            makeAttacksUnclickable();

            player2 = gameMaster.getSpecificPlayer(2);

            Player player1 = gameMaster.getSpecificPlayer(1);

            // ------- Singleplayer -------- //
            if(!gameMaster.getSpecificPlayer(2).getPulse()){
                labelPanel.setRounds(gameMaster.getGameRounds()); //DONE <-- prøver å legge update rounds her
                if(player.getCurrentEnergy()+energyUsed > 0) {
                    player.makeNextMove(gameMaster.getGamePosition(), energyUsed, player2.getCurrentEnergy());
                    player2.makeNextMove(gameMaster.getGamePosition(), player2.getCurrentEnergy(), player1.getCurrentEnergy());
                    if(player.getCurrentEnergy() <= 0){
                        while (!gameMaster.isGameOver()) {
                            player.makeNextMove(gameMaster.getGamePosition(), 0, player2.getCurrentEnergy());
                            player2.makeNextMove(gameMaster.getGamePosition(), player2.getCurrentEnergy(), player1.getCurrentEnergy());
                        }
                    }
                }
                makeClickable();
                restrictor(gameButtonsSingleplayer);
                updateGamePanel(player1, player2);
            }

            // --------- Multiplayer ---------- //
            else {
                waitForPlayer(energyUsed); //if its a multiplayer game, send it to the waitForPlayer

            }

            gameOver();

        }

    }

    /**
     * Updates the game panel
     * @param player1 - the Player object of player 1
     * @param player2 - the Player object of player 2
     */
    private void updateGamePanel(Player player1, Player player2) {
        labelPanel.setProgressbarEnergy(player1.getCurrentEnergy(), player2.getCurrentEnergy());
        labelPanel.setRounds(gameMaster.getGameRounds());
        imagePanel.addImage(gameMaster.getGamePosition());
        mainFrame.validate();
        mainFrame.repaint();
    }

    /**
     * Checks if game is over, if true then makes the game over frame
     */
    private void gameOver() {
        if(gameMaster.isGameOver()){
            gameButtonsSingleplayer.makeClickable();
            gameButtonsMultiplayer.makeClickable();

            if(player.equals(gameMaster.determineWinner())){
                new GameOverPanel("Winner");
            }
            else if(gameMaster.determineWinner() == null) {
                new GameOverPanel("Nice Tie");
            }
            else {
                new GameOverPanel("Loser");
            }

        }
    }


    /**
     * This method runs the logic behind the multiplayer section. This has a timer that checks if both players made a move.
     * @param energyUsed - the energy from the player
     */
    private void waitForPlayer(int energyUsed) {
        gameMaster.gameProcessor(player, energyUsed);

        timer = new Timer(2000, ev -> {
            if (gameMaster.isUpdated()) {
                gameMaster = gameMaster.getGameInProgress(gameMaster.getGameID());
                String[] playerMoves = Queries.getPlayerMove(gameMaster.getGameID());
                if (playerMoves[0] != null && playerMoves[1] != null) {
                    int p1_energyUse = Integer.valueOf(playerMoves[0]);
                    int p2_energyUse = Integer.valueOf(playerMoves[1]);
                    gameMaster.setP1_energyUse(p1_energyUse);
                    gameMaster.setP2_energyUse(p2_energyUse);
                    if(player.getHost()) {
                        try {
                            System.out.println("Her skal du sova");
                            Thread.sleep(1600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Sov du godt?");
                        gameMaster.resetMoves();
                        Debugger.print("Enemy used: " + p2_energyUse + " Energy");
                    }
                    else {
                        Debugger.print("Enemy used: " + p1_energyUse + " Energy");
                    }
                    gameMaster.evaluateTurn();
                    updateGamePanel(gameMaster.getSpecificPlayer(1), gameMaster.getSpecificPlayer(2));
                    ((Timer)ev.getSource()).stop();
                    playerMoves[0] = "0";
                    playerMoves[1] = "0";
                }
                gameButtonsMultiplayer.makeClickable();
                if(player.getCurrentEnergy() > 0){
                    restrictor(gameButtonsMultiplayer);
                }
                gameOver();
            }
        });
        timer.start();
    }

    /**
     * ListPanel makes a visual representation of the database lists from saved_games and open_games
     */
    private class ListPanel extends JPanel {
        private ArrayList<ArrayList<JComponent>> array = new ArrayList<>();
        private TreeMap<String, ArrayList<String>> playerMap = new TreeMap<>();

        private GridBagConstraints gbc = new GridBagConstraints();

        public ListPanel(String gameType) {
            setLayout(new BorderLayout());
            populateGameTable(gameType);
        }

        JPanel getPanel(String join) {
            JPanel m = new JPanel();
            ArrayList<JComponent> arr = new ArrayList<>();

            if(join.equals("Join")) {
                arr.add(new JLabel("Hostname"));
                arr.add(new JLabel("Highscore", JLabel.CENTER));
            }
            else {
                arr.add(new JLabel("Player 2 name"));
                arr.add(new JLabel("Player 1 name", JLabel.CENTER));
            }
            arr.add(new JLabel(join +" Game"));
            ArrayList<ArrayList<JComponent>> arrayList = new ArrayList<>();
            arrayList.add(arr);

            ArrayList<JComponent> fred = new ArrayList<>();
            JLabel tom = new JLabel("");
            tom.setPreferredSize(new Dimension(0,20));
            fred.add(new JLabel(""));
            fred.add(tom);
            fred.add(new JLabel(""));
            array.add(fred);

            JPanel k = new JPanel(new BorderLayout());
            k.setPreferredSize(new Dimension(1000, 50));
            amount(k, arrayList);
            getListReady(join);

            m.setLayout(new BoxLayout(m, BoxLayout.Y_AXIS));
            amount(m, array);

            JScrollPane scrollPane = new JScrollPane(m);
            //scrollPane.setForeground(new Color(70, 70, 70));
            add(k, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            return this;
        }

        /**
         * Makes every other row a different color.
         * @param pan - the panel to add to
         * @param num - an ArrayList with the conponents from the database
         */
        private void amount(JPanel pan, ArrayList<ArrayList<JComponent>> num) {

            for (int i = 0; i < num.size(); i++) {
                JPanel m = new JPanel(new GridBagLayout());

                if(i%2 == 0) {
                    m.setBackground(new Color(54,54,54));
                }
                else {
                    m.setBackground(new Color(37,37,37));
                }

                gbc.weightx = 0.5;
                gbc.weighty = 0.5;

                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                m.add(num.get(i).get(0), gbc);

                gbc.gridx = 0;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.anchor = GridBagConstraints.CENTER;
                m.add(num.get(i).get(1), gbc);

                gbc.gridx = 2;
                gbc.anchor =GridBagConstraints.LINE_END;
                m.add(num.get(i).get(2), gbc);

                pan.add(m);

            }
        }

        /**
         * Makes labels and buttons based on the information from the database
         * @param join
         */
        void getListReady(String join) {
            for (String id : playerMap.keySet()) {
                String hostName = playerMap.get(id).get(1);
                ArrayList<JComponent> array2 = new ArrayList<>();
                array2.add(new JLabel(hostName));
                array2.add(new JLabel(playerMap.get(id).get(0), JLabel.CENTER));
                array2.add(new JButton(new AbstractAction(join) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(join.equals("Join")){
                            gameMaster = new GameMaster();
                            waitingPanel = new LoadingPanel("Joining Game...", false);

                            final boolean[] hasJoined = new boolean[1];
                            gameMaster.joinGame(id, player);
                            String gameId = id + player.getRandom();
                            timerJoin = new Timer(2000, evt -> {
                                hasJoined[0] = gameMaster.gameExists(gameId); //FIXME gameMaster.hasJoined
                                if(hasJoined[0]) {
                                    SwingUtilities.invokeLater(() -> {
                                        gameMaster = gameMaster.getGameInProgress(gameId);
                                        Player p2 = gameMaster.getSpecificPlayer(1);
                                        gameMaster.setPlayers(p2, player);
                                        System.out.println(gameMaster.toString());

                                        mainFrame.remove(menuPanel);
                                        mainFrame.changePanel(gamePanel.setGame(gameMaster, gameButtonsMultiplayer));
                                        waitingPanel.dispose();
                                        mainFrame.setVisible(true);
                                        ((Timer)evt.getSource()).stop();
                                        Debugger.print("Success");
                                        timerJoin = null;
                                    });
                                }
                                else {
                                    waitingPanel.setVisible(true);
                                    Debugger.print("Trying To Connect");
                                }
                            });
                            timerJoin.start();

                        }
                        else {
                            gameMaster = new GameMaster();
                            gameMaster = gameMaster.loadGame(id, player);
                            System.out.println(gameMaster);//SOUT
                            imagePanel.removeImage();
                            mainFrame.remove(menuPanel);
                            menuPanel.updateSection(imagePanel, 2);
                            mainFrame.changePanel(gamePanel.setGame(gameMaster, gameButtonsSingleplayer));
                            restrictor(gameButtonsSingleplayer);
                            //gameMaster.startGame();
                            }
                        }
                }));
                array.add(array2);
            }
        }

        /**
         * Gets the information from the database
         * @param gameType
         */
        void populateGameTable(String gameType) {
            if(playerMap.size() > 0) {
                playerMap.clear();
            }
            if(gameType.equals("Singleplayer")) {
                playerMap = Queries.getGameMap(playerMap);
            }
            else {
                playerMap = Queries.getMultiplayerMap(playerMap);
            }
        }

    }

    /**
     * LoadingPanel is a frame that shows when the user is waiting for an action to take place
     */
    private class LoadingPanel extends JFrame {
        private ImageIcon waitingIcon = new ImageIcon(getClass().getResource("/img/loading.gif"));

        private JLabel messageLabel, imageLabel;
        private JButton cancelButton;

        /**
         * The constructor shows a text and a gif to indicate that something is happening
         * @param message
         * @param isHost
         */
        public LoadingPanel(String message, boolean isHost) {
            setUI();
            GridBagConstraints gbc = new GridBagConstraints();
            JPanel panel = new JPanel(new GridBagLayout());
            messageLabel = new JLabel(message, JLabel.CENTER);
            imageLabel = new JLabel(waitingIcon);
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                dispose();
                if(isHost) {
                    gameMaster.removeOpenGame(player.getRandom());
                    timer.stop();
                }
                else {
                    timerJoin.stop();
                }
            });

            //setModal(true);

            Dimension size = new Dimension(500,300);
            setPreferredSize(size);
            setSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setLocationRelativeTo(null);

            gbc.weightx = 0.5;
            gbc.weighty = 0.5;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.SOUTH;
            panel.add(messageLabel, gbc);
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(imageLabel, gbc);
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.NORTH;
            panel.add(cancelButton, gbc);
            add(panel);
            setVisible(false);
        }

    }

    /**
     * GameOverPanel shows the message when a game is over. There is 3 possible outcomes: Winner, Tie and Loser
     */
    private class GameOverPanel extends JDialog {

        private JLabel messageLabel, imageLabel, gameOverLabel, pointsLabel, roundLabel;
        private JButton cancelButton;

        /**
         * Creates the Dialog box
         * @param message
         */
        public GameOverPanel(String message) {
            setUI();
            GridBagConstraints gbc = new GridBagConstraints();

            setTitle("Game Over");
            if(gameMaster.getTimer() != null) {
                gameMaster.stopTimer();
            }

            JPanel panel = new JPanel(new GridBagLayout());
            gameOverLabel = new JLabel("Game Over", JLabel.CENTER);
            messageLabel = changeText(message);
            messageLabel.setFont(new Font("Calibri", Font.BOLD, 50));
            imageLabel = changeImage(message);
            roundLabel = new JLabel("Game ended in Round: " + gameMaster.getGameRounds());
            roundLabel.setFont(new Font("Calibri", Font.ITALIC, 25));
            roundLabel.setForeground(new Color(160,160,160));
            if(player.equals(gameMaster.getSpecificPlayer(1))) {
                pointsLabel = new JLabel("You earned " + gameMaster.getPointsFromPosition(gameMaster.getGamePosition()) + " points!");
            }else {
                pointsLabel = new JLabel("You earned " + gameMaster.getPointsFromPosition(gameMaster.getGamePosition()*-1) + " points!");
            }
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                dispose();
                quitToMenu(gameButtonsMultiplayer, false);
            });

            setModal(true);

            Dimension size = new Dimension(700,500);
            setPreferredSize(size);
            setSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setLocationRelativeTo(null);

            gbc.weightx = 0.5;
            gbc.weighty = 0.5;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(gameOverLabel, gbc);
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.SOUTH;
            panel.add(messageLabel, gbc);
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.NORTH;
            panel.add(roundLabel, gbc);
            gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.CENTER;
            panel.add(imageLabel, gbc);
            gbc.gridy = 4;
            gbc.anchor = GridBagConstraints.NORTH;
            panel.add(pointsLabel, gbc);
            gbc.gridy = 5;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.weighty = 3;
            panel.add(cancelButton, gbc);
            add(panel);
            setVisible(true);
        }

        /**
         * Changes the text in the label
         * @param message - the new text
         * @return - The new JLabel with the text
         */
        public JLabel changeText(String message) {
            return new JLabel(message, JLabel.CENTER);
        }

        /**
         * Changes the image shown with the message
         * @param message - the message
         * @return - A JLabel with the updated image
         */
        public JLabel changeImage(String message) {
            ImageIcon waitingIcon;
            if(message.equals("Winner")) {
                waitingIcon = new ImageIcon(getClass().getResource("/img/winner.png"));
            }
            else if(message.equals("Nice Tie")){
                waitingIcon = new ImageIcon(getClass().getResource("/img/tie.png"));
            }
            else {
                waitingIcon = new ImageIcon(getClass().getResource("/img/loser.png"));
            }

            return new JLabel(waitingIcon);
        }

    }

    /**
     * Changes the UI of the Java and Metal LookAndFeel
     */
    static void setUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        FontUIResource f = new FontUIResource("Calibri", Font.PLAIN, 22);
        ColorUIResource bg = new ColorUIResource(70, 70, 70);
        ColorUIResource fg = new ColorUIResource(255, 255, 255);
        Color buttonBG = new Color(120, 120, 120);
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            String keyLower = key.toString().toLowerCase();
            Object value = UIManager.get(key);
            if (value != null) {
                if (value instanceof FontUIResource) {
                    UIManager.put(key, f);
                }
                if (value instanceof ColorUIResource) {
                    if (keyLower.contains("background"))
                        UIManager.put(key, bg);
                    if (keyLower.contains("foreground"))
                        UIManager.put(key, fg);
                    if (keyLower.contains("button.background"))
                        UIManager.put(key, buttonBG);
                    if (keyLower.contains("button.foreground"))
                        UIManager.put(key, fg);
                    if (keyLower.contains("button.select"))
                        UIManager.put(key, bg);
                    if (keyLower.contains("button.focus"))
                        UIManager.put(key, buttonBG);
                    if (keyLower.contains("textfield.background"))
                        UIManager.put(key, fg);
                    if (keyLower.contains("textfield.foreground"))
                        UIManager.put(key, bg);
                    if (keyLower.contains("list.background"))
                        UIManager.put(key, buttonBG);
                    if (keyLower.contains("optionpane.messageforeground"))
                        UIManager.put(key, fg);
                    if (keyLower.contains("progressbar.selectionBackground"))
                        UIManager.put(key, fg);
                    if (keyLower.contains("progressbar.selectionForeground"))
                        UIManager.put(key, fg);
                    if (keyLower.contains("menu.foreground"))
                        UIManager.put(key, fg);
                }
            }
        }
    }

    /**
     * Changes the text of the energybar, and TextArea when selecting text
     */
    private class EnergyBarUI extends BasicProgressBarUI {
        @Override
        protected Color getSelectionForeground() {
            return Color.WHITE;
        }

        @Override
        protected Color getSelectionBackground() {
            return Color.WHITE;
        }
    }
}
