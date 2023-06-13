import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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
    private Database database;
    private JButton bStart;
    private JPanel UI, tablePanel;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private JSpinner spinnerTPS;
    private JScrollPane scrollPane;
    private final Timer updateDatabase;
    private final String host = "MCmoderSD.live", port = "3306";
    private int TPS = 100;
    private final int frameWidth;
    private final int frameHeight;
    private boolean newGame = true, isUploaded = true;


    // Konstruktor und Instanz
    public UI(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, boolean sound , String[] args, int points) {
        scoredPoints = points;
        frameWidth = width;
        frameHeight= height;
        Background = backgroundImage;
        instance = this;

        // Initialisierung des Fensters
        initFrame(width, height, title, icon, resizable, backgroundImage, args, scoredPoints, Tickrate);
        spinnerTPS.setValue(TPS);
        score.setVisible(true);
        playerName.setVisible(true);
        soundCheckBox.setSelected(sound);

        // Timer für die Aktualisierung der Bestenliste
        updateDatabase = new Timer(5000, e -> initLeaderBoard(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points));

        // Initialisierung der Datenbankverbindung und der Bestenliste

        if (Methods.instance.checkSQLConnection(host, port)) {
            database = new Database(host, port, "FlappyBirdLeaderboard", "flappy", "2013");
            initLeaderBoard(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
            updateDatabase.start();
        }

        // ActionListener für den Start-Button
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

    // Methode zum Starten des Spiels
    private void play(int Tickrate, String[] args) {
        new Main().run(Tickrate, soundCheckBox.isSelected(), args);
        updateDatabase.stop();
        dispose();
    }

    // Methode zum Initialisieren des Fensters
    public void initFrame(int width, int height, String title, String icon, boolean resizable, String backgroundImage, String[] args, int points, int Tickrate) {
        if (Tickrate <= TPS)
            TPS = Tickrate;

        add(UI);
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(resizable);
        setIconImage((Methods.instance.reader(icon)));

        Movement.instance.backgroundResetX = 0;

        score.setText("Global Leaderboard");

        if (points >= 0) {
            isUploaded = false;
            newGame = false;
            playerName.setEnabled(true);
            score.setText("Dein Score: " + points);
            bStart.setText("Score Bestätigen");
            bStart.setToolTipText("Lade deinen Score hoch");
        }
    }

    // Methode zum Hochladen des Scores
    private void upload(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        bStart.setText("Nochmal Spielen");
        bStart.setToolTipText("Nochmal Spielen");
        isUploaded = true;
        newGame = true;

        if (!Logic.instance.developerMode && !Logic.instance.cheatsEnabled) {
            if (playerName.getText().length() != 0 && !playerName.getText().contains("Username")) {
                if (playerName.getText().length() <= 32) {
                    if (!Methods.instance.checkUserName(playerName.getText()) && !playerName.getText().contains(" ")) {
                        writeLeaderBoard(playerName.getText(), points);
                    } else {
                        JOptionPane.showMessageDialog(null, "Der Username ist nicht erlaubt!", "Fehler", JOptionPane.ERROR_MESSAGE);
                        new UI(width, height, title, icon, resizable, backgroundImage, Tickrate, soundCheckBox.isSelected(), args, points);
                        updateDatabase.stop();
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Der Username ist zu lang!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    new UI(width, height, title, icon, resizable, backgroundImage, Tickrate, soundCheckBox.isSelected(), args, points);
                    updateDatabase.stop();
                    dispose();
                }
            }
        }
    }

    // Methode zum Initialisieren der Fensterelemente
    private void createUIComponents() {

        UI = new JPanel() {
            @Override
            protected void paintComponent(Graphics gUI) {
                super.paintComponent(gUI);
                gUI.drawImage(Methods.instance.reader(Background), Movement.instance.backgroundResetX, 0, Methods.instance.getBackgroundWidth(), getHeight(), this);
                repaint();
            }
        };

        Dimension frameDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((frameDimension.width - frameWidth) / 2, (frameDimension.height - frameHeight) / 2);

        bStart = new JButton();
        bStart.setOpaque(false);
        bStart.setToolTipText("Starte das Spiel");

        soundCheckBox = new JCheckBox();
        soundCheckBox.setOpaque(false);
        soundCheckBox.setToolTipText("Aktiviere oder deaktiviere den Sound");
        soundCheckBox.setBorder(BorderFactory.createEmptyBorder());

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

    // Methode zum Aktualisieren des Leaderboards
    private void initLeaderBoard(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        if (Methods.instance.checkSQLConnection(host, port)) {
            leaderBoard.setVisible(true);

            Database.Table table = database.getTable("leaderboard");
            Database.Table.Column users = table.getColumn("users");
            Database.Table.Column highscores = table.getColumn("scores");
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
            model.addColumn("Rank");
            model.addColumn("Username");
            model.addColumn("Highscore");

            if (userValues != null && scoreValues != null && userValues.length == scoreValues.length) {
                for (int i = 0; i < userValues.length; i++) {
                    model.addRow(new Object[]{(i + 1) + ".", userValues[i], scoreValues[i]});
                }
            }

            // Tabelle nach der Punktzahl sortieren
            sortTableByScore(model);

            leaderBoard.setModel(model);
            adjustRowHeight(leaderBoard);
            adjustColumnWidths(leaderBoard);
        } else {
            handleNoSQLConnection(width, height, title, icon, resizable, backgroundImage, Tickrate, args, points);
        }
    }

    // Methode zum Sortieren der Tabelle nach der Punktzahl
    private void sortTableByScore(DefaultTableModel model) {
        ArrayList<RowData> rowDataList = new ArrayList<>();

        // Daten aus der Tabelle in die ArrayList kopieren
        for (int i = 0; i < model.getRowCount(); i++) {
            String user = model.getValueAt(i, 1).toString();
            int score = Integer.parseInt(model.getValueAt(i, 2).toString());
            RowData rowData = new RowData(i + 1, user, score);
            rowDataList.add(rowData);
        }

        // ArrayList nach der Punktzahl sortieren
        rowDataList.sort(Comparator.comparingInt(RowData::getScore).reversed());

        // Aktualisierte Daten in die Tabelle einfügen
        model.setRowCount(0);
        for (int i = 0; i < rowDataList.size(); i++) {
            RowData rowData = rowDataList.get(i);
            model.addRow(new Object[]{(i + 1) + ".", rowData.getUser(), rowData.getScore()});
        }
    }

    // Methode zum Anpassen der Zeilenhöhe
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

    // Methode zum Anpassen der Spaltenbreiten
    private void adjustColumnWidths(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn rankColumn = columnModel.getColumn(0);
        rankColumn.setMaxWidth(800); // Maximale Breite der Rang-Zeile auf 50 setzen
        rankColumn.setMinWidth(100); // Mindestbreite der Rang-Zeile auf 50 setzen
        rankColumn.setPreferredWidth(50); // Bevorzugte Breite der Rang-Zeile auf 50 setzen

        // Die restlichen Spaltenbreiten anpassen, um Platz für die Rang-Zeile zu schaffen
        for (int col = 1; col < columnModel.getColumnCount(); col++) {
            TableColumn column = columnModel.getColumn(col);
            column.setMinWidth(0);
            column.setMaxWidth(Integer.MAX_VALUE);
            column.setPreferredWidth(0);
        }
    }

    // Methode zum Anzeigen einer Fehlermeldung, wenn keine Verbindung zum SQL Server hergestellt werden konnte
    private void handleNoSQLConnection(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        if (!updateDatabase.isRunning()) JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum SQL Server hergestellt werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
        if (updateDatabase.isRunning()) JOptionPane.showMessageDialog(null, "Verbindung zum SQL Server verloren, überprüfe deine Internetverbindung!", "Fehler", JOptionPane.ERROR_MESSAGE);
        updateDatabase.stop();
        new UI(width, height, title, icon, resizable, backgroundImage, Tickrate, soundCheckBox.isSelected(),args, points);
        dispose();
    }

    // Methode zum Schreiben der Daten in die Tabelle
    private void writeLeaderBoard(String username, int score) {
        Database.Table table = database.getTable("leaderboard");
        Database.Table.Column users = table.getColumn("users");
        Database.Table.Column highscores = table.getColumn("scores");

        ArrayList<String> usernames = new ArrayList<>(Arrays.asList(users.getValues()));
        ArrayList<String> scores = new ArrayList<>(Arrays.asList(highscores.getValues()));
        if (!users.contains(username)) users.addLine(username);

        if (usernames.contains(username)) {
            int index = usernames.indexOf(username);
            int oldScore = Integer.parseInt(scores.get(index));

            if (score >= oldScore) {
                scores.set(index, String.valueOf(score));
            } else {
                score = oldScore;
            }
        }

        highscores.set(users, username, String.valueOf(score));
    }

    // Innere Klasse für die Daten der Tabelle
    static class RowData {
        private final int index;
        private final String user;
        private final int score;

        // Konstruktor
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