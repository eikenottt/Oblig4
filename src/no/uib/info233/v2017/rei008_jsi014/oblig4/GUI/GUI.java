package no.uib.info233.v2017.rei008_jsi014.oblig4.GUI;

import no.uib.info233.v2017.rei008_jsi014.oblig4.*;
import no.uib.info233.v2017.rei008_jsi014.oblig4.connections.Queries;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeMap;


public class GUI{
    private String player1Name = "Player 1";
    private long time = System.currentTimeMillis();

    private GameMaster gameMaster;

    private Player player1, player2;

    private MainFrame mainFrame = new MainFrame("Game");

    private ButtonPanel menuButtons = new ButtonPanel("Singleplayer", "Multiplayer", "Quit Game"),
                singleplayerButtons = new ButtonPanel("New Game", "Load Game", "Back To Menu"),
                gameButtonsSingleplayer = new ButtonPanel("Stab", "Slash", "Overhead Swing", "Save Game", "Quit To Menu"),
                gameButtonsMultiplayer = new ButtonPanel("Stab", "Slash", "Overhead Swing", "Resign");

    private GamePanel gamePanel = new GamePanel();

    private ListPanel listPanel;

    private LabelPanel labelPanel;

    private MenuPanel menuPanel = new MenuPanel(player1Name, Queries.getScore(player1Name), menuButtons);

    public GUI() {

        mainFrame.add(menuPanel);
        System.out.println(System.currentTimeMillis() - time);

        ButtonPanel buttonPanell = new ButtonPanel("Continue", "Quit Game");

        JButton[] buttons = buttonPanell.getButtons();

        JOptionPane optionPane = new JOptionPane();

        player1Name = optionPane.showInputDialog("Player Name:");
        player1Name = (player1Name != null && !player1Name.equals("")) ? player1Name : "Player 1";
        player1 = new HumanPlayer(player1Name);
        menuPanel.setPlayerName(player1Name);

        /*for (int i = 0; i < 500; i++) {
            Debugger.print("This is a test, to check if it prints out a number:  " + i + "\n");
        }*/
    }

    private void exitProgram() {
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Exit Game", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }

    }

    private void quitToMenu(ButtonPanel buttonPanel) {
        int choice = JOptionPane.showOptionDialog(mainFrame, "Are you sure you want to quit the current game?", "Quit Current Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(choice == JOptionPane.YES_OPTION) {
            menuPanel.updateButtons(menuButtons);
            mainFrame.remove(gamePanel);
            mainFrame.changePanel(menuPanel);
            buttonPanel.makeClickable();
        }
    }

    private void loadList(String playerType) {
        if (Queries.hasConnection()) {
            listPanel = new ListPanel(playerType);
            mainFrame.remove(menuPanel);
            JPanel panel;
            if(playerType.equals("Singleplayer")){
                panel = listPanel.getPanel("Load");
            }
            else {
                panel = listPanel.getPanel("Join");
            }
            mainFrame.changePanel(panel);
            Debugger.print("Saved games successfully retrieved");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "You are not connected to Wildboy", "Connection error", JOptionPane.ERROR_MESSAGE);
            Debugger.print("There was a problem loading the saved games");
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

        private void setUI() {
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
                        if (keyLower.toString().contains("foreground"))
                            UIManager.put(key, fg);
                        if (keyLower.toString().contains("button.background"))
                            UIManager.put(key, buttonBG);
                        if (keyLower.toString().contains("button.foreground"))
                            UIManager.put(key, fg);
                        if (keyLower.toString().contains("button.select"))
                            UIManager.put(key, bg);
                        if (keyLower.toString().contains("button.focus"))
                            UIManager.put(key, buttonBG);
                        if (keyLower.toString().contains("textfield.background"))
                            UIManager.put(key, fg);
                        if (keyLower.toString().contains("textfield.foreground"))
                            UIManager.put(key, bg);
                        if (keyLower.toString().contains("list.background"))
                            UIManager.put(key, buttonBG);
                        if (keyLower.toString().contains("optionpane.messageforeground")) {
                            UIManager.put(key, fg);
                        }
                        if (keyLower.toString().contains("progressbar.foreground")) {
                            UIManager.put(key, ColorUIResource.RED);
                        }
                        if (keyLower.toString().contains("menu.foreground")) {
                            UIManager.put(key, fg);
                        }
                    }
                }
            }
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

            add(new ImagePanel(getClass().getResource("/img/icon.png").getPath()));
        }

        public void updateButtons(ButtonPanel buttonPanel) {
            remove(1);
            add(buttonPanel, 1);
        }

        public String getPlayerName () {
            return playerName;
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
        GameMaster gameMaster;

        public LabelPanel(String playerName, Float score) {
            setLayout(new GridBagLayout());
            JLabel playerNameLabel = new JLabel(playerName, JLabel.CENTER);
            JLabel scoreLabel = new JLabel("Score: "+score);

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
            JLabel numRoundsLabel = new JLabel("Round " + gameMaster.getGameRounds(), JLabel.CENTER);

            // EnergyBars
            player1EnergyBar = new JProgressBar(0,100);
            player2EnergyBar = new JProgressBar(0,100);
            player1EnergyBar.setForeground(Color.RED);
            player2EnergyBar.setBackground(Color.BLUE);
            player2EnergyBar.setForeground(new Color(70,70,70));
            player2EnergyBar.setStringPainted(true);
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

        public void setGameMaster(GameMaster gameMaster) {
            this.gameMaster = gameMaster;
        }
    }

    private class ImagePanel extends JPanel {

        GridBagConstraints gbc;
        ImageIcon imageIcon;

        public ImagePanel(String imgPath) {
            gbc = new GridBagConstraints();
            imageIcon = new ImageIcon(imgPath);
            JLabel imageIconLabel = new JLabel(imageIcon);
            setLayout(new GridBagLayout());

            gbc.weighty = 10;
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(imageIconLabel, gbc);

        }

        public ImageIcon getImage() {
            return imageIcon;
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

            add(buttonPanel);

            return this;
        }

    }

    private class ButtonPanel extends JPanel {

        JButton[] buttons;

        public ButtonPanel(String... buttonNames) {
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
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].getText().equals(buttonName)) {
                    buttons[i].setEnabled(false);
                }
            }
        }
        void makeClickable() {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setEnabled(true);
            }
        }


        public JButton[] getButtons() {
            return buttons;
        }

        private class ButtonListener extends Component implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {

                Debugger.print(e.getActionCommand() + " was pressed!\n");

                switch (e.getActionCommand()){
                    case "Singleplayer":
                        menuPanel.updateButtons(singleplayerButtons);
                        mainFrame.updateFrame();
                        break;
                    case "Multiplayer":
                        loadList("Multiplayer");
                        break;
                    case "New Game":
                        player1 = new HumanPlayer(player1Name);
                        player2 = new AggressivePlayer("CPU");
                        gameMaster = new GameMaster();
                        gameMaster.setPlayers(player1, player2);
                        mainFrame.remove(menuPanel);
                        mainFrame.changePanel(gamePanel.setGame(gameMaster, gameButtonsSingleplayer));
                        gameMaster.startGame();
                        break;
                    case "Back To Menu":
                        menuPanel.updateButtons(menuButtons);
                        mainFrame.updateFrame();
                        break;
                    case "Host Game":
                        gameMaster = new GameMaster();
                        gameMaster.hostGame(player1);
                        break;
                    case "Save Game":
                        if(gameMaster != null) {
                            gameMaster.saveGame();
                        }else {
                            Debugger.print("GameMaster was not initialized");
                        }
                        break;
                    case "Load Game":
                        loadList("Singleplayer");
                        break;
                    case "Stab":
                        doRound(player1.stab(player1.getCurrentEnergy()));
                        break;
                    case "Slash":
                        doRound(player1.slash(player1.getCurrentEnergy()));
                        break;
                    case "Overhead Swing":
                        doRound(player1.overheadSwing(player1.getCurrentEnergy()));
                        break;
                    case "Quit To Menu":
                        quitToMenu(ButtonPanel.this);
                        break;

                    case "Quit Game":
                        exitProgram();
                        break;


                }
            }
        }

        private void doRound(int energyUsed) {
            int currentEnergy = player1.getCurrentEnergy();
            if(!player1.useStab()){
                makeUnclickable("Stab");
            }
            if(!player1.useOverheadSwing()) {
                makeUnclickable("Overhead Swing");
            }
            if(!player1.useSlash()){
                makeUnclickable("Slash");
            }
            gameMaster.listenToPlayerMove(player1, energyUsed);
            labelPanel.setProgressbarEnergy(currentEnergy, player2.getCurrentEnergy());
            mainFrame.validate();
            mainFrame.repaint();
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
                            gameMaster.joinGame(id, player1);
                            gamePanel.setGame(gameMaster, gameButtonsMultiplayer);
                            mainFrame.remove(listPanel);
                            mainFrame.changePanel(gamePanel);
                        }
                        else {
                            gameMaster = new GameMaster();
                            gameMaster.loadGame(id);
                            gamePanel.setGame(gameMaster, gameButtonsSingleplayer);
                            mainFrame.remove(listPanel);
                            mainFrame.changePanel(gamePanel);
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
}
