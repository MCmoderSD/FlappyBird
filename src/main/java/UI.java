import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public class UI extends JFrame {
    public static UI instance;
    private final String Background;
    private final int scoredPoints;
    private JButton bStart;
    private JPanel UI;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private JSpinner spinnerTPS;
    private Timer refreshLeaderBoard;
    private int TPS = 100;
    private boolean newGame = true, isUploaded = true;

    public UI(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        scoredPoints = points;
        Background = backgroundImage;
        instance = this;

        initFrame(width, height, title, icon, resizable, backgroundImage, args, scoredPoints, Tickrate);

        score.setVisible(false);
        playerName.setVisible(true);
        leaderBoard.setVisible(false);
        soundCheckBox.setSelected(true);

        bStart.addActionListener(e -> {
            if (newGame) {
                TPS = (int) spinnerTPS.getValue();
                play(TPS, args);
            } else if (scoredPoints >= 0 && !isUploaded) {
                upload(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
            }
        });
    }

    private void play(int Tickrate, String[] args) {
        new Main().run(Tickrate, getSound(), args);
        dispose();
    }

    public void initFrame(int width, int height, String title, String icon, boolean resizable, String backgroundImage, String[] args, int points, int Tickrate) {
        if (Tickrate <= TPS) TPS = Tickrate;

        add(UI);
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(resizable);
        setIconImage((Methods.instance.reader(icon)));
        Movement.instance.backgroundResetX = 0;
        UI.repaint();
        initLeaderBoard(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);

        if (points >= 0) {
            isUploaded = false;
            newGame = false;
            score.setVisible(true);
            playerName.setVisible(true);
            score.setText("Dein Score: " + points);
            bStart.setText("Score Bestätigen");
            bStart.setToolTipText("Lade deinen Score hoch");
        }
    }

    private void upload(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        bStart.setText("Nochmal Spielen");
        bStart.setToolTipText("Nochmal Spielen");
        isUploaded = true;
        newGame = true;
        if ((playerName.getText().length() == 0)) {
            if (playerName.getText().length() <= 32) {
                if (checkUserName(playerName.getText())) {
                    writeLeaderBoard();
                } else {
                    JOptionPane.showMessageDialog(null, "Der Username ist nicht erlaubt!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    new UI(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Der Username ist zu lang!", "Fehler", JOptionPane.ERROR_MESSAGE);
                new UI(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
                dispose();
            }
        }
    }

    private boolean getSound() {
        return soundCheckBox.isSelected();
    }

    private void createUIComponents() {
        UI = new JPanel() {
            @Override
            protected void paintComponent(Graphics gUI) {
                super.paintComponent(gUI);
                gUI.drawImage(Methods.instance.reader(Background), Movement.instance.backgroundResetX, 0, Methods.instance.getBackgroundWidth(), getHeight(), this);
                repaint();
            }
        };

        bStart = new JButton();
        bStart.setOpaque(false);
        bStart.setToolTipText("Starte das Spiel");

        playerName = new JTextField();
        playerName.setOpaque(false);
        playerName.setToolTipText(" ");
        playerName.setHorizontalAlignment(JTextField.CENTER);
        Methods.instance.setPlaceholder(playerName, "Username");


        spinnerTPS = new JSpinner();
        spinnerTPS.setOpaque(false);
        spinnerTPS.setValue(TPS);
        spinnerTPS.addChangeListener(e -> {
            if (spinnerTPS.getValue() != null) {
                if ((int) spinnerTPS.getValue() >= 100)
                    spinnerTPS.setValue(100);
                if ((int) spinnerTPS.getValue() <= 1)
                    spinnerTPS.setValue(1);
            }
        });
        JFormattedTextField txt = ((JSpinner.DefaultEditor) spinnerTPS.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false); // Verhindert ungültige Eingaben
    }

    @SuppressWarnings("unused")
    private void initLeaderBoard(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        leaderBoard.setVisible(true);
        // ToDo Daten vom SQL Server holen und in die Tabelle einfügen
        refreshLeaderBoard = new Timer(1000, e -> {
            if (checkSQLConnection()) {
                // ToDo Daten vom SQL Server holen und in die Tabelle einfügen
            } else {
                JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum SQL Server hergestellt werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                handleNoSQLConnection(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
            }
        });
        refreshLeaderBoard.start();
    }

    private void handleNoSQLConnection(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum SQL Server hergestellt werden! \nVersuche es nochmal order gib kein Username ein", "Fehler", JOptionPane.ERROR_MESSAGE);
        refreshLeaderBoard.stop();
        new UI(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
    }

    private void writeLeaderBoard() {
        String userSQL = ""; // ToDo Usernamen vom SQL Server holen

        int scoreSQL = 0; // ToDo Score vom SQL Server holen

        // ToDo Daten in die Tabelle einfügen

        if (Objects.equals(playerName.getText(), userSQL) && scoredPoints > scoreSQL) {
            // ToDo Score vom SQL Server aktualisieren
        }
    }

    @SuppressWarnings("unused")
    private boolean checkUserName(String userName) {
        File blockedTerms = new File("src/main/resources/blockedTerms.txt");
        // ToDo userName in blockedTerms suchen
        return true;
    }

    private boolean checkSQLConnection() {
        // ToDo SQL Server Verbindung testen
        return true;
    }
}