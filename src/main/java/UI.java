import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

@SuppressWarnings("unused")
public class UI extends JFrame {
    public static UI instance;
    private final String Background;
    private final int scoredPoints;
    private final Database database;
    private JButton bStart;
    private JPanel UI, tablePanel;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private JSpinner spinnerTPS;
    private JScrollPane scrollPane;
    private Timer refreshLeaderBoard;
    private int TPS = 100;
    private boolean newGame = true, isUploaded = true;

    public UI(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        scoredPoints = points;
        Background = backgroundImage;
        instance = this;

        database = new Database("mcmodersd.live", "3306", "FlappyBirdLeaderboard", "test", "test");

        initFrame(width, height, title, icon, resizable, backgroundImage, args, scoredPoints, Tickrate);
        spinnerTPS.setValue(TPS);
        score.setVisible(true);
        playerName.setVisible(true);
        score.setText("Global Leaderboard");
        initLeaderBoard(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);


        bStart.addActionListener(e -> {
            if (newGame) {
                int spinnerValue = (int) spinnerTPS.getValue();
                if (spinnerValue <= 100 && spinnerValue > 0) TPS = spinnerValue;
                play(TPS, args);
            } else if (scoredPoints >= 0 && !isUploaded) {
                upload(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
            }
        });


    }

    private void play(int Tickrate, String[] args) {
        new Main().run(Tickrate, soundCheckBox.isSelected(), args);
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
        Dimension frameDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((frameDimension.width - width) / 2, (frameDimension.height - height) / 2);

        if (points >= 0) {
            isUploaded = false;
            newGame = false;
            playerName.setEnabled(true);
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
        if (!Logic.instance.developerMode) {
            if (playerName.getText().length() != 0) {
                if (playerName.getText().length() <= 32) {
                    if (!Methods.instance.checkUserName(playerName.getText())) {
                        writeLeaderBoard(playerName.getText(), points);
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

        soundCheckBox = new JCheckBox();
        soundCheckBox.setOpaque(false);
        soundCheckBox.setToolTipText("Aktiviere oder deaktiviere den Sound");
        soundCheckBox.setBorder(BorderFactory.createEmptyBorder());
        soundCheckBox.setSelected(true);

        playerName = new JTextField();
        playerName.setOpaque(false);
        playerName.setEnabled(false);
        playerName.setFont(new Font("Roboto", Font.PLAIN, 22));
        playerName.setToolTipText("Gib deinen Username ein");
        playerName.setHorizontalAlignment(JTextField.CENTER);
        playerName.setBorder(BorderFactory.createEmptyBorder());
        Methods.instance.setPlaceholder(playerName, "Username");

        scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        leaderBoard = new JTable();
        leaderBoard.setFont(new Font("Roboto", Font.PLAIN, 22));
        leaderBoard.setOpaque(false);

        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);
        scrollPane.add(leaderBoard);
        scrollPane.setOpaque(false);

        spinnerTPS = new JSpinner();
        spinnerTPS.setOpaque(false);
        spinnerTPS.setToolTipText("Ticks pro Sekunde");
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

    private void initLeaderBoard(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        leaderBoard.setVisible(true);

        Database.Table table = new Database.Table("leaderboard", database);
        Database.Table.Column users = new Database.Table.Column("users", table);
        Database.Table.Column highscores = new Database.Table.Column("scores", table);
        leaderBoard.getTableHeader().setReorderingAllowed(false); // Spaltenverschiebung deaktivieren

        // Daten vom SQL Server holen und in die Tabelle einfügen
        String[] userValues = users.getValues();
        String[] scoreValues = highscores.getValues();

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Die Zellen sind nicht editierbar
            }
        };
        model.addColumn("Username");
        model.addColumn("Highscore");

        if (userValues != null && scoreValues != null && userValues.length == scoreValues.length) {
            for (int i = 0; i < userValues.length; i++) {
                model.addRow(new Object[]{userValues[i], scoreValues[i]});
            }
        }

        // Tabelle nach der Punktzahl sortieren
        sortTableByScore(model);

        leaderBoard.setModel(model);
        adjustRowHeight(leaderBoard);

        refreshLeaderBoard = new Timer(10000, e -> {
            if (Methods.instance.checkSQLConnection("MCmoderSD.live", 3306)) {
                String[] updatedUserValues = users.getValues();
                String[] updatedScoreValues = highscores.getValues();

                if (updatedUserValues != null && updatedScoreValues != null && updatedUserValues.length == updatedScoreValues.length) {
                    model.setRowCount(0);
                    for (int i = 0; i < updatedUserValues.length; i++) {
                        model.addRow(new Object[]{updatedUserValues[i], updatedScoreValues[i]});
                    }
                }

                // Tabelle nach der Punktzahl sortieren
                sortTableByScore(model);
                adjustRowHeight(leaderBoard);
            } else {
                JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum SQL Server hergestellt werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                handleNoSQLConnection(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
            }
        });
        refreshLeaderBoard.start();
    }

    private void sortTableByScore(DefaultTableModel model) {
        ArrayList<RowData> rowDataList = new ArrayList<>();

        // Daten aus der Tabelle in die ArrayList kopieren
        for (int i = 0; i < model.getRowCount(); i++) {
            String user = model.getValueAt(i, 0).toString();
            int score = Integer.parseInt(model.getValueAt(i, 1).toString());
            RowData rowData = new RowData(i, user, score);
            rowDataList.add(rowData);
        }

        // ArrayList nach der Punktzahl sortieren
        rowDataList.sort(Comparator.comparingInt(RowData::getScore).reversed());

        // Aktualisierte Daten in die Tabelle einfügen
        model.setRowCount(0);
        for (RowData rowData : rowDataList) {
            model.addRow(new Object[]{rowData.getUser(), rowData.getScore()});
        }
    }

    private void adjustRowHeight(JTable table) {
        int rowHeight = table.getRowHeight();
        FontMetrics fontMetrics = table.getFontMetrics(table.getFont());
        Font boldFont = new Font("Roboto", Font.BOLD, 24);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Spalte "User" zentriert ausrichten

        for (int row = 0; row < table.getRowCount(); row++) {
            int fontHeight = fontMetrics.getHeight() + 10;
            table.setRowHeight(row, Math.max(rowHeight, fontHeight));
        }

        table.getTableHeader().setFont(boldFont);
    }


    private void handleNoSQLConnection(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum SQL Server hergestellt werden! \nVersuche es nochmal order gib kein Username ein", "Fehler", JOptionPane.ERROR_MESSAGE);
        refreshLeaderBoard.stop();
        new UI(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
        dispose();
    }

    private void writeLeaderBoard(String username, int score) {
        Database.Table table = new Database.Table("leaderboard", database);
        Database.Table.Column users = new Database.Table.Column("users", table);
        Database.Table.Column highscores = new Database.Table.Column("scores", table);

        ArrayList<String> usernames = new ArrayList<>(Arrays.asList(users.getValues()));
        ArrayList<String> scores = new ArrayList<>(Arrays.asList(highscores.getValues()));

        if (usernames.contains(username)) {
            int index = usernames.indexOf(username);
            int oldScore = Integer.parseInt(scores.get(index));
            if (score > oldScore) {
                scores.set(index, String.valueOf(score));
                // ToDo: Score Updaten
            }
        } else {
            System.out.println("Username ist nicht im System");

            // ToDo: Username und Score in die Datenbank einfügen mit Score

        }


        String userSQL = ""; // ToDo: Usernamen vom SQL Server holen
        int scoreSQL = 0; // ToDo: Score vom SQL Server holen

    }


    static class RowData {
        private final int index;
        private final String user;
        private final int score;

        public RowData(int index, String user, int score) {
            this.index = index;
            this.user = user;
            this.score = score;
        }

        public int getIndex() {
            return index;
        }

        public String getUser() {
            return user;
        }

        public int getScore() {
            return score;
        }
    }
}