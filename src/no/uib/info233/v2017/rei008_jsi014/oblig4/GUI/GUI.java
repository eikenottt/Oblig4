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


public class GUI{
    private long time = System.currentTimeMillis();

    private String player1Name = "Player 1";

    private GameMaster gameMaster;

    private Player player, player2;

    private MainFrame mainFrame = new MainFrame("Game");

    private ButtonPanel menuButtons = new ButtonPanel("Singleplayer", "Multiplayer", "Quit Game"),
                singleplayerButtons = new ButtonPanel("New Game", "Load Game", "Back To Menu"),
                loadButtons = new ButtonPanel("Back To Menu"),
                gameButtonsSingleplayer = new ButtonPanel("Stab", "Slash", "Overhead Swing", "Save Game", "Quit To Menu"),
                gameButtonsMultiplayer = new ButtonPanel("Stab", "Slash", "Overhead Swing", "Resign"),
                multiplayerButtons = new ButtonPanel("Host Game", "Back To Menu", "Refresh");

    private GamePanel gamePanel = new GamePanel();

    private ListPanel listPanel;

    private LoadingPanel waitingPanel;

    private LabelPanel labelPanel;

    private static Timer timer;

    private ImagePanel imagePanel = new ImagePanel("icon");

    private MenuPanel menuPanel = new MenuPanel(player1Name, Queries.getScore(player1Name), menuButtons);

    public GUI() {

        mainFrame.add(menuPanel);
        System.out.println(System.currentTimeMillis() - time);

        ButtonPanel buttonPanell = new ButtonPanel("Continue", "Quit Game");

        JButton[] buttons = buttonPanell.getButtons();

        JOptionPane optionPane = new JOptionPane();

        player1Name = optionPane.showInputDialog("Player Name:");
        player1Name = (player1Name != null && !player1Name.equals("")) ? player1Name : "Player 1";
        player = new HumanPlayer(player1Name);
        menuPanel.setPlayerName(player.getName());

    }

    private void exitProgram() {
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit Game", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private boolean quitToMenu(ButtonPanel buttonPanel) {
        boolean quit = false;
        int choice = JOptionPane.showOptionDialog(mainFrame, "Are you sure you want to quit the current game?", "Quit Current Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(choice == JOptionPane.YES_OPTION) {
            imagePanel.removeImage();
            menuPanel.updateSection(menuButtons, 1);
            mainFrame.remove(gamePanel);
            mainFrame.changePanel(menuPanel);
            //player.setCurrentEnergy(100);
            buttonPanel.makeClickable();
            quit = true;
        }
        return quit;
    }

    private JPanel loadList(String playerType) {
        if (GameMaster.hasConnection()) { //Fixme gameMaster .hasConnection
            listPanel = new ListPanel(playerType);
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

    private void notConnected() {
        JOptionPane.showMessageDialog(mainFrame, "You are not connected to Wildboy", "Connection error", JOptionPane.ERROR_MESSAGE);
        Debugger.print("There was a problem connecting to the server");
    }

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

    private class MainFrame extends JFrame {
        String playerName, fstBtn, sndBtn, trdBtn;

        Float score;

        public MainFrame(String title) {
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


        void changePanel(JPanel newPanel) {
            add(newPanel);
            revalidate();
            repaint();
            setVisible(true);
        }

        void updateFrame() {
            revalidate();
            repaint();
        }
        public MainFrame getFrame() {
            return this;
        }

    }

    private class MenuPanel extends JPanel {
        String playerName;
        Float score;

        ButtonPanel buttonPanel;

        public MenuPanel(String playerName, Float score, ButtonPanel buttonPanel) {

            this.playerName = playerName;
            this.score = score;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            add(new LabelPanel(playerName, score));

            add(buttonPanel);

            add(imagePanel);
        }



        public void updateSection(JPanel panel, int sectionNr) {
            remove(sectionNr);
            add(panel, sectionNr);
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
            this.score = Queries.getScore(playerName);

            remove(0);
            add(new LabelPanel(playerName, score), 0);
            validate();
            repaint();
        }

    }

    private class LabelPanel extends JPanel {
        GridBagConstraints gbc = new GridBagConstraints();
        JProgressBar player1EnergyBar, player2EnergyBar;
        JLabel numRoundsLabel;

        Float score;

        GameMaster gameMaster;

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

        public void setProgressbarEnergy(int player1energy, int player2energy) {
            player1EnergyBar.setValue(player1energy);
            player1EnergyBar.setString(player1energy+"");
            player2EnergyBar.setValue(100- player2energy);
            player2EnergyBar.setString(player2energy+"");
        }

        public void setRounds(int rounds){
            numRoundsLabel.setText("Round " + rounds);
        }

        public void setGameMaster(GameMaster gameMaster) {
            this.gameMaster = gameMaster;
        }

        public void updateScore(Float score) {
            this.score = score;
        }

    }

    private class ImagePanel extends JPanel {
        GridBagConstraints gbc;

        private ImageIcon players = new ImageIcon(getClass().getResource("/img/Player-icons.png"));
        private ImageIcon icon = new ImageIcon(getClass().getResource("/img/icon.png"));
        private JLayeredPane layeredPane = new JLayeredPane();
        private JLabel playerLabel = new JLabel(players);
        private JLabel iconLabel = new JLabel(icon);

        public ImagePanel(String... imgPath) {
            gbc = new GridBagConstraints();

            layeredPane.setLayout(new GridBagLayout());
            setLayout(new GridBagLayout());
            int i = 0;

            for(String img  : imgPath) {
                JLabel imageIconLabel = new JLabel(new ImageIcon(getClass().getResource("/img/"+img+".png")));

                gbc.weighty = 10;
                gbc.weightx = 2;
                gbc.gridx = i;
                gbc.gridy = 0;
                layeredPane.add(imageIconLabel, gbc, 3);


                i++;
            }
            add(layeredPane);

        }
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
            layeredPane.add(playerLabel, gbc, 1);
            layeredPane.validate();
            layeredPane.repaint();
            add(layeredPane);
            validate();
            repaint();

        }

        public void removeImage() {
            layeredPane.removeAll();
            layeredPane.add(iconLabel, gbc, 1);
            repaint();
        }

    }

    private class GamePanel extends JPanel{

        public GamePanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        }

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

    private class ButtonPanel extends JPanel {

        JButton[] buttons;

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

                JButton button1 = new JButton(buttonNames[i]);
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
        void makeUnclickable(String buttonName) {
            for (JButton button : buttons) {
                if (button.getText().equals(buttonName)) {
                    button.setEnabled(false);
                }
            }
        }

        void makeAttacksUnclickable() {
            String buttonName;
            for(JButton button : buttons){
                buttonName = button.getText();
                if(!buttonName.equals("Resign") && !buttonName.equals("Quit To Menu") && !buttonName.equals("Save Game")) {
                    button.setEnabled(false);
                }
            }
        }


        void makeClickable() {
            for (JButton button : buttons) {
                button.setEnabled(true);
            }
        }

        JButton[] getButtons() {
            return buttons;
        }
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
                        quitToMenu(ButtonPanel.this);
                        break;
                    case "Resign":
                        if(quitToMenu(ButtonPanel.this)) {
                            gameMaster.resign(player);
                        }
                        break;
                    case "Cancel":
                        System.out.println(this); //SOUT
                        waitingPanel.dispose();
                        timer.stop();
                        Debugger.printError("The loading screen is Null");
                        break;
                    case "Quit Game":
                        exitProgram();
                        break;


                }
            }

        }
        private void doRound(int energyUsed) {
            makeAttacksUnclickable();

            player2 = gameMaster.getSpecificPlayer(2);

            Player player1 = gameMaster.getSpecificPlayer(1);

            if(player1.equals(player)) {

            }

            if(!gameMaster.getSpecificPlayer(2).getPulse()){
                labelPanel.setRounds(gameMaster.getGameRounds()); //DONE <-- prøver å legge update rounds her
                restrictor(gameButtonsSingleplayer);
                if(player.getCurrentEnergy() > 0) {
                    player.makeNextMove(gameMaster.getGamePosition(), energyUsed, player2.getCurrentEnergy());
                    player2.makeNextMove(gameMaster.getGamePosition(), player2.getCurrentEnergy(), player1.getCurrentEnergy());
                }
                else {
                    while (!gameMaster.isGameOver()) {
                        player.makeNextMove(gameMaster.getGamePosition(), 0, player2.getCurrentEnergy());
                        player2.makeNextMove(gameMaster.getGamePosition(), player2.getCurrentEnergy(), player1.getCurrentEnergy());
                    }
                }
                makeClickable();
            }
            else {
                restrictor(gameButtonsMultiplayer);
                player1.getPlayerMove();
                waitForPlayer(energyUsed);
            }

            labelPanel.setProgressbarEnergy(player1.getCurrentEnergy(), player2.getCurrentEnergy());
            labelPanel.setRounds(gameMaster.getGameRounds());
            imagePanel.addImage(gameMaster.getGamePosition());
            mainFrame.validate();
            mainFrame.repaint();

            System.out.println(gameMaster.isGameOver());//SOUT denne hadde du glemt
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

    }


    private void waitForPlayer(int energyUsed) {
        player.makeNextMove(gameMaster.getGamePosition(), energyUsed, player2.getCurrentEnergy());
        timer = new Timer(2000, e -> {
            Debugger.print("Waiting for the other player to make a move");
            if (gameMaster.hasMoved(gameMaster.getGameID())) {
                gameButtonsMultiplayer.makeClickable();
                ((Timer)e.getSource()).stop();
                gameMaster.updateGameInProgress(gameMaster.getGameID());
                getInformaiton();
                Debugger.print("Your turn");
            }
        });
        timer.start();
    }

    private void getInformaiton() {
        if(player.getHost()) {
            gameMaster = gameMaster.getGameInProgress(gameMaster.getGameID());
        }
        else {
            Player p2 = gameMaster.getSpecificPlayer(1);
            gameMaster.setPlayers(p2, player);
        }

    }

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

            arr.add(new JLabel("Playername"));
            arr.add(new JLabel("Highscore", JLabel.CENTER));
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
            getFromStuff(join);

            m.setLayout(new BoxLayout(m, BoxLayout.Y_AXIS));
            amount(m, array);

            JScrollPane scrollPane = new JScrollPane(m);
            //scrollPane.setForeground(new Color(70, 70, 70));
            add(k, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            return this;
        }

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

        void getFromStuff(String join) {
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
                            timer = new Timer(2000, evt -> {
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
                                    });
                                }
                                else {
                                    waitingPanel.setVisible(true);
                                    Debugger.print("Trying To Connect");
                                }
                            });
                            timer.start();

                        }
                        else {
                            gameMaster = new GameMaster();
                            gameMaster = gameMaster.loadGame(id, player);
                            System.out.println(gameMaster);//SOUT
                            mainFrame.remove(menuPanel);
                            mainFrame.changePanel(gamePanel.setGame(gameMaster, gameButtonsSingleplayer));
                            restrictor(gameButtonsSingleplayer);
                            gameMaster.startGame();
                            }
                        }
                }));
                array.add(array2);
            }
        }

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

    private class LoadingPanel extends JFrame {
        private ImageIcon waitingIcon = new ImageIcon(getClass().getResource("/img/loading.gif"));

        private JLabel messageLabel, imageLabel;
        private JButton cancelButton;

        public LoadingPanel(String message, boolean isHost) {
            setUI();
            GridBagConstraints gbc = new GridBagConstraints();
            JPanel panel = new JPanel(new GridBagLayout());
            messageLabel = new JLabel(message, JLabel.CENTER);
            imageLabel = new JLabel(waitingIcon);
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                dispose();
                timer.stop();
                if(isHost) {
                    gameMaster.removeOpenGame(player.getRandom());
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
    private class GameOverPanel extends JDialog {

        private JLabel messageLabel, imageLabel, gameOverLabel, pointsLabel, roundLabel;
        private JButton cancelButton;

        public GameOverPanel(String message) {
            setUI();
            GridBagConstraints gbc = new GridBagConstraints();

            setTitle("Game Over");

            JPanel panel = new JPanel(new GridBagLayout());
            gameOverLabel = new JLabel("Game Over", JLabel.CENTER);
            messageLabel = changeText(message);
            messageLabel.setFont(new Font("Calibri", Font.BOLD, 50));
            imageLabel = changeImage(message);
            roundLabel = new JLabel("Game ended in Round: " + gameMaster.getGameRounds());
            roundLabel.setFont(new Font("Calibri", Font.ITALIC, 25));
            roundLabel.setForeground(new Color(160,160,160));
            pointsLabel = new JLabel("You earned " + gameMaster.getPointsFromPosition(gameMaster.getGamePosition()) + " points!");
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                dispose();
                imagePanel.removeImage();
                mainFrame.remove(gamePanel);
                labelPanel = new LabelPanel(player.getName(), Queries.getScore(player.getName()));
                menuPanel.updateSection(labelPanel, 0);
                menuPanel.updateSection(menuButtons, 1);
                mainFrame.changePanel(menuPanel);
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

        public JLabel changeText(String message) {
            return new JLabel(message, JLabel.CENTER);
        }

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
