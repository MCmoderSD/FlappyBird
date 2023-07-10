import com.fasterxml.jackson.databind.JsonNode;

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
import java.util.Objects;

public class UI extends JFrame {
    private final String backgroundImage;
    private final int points;
    private final Timer updateDatabase;
    private final String host;
    private final String port;
    private final int width, height;
    private final boolean resizable;
    private final String title;
    private final String icon;
    private final Utils utils;
    private final Movement movement;
    private Database database;
    private JButton bStart;
    private JPanel backgroundFrame, tablePanel;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private JSpinner spinnerTPS;
    private JScrollPane scrollPane;
    private double TPS;
    private boolean newGame = true, isUploaded = true;

    // Konstruktor und UI initialisieren
    public UI(Utils utils, Movement movement, int width, int height, String title, String icon, boolean resizable, String backgroundImage, int JumpHeight, double Tickrate, boolean sound , String[] args, int points, Config config) {
        this.utils = utils;
        this.movement = movement;
        this.width = width;
        this.height= height;
        this.title = title;
        this.icon = icon;
        this.resizable = resizable;
        this.backgroundImage = backgroundImage;
        this.TPS = Tickrate;
        this.points = points;


        JsonNode json = utils.readJson("Database");
        host = json.get("host").asText();
        port = json.get("port").asText();
        String databaseName = json.get("database").asText();
        String table = json.get("table").asText();
        String user = json.get("user").asText();
        String password = json.get("password").asText();

        if (args.length >= 2) table = json.get("reversedTable").asText();

        if (Tickrate <= TPS) TPS = Tickrate;

        movement.backgroundResetX = 0;

        score.setText("Global Leaderboard");

        if (Objects.equals(backgroundImage, "911/Skyline.png")) playerName.setForeground(Color.WHITE);
        else playerName.setForeground(Color.BLACK);

        if (points >= 0) {
            isUploaded = false;
            newGame = false;
            playerName.setEnabled(true);
            score.setText("Dein Score: " + points);
            bStart.setText("Score Bestätigen");
            bStart.setToolTipText("Lade deinen Score hoch");
        }

        TPS = TPS + (1 + Tickrate - utils.calculateOSspecifcTickrate(Tickrate));
        if (TPS > 100) TPS = 100;
        spinnerTPS.setValue((int) TPS);
        score.setVisible(true);
        playerName.setVisible(true);
        soundCheckBox.setSelected(sound);

        // Timer für die Aktualisierung der Bestenliste
        String finalTable = table;
        updateDatabase = new Timer(5000, e -> initLeaderBoard(utils, movement, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, args, points, config, finalTable));

        // Initialisierung der Datenbankverbindung und der Bestenliste
        if (utils.checkSQLConnection(host, port)) {
            database = new Database(host, port, databaseName, user, password);
            initLeaderBoard(utils, movement, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, args, points, config, table);
            updateDatabase.start();
        }

        // ActionListener für den Start-Button
        bStart.addActionListener(e -> {
            if (newGame) {
                int spinnerValue = (int) spinnerTPS.getValue();
                if (spinnerValue <= 100 && spinnerValue > 0) TPS = spinnerValue;
                play(JumpHeight, TPS, args, config);
            } else if (this.points >= 0 && !isUploaded) {
                upload(utils, movement, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, args, points, config);
            }
        });
    }

    // Methode zum Starten des Spiels
    private void play(int JumpHeight, double Tickrate, String[] args, Config config) {
        config.run(utils, movement, JumpHeight, utils.calculateOSspecifcTickrate(Tickrate), soundCheckBox.isSelected(), args);
        updateDatabase.stop();
        dispose();
    }

    // Methode zum Hochladen des Scores
    private void upload(Utils utils, Movement movement, int width, int height, String title, String icon, boolean resizable, String backgroundImage, int JumpHeight, double Tickrate, String[] args, int points, Config config) {
        bStart.setText("Nochmal Spielen");
        bStart.setToolTipText("Nochmal Spielen");
        isUploaded = true;
        newGame = true;

        // Überprüfung der Eingabe
        if (!Logic.instance.developerMode && !Logic.instance.cheatsEnabled && points > 0) {
            if (playerName.getText().length() != 0 && !playerName.getText().contains("Username")) {
                if (playerName.getText().length() <= 32) {
                    if (!utils.checkUserName(playerName.getText()) && !playerName.getText().contains(" ")) {
                        writeLeaderBoard(playerName.getText(), points); // Hochladen des Scores
                    } else { // Fehlermeldung bei unerlaubtem Username
                        // JOptionPane.showMessageDialog(null, "Der Username ist nicht erlaubt!", "Fehler", JOptionPane.ERROR_MESSAGE);
                        new UI(utils, movement, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, soundCheckBox.isSelected(), args, points, config);
                        updateDatabase.stop();
                        dispose();
                    }
                } else { // Fehlermeldung bei zu langem Username
                    // JOptionPane.showMessageDialog(null, "Der Username ist zu lang!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    new UI(utils, movement, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, soundCheckBox.isSelected(), args, points, config);
                    updateDatabase.stop();
                    dispose();
                }
            }
        }
    }

    // Methode zum Initialisieren der Fensterelemente
    private void createUIComponents() {
        // Initialisierung des JFrames
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(resizable);
        setIconImage(utils.reader(icon));

        // Initialisierung JPanels mit Hintergrundbild
        backgroundFrame = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(utils.reader(backgroundImage), movement.backgroundResetX, 0, utils.getBackgroundWidth(backgroundImage), getHeight(), this);
                repaint();
            }
        };
        // Add panel to Frame
        add(backgroundFrame);

        // Initialisierung der Fensterposition
        setLocation(utils.centerFrame(this));
        setVisible(true);

        // Initialisierung des Start-Buttons
        bStart = new JButton();
        bStart.setOpaque(false);
        bStart.setToolTipText("Starte das Spiel");

        // Initialisierung des Sound-Checkbox
        soundCheckBox = new JCheckBox();
        soundCheckBox.setOpaque(false);
        soundCheckBox.setToolTipText("Aktiviere oder deaktiviere den Sound");
        soundCheckBox.setBorder(BorderFactory.createEmptyBorder());
        soundCheckBox.setFont(new Font("Roboto", Font.PLAIN, 24));
        if (Objects.equals(backgroundImage, "911/Skyline.png")) soundCheckBox.setForeground(Color.WHITE);
        else soundCheckBox.setForeground(Color.BLACK);

        // Initialisierung des Username-Textfeldes
        playerName = new JTextField();
        playerName.setOpaque(false);
        playerName.setEnabled(false);
        playerName.setFont(new Font("Roboto", Font.PLAIN, 22));
        if (Objects.equals(backgroundImage, "911/Skyline.png")) playerName.setForeground(Color.WHITE);
        else playerName.setForeground(Color.BLACK);
        playerName.setToolTipText("Gib deinen Username ein");
        playerName.setHorizontalAlignment(JTextField.CENTER);
        playerName.setBorder(BorderFactory.createEmptyBorder());
        utils.setPlaceholder(playerName, "Username");

        // Initialisierung der ScrollPane für die Tabelle
        scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Initialisierung der Tabelle
        leaderBoard = new JTable();
        leaderBoard.setFont(new Font("Roboto", Font.PLAIN, 22));
        leaderBoard.setOpaque(false);

        // Hinzufügen der Tabelle zur ScrollPane
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);
        scrollPane.add(leaderBoard);
        scrollPane.setOpaque(false);

        // Initialisierung des TPS-Spinners
        spinnerTPS = new JSpinner();
        spinnerTPS.setOpaque(false);
        spinnerTPS.setToolTipText("Ticks pro Sekunde");

        // Verhindert ungültige Eingaben im TPS-Spinner
        JFormattedTextField txt = ((JSpinner.DefaultEditor) spinnerTPS.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        spinnerTPS.addChangeListener(e -> {
            if (spinnerTPS.getValue() != null) {
                if ((int) spinnerTPS.getValue() >= 100)
                    spinnerTPS.setValue(100);
                if ((int) spinnerTPS.getValue() <= 1)
                    spinnerTPS.setValue(1);
            }
        });
    }

    // Methode zum Aktualisieren des Leaderboards
    private void initLeaderBoard(Utils utils, Movement movement, int width, int height, String title, String icon, boolean resizable, String backgroundImage, int JumpHeight, double Tickrate, String[] args, int points, Config config, String tableName) {
        if (utils.checkSQLConnection(host, port)) {
            leaderBoard.setVisible(true);
            scrollPane.setVisible(true);
            tablePanel.setVisible(true);

            // Initialisierung der Datenbank
            Database.Table table = database.getTable(tableName);
            Database.Table.Column users = table.getColumn("users");
            Database.Table.Column highscores = table.getColumn("scores");
            leaderBoard.getTableHeader().setReorderingAllowed(false); // Spaltenverschiebung deaktivieren

            // Daten vom SQL Server holen und in die Tabelle einfügen
            String[] userValues = users.getValues();
            String[] scoreValues = highscores.getValues();

            // Tabelle erstellen
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Die Zellen sind nicht editierbar
                }
            };

            // Spalten hinzufügen
            model.addColumn("Rank");
            model.addColumn("Username");
            model.addColumn("Highscore");

            // Daten in die Tabelle einfügen
            if (userValues != null && scoreValues != null && userValues.length == scoreValues.length) {
                for (int i = 0; i < userValues.length; i++) {
                    model.addRow(new Object[]{(i + 1) + ".", userValues[i], scoreValues[i]});
                }
            }

            // Tabelle nach der Punktzahl sortieren
            sortTableByScore(model);

            // Tabelle configuieren
            leaderBoard.setModel(model);
            adjustRowHeight(leaderBoard);
            adjustColumnWidths(leaderBoard);
        } else { // Wenn keine Verbindung zur Datenbank besteht
            handleNoSQLConnection(utils, movement, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, args, points, config);
        }
    }

    // Methode zum Sortieren der Tabelle nach der Punktzahl
    private void sortTableByScore(DefaultTableModel model) {
        ArrayList<RowData> rowDataList = new ArrayList<>();

        // Daten aus der Tabelle in die ArrayList kopieren
        for (int i = 0; i < model.getRowCount(); i++) {
            String user = model.getValueAt(i, 1).toString();
            int score = Integer.parseInt(model.getValueAt(i, 2).toString());
            RowData rowData = new RowData(user, score);
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

        // Spalten zentriert ausrichten
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Spalte "User" zentriert ausrichten

        // Zeilenhöhe anpassen
        for (int row = 0; row < table.getRowCount(); row++) {
            int fontHeight = fontMetrics.getHeight() + 10;
            table.setRowHeight(row, Math.max(rowHeight, fontHeight));
        }

        // Schriftart der Spaltenüberschriften Fett setzen
        table.getTableHeader().setFont(boldFont);
    }

    // Methode zum Anpassen der Spaltenbreiten
    private void adjustColumnWidths(JTable table) {

        // Breite der Rang-Zeile anpassen
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
    private void handleNoSQLConnection(Utils utils, Movement movement, int width, int height, String title, String icon, boolean resizable, String backgroundImage, int JumpHeight, double Tickrate, String[] args, int points, Config config) {
        if (!updateDatabase.isRunning()) JOptionPane.showMessageDialog(null, "Es konnte keine Verbindung zum SQL Server hergestellt werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
        if (updateDatabase.isRunning()) JOptionPane.showMessageDialog(null, "Verbindung zum SQL Server verloren, überprüfe deine Internetverbindung!", "Fehler", JOptionPane.ERROR_MESSAGE);
        updateDatabase.stop();
        new UI(utils, movement, width, height, title, icon, resizable, backgroundImage, JumpHeight, Tickrate, soundCheckBox.isSelected(),args, points, config);
        dispose();
    }

    // Methode zum Schreiben der Daten in die Tabelle
    private void writeLeaderBoard(String username, int score) {

        // Daten aus der Tabelle auslesen
        Database.Table table = database.getTable("leaderboard");
        Database.Table.Column users = table.getColumn("users");
        Database.Table.Column highscores = table.getColumn("scores");

        // Daten in ArrayLists speichern
        ArrayList<String> usernames = new ArrayList<>(Arrays.asList(users.getValues()));
        ArrayList<String> scores = new ArrayList<>(Arrays.asList(highscores.getValues()));

        // Wenn der Benutzername noch nicht in der Tabelle vorhanden ist, wird er hinzugefügt
        if (!users.containsLC(username)) users.addLine(username);

        // Wenn der Benutzername bereits in der Tabelle vorhanden ist, wird die Punktzahl aktualisiert
        if (usernames.contains(username)) {
            int index = usernames.indexOf(username);
            int oldScore = Integer.parseInt(scores.get(index));

            if (score >= oldScore) {
                scores.set(index, String.valueOf(score));
            } else {
                score = oldScore;
            }
        }

        // Daten in die Tabelle schreiben
        highscores.set(users, username, String.valueOf(score));
    }

    // Innere Klasse für die Daten der Tabelle
    static class RowData {
        private final String user;
        private final int score;

        // Konstruktor
        public RowData(String user, int score) {
            this.user = user;
            this.score = score;
        }
        public String getUser() {
            return user;
        }
        public int getScore() {
            return score;
        }
    }
}