import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class UI extends JFrame {
    // Objects
    private final Config config;
    private final Utils utils;
    // Attributes
    private final Timer updateDatabase;
    private final String host, port, tableName;
    private final int points;
    private Database database;
    private boolean newGame = true, isUploaded = true;

    // UI Elements
    private JButton bStart;
    private JPanel backgroundFrame, tablePanel;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private JSpinner spinnerFPS;
    private JScrollPane scrollPane;
    private boolean f3Pressed = false;

    // Constructor
    public UI(Config config, Utils utils) {
        Main.isRunning = false;

        this.config = config;
        this.utils = utils;
        this.points = config.getPoints();

        // Database Configuration
        JsonNode json = utils.readJson("Database");
        String databaseName, table, user, password;

        if (json != null) {
            host = json.get("host").asText();
            port = json.get("port").asText();
            databaseName = json.get("database").asText();
            table = json.get("table").asText();
            user = json.get("user").asText();
            password = json.get("password").asText();

            if (config.getArgs().length >= 2) table = json.get("reversedTable").asText();
        } else {
            host = "error";
            port = "0";
            databaseName = "error";
            table = "error";
            user = "error";
            password = "error";
        }

        tableName = table;

        // Initialize UI
        score.setText("Global Leaderboard");

        if (points >= 0) {
            isUploaded = false;
            newGame = false;
            playerName.setEnabled(true);
            score.setText("Your Score: " + points);
            bStart.setText("Confirm Score");
            bStart.setToolTipText("Upload your score");
        }

        spinnerFPS.setValue(config.getFPS());
        score.setVisible(true);
        playerName.setVisible(true);
        soundCheckBox.setSelected(config.isSound());

        // Timer for updating the leaderboard
        updateDatabase = new Timer(5000, e -> initLeaderBoard());

        // Initialize database connection and leaderboard
        if (utils.checkSQLConnection(host, port)) {
            database = new Database(host, port, databaseName, user, password);
            initLeaderBoard();
            updateDatabase.start();
        }

        // ActionListener for the Start button
        bStart.addActionListener(e -> {
            if (newGame) {

                // Start the game
                int fps = (int) Double.parseDouble(spinnerFPS.getValue().toString());
                if (fps < 1) fps = 1;
                if (fps > 360) fps = 360;

                config.setFPS(fps);
                config.setPoints(0);
                config.setSound(soundCheckBox.isSelected());

                Main.isRunning = true;

                JFrame frame = new JFrame(config.getTitle());
                GamePanel gamePanel = new GamePanel(frame, config);
                frame.add(gamePanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(config.getWindowSizeX(), config.getWindowSizeY());
                frame.setResizable(config.isResizable());
                frame.setLocationRelativeTo(null);
                frame.setLocation(utils.centerFrame(frame));
                frame.setIconImage(utils.readImage(config.getIcon()));
                frame.setVisible(true);
                updateDatabase.stop();
                dispose();

            } else if (this.points >= 0 && !isUploaded)
                bStart.setText("Play Again");
            bStart.setToolTipText("Play Again");
            isUploaded = true;
            newGame = true;

            // Check input
            if (points <= 0) return;
            if (!(!playerName.getText().isEmpty() && !playerName.getText().contains("Username"))) return;
            if (playerName.getText().length() <= 32) {
                if (!utils.checkUserName(playerName.getText()) && !playerName.getText().contains(" "))
                    writeLeaderBoard(playerName.getText(), points, tableName); // Upload the score
                else { // Error message for invalid username
                    new UI(config, utils);
                    updateDatabase.stop();
                    dispose();
                }
            } else { // Error message for too long username
                new UI(config, utils);
                updateDatabase.stop();
                dispose();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                // F3 + R key combination : Reverse Toggle
                if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = true;
                else if (f3Pressed && e.getKeyCode() == KeyEvent.VK_R) {

                    if (config.getArgs().length == 1) changeArgs(config.getArgs()[0], false);
                    else if (config.getArgs().length == 2) changeArgs(config.getArgs()[0], false);
                    else changeArgs("lena", true);

                    f3Pressed = false; // reset F3 status
                }

                // F3 + C key combination : Swap Assets Toggle
                if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = true;
                else if (f3Pressed && e.getKeyCode() == KeyEvent.VK_C) {


                    if (config.getArgs().length == 0) changeArgs("911", false);

                    else if (config.getArgs().length == 1) {

                        if (config.getArgs()[0].equals("lena")) changeArgs("911", false);
                        else if (config.getArgs()[0].equals("911")) changeArgs("lenabeta", false);
                        else if (config.getArgs()[0].equals("lenabeta")) changeArgs("911beta", false);
                        else if (config.getArgs()[0].equals("911beta")) changeArgs("alpha", false);
                        else if (config.getArgs()[0].equals("alpha")) changeArgs("lena", false);

                    } else if (config.getArgs().length > 1) {

                        if (config.getArgs()[0].equals("lena")) changeArgs("911", true);
                        else if (config.getArgs()[0].equals("911")) changeArgs("lenabeta", true);
                        else if (config.getArgs()[0].equals("lenabeta")) changeArgs("911beta", true);
                        else if (config.getArgs()[0].equals("911beta")) changeArgs("alpha", true);
                        else if (config.getArgs()[0].equals("alpha")) changeArgs("lena", true);

                    }

                    f3Pressed = false; // reset F3 status
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                // Reset the F3 status when the key is released
                if (e.getKeyCode() == KeyEvent.VK_F3) f3Pressed = false;
            }
        });
    }

    // Method to initialize UI components
    private void createUIComponents() {
        // Initialize JFrame
        setTitle(config.getTitle());
        setSize(config.getWindowSizeX(), config.getWindowSizeY());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(config.isResizable());
        setIconImage(utils.readImage(config.getIcon()));

        // Initialize JPanels with background image
        backgroundFrame = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(utils.readImage(config.getBackground()), 0, 0,
                        utils.getBackgroundWidth(config.getBackground()), getHeight(), this);
                repaint();
            }
        };
        // Add panel to Frame
        add(backgroundFrame);

        // Initialize window position
        setLocation(utils.centerFrame(this));
        setVisible(true);

        // Initialize Start button
        bStart = new JButton();
        bStart.setOpaque(false);
        bStart.setToolTipText("Start the game");

        // Initialize Sound Checkbox
        soundCheckBox = new JCheckBox();
        soundCheckBox.setOpaque(false);
        soundCheckBox.setToolTipText("Enable or disable sound");
        soundCheckBox.setBorder(BorderFactory.createEmptyBorder());
        soundCheckBox.setFont(new Font("Roboto", Font.PLAIN, 24));
        soundCheckBox.setForeground(utils.calculateForegroundColor(utils.getAverageColorInRectangle(utils.getBottomMenuBounds(backgroundFrame), backgroundFrame)));

        // Initialize Username Textfield
        playerName = new JTextField();
        playerName.setOpaque(false);
        playerName.setEnabled(false);
        playerName.setFont(new Font("Roboto", Font.PLAIN, 22));
        playerName.setForeground(utils.calculateForegroundColor(utils.getAverageColorInRectangle(utils.getBottomMenuBounds(backgroundFrame), backgroundFrame)));
        playerName.setToolTipText("Enter your username");
        playerName.setHorizontalAlignment(JTextField.CENTER);
        playerName.setBorder(BorderFactory.createEmptyBorder());
        utils.setPlaceholder(playerName, "Username", backgroundFrame);

        // Initialize ScrollPane for the table
        scrollPane = new JScrollPane();
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Initialize the table
        leaderBoard = new JTable();
        leaderBoard.setFont(new Font("Roboto", Font.PLAIN, 22));
        leaderBoard.setOpaque(false);

        // Add the table to the ScrollPane
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);
        scrollPane.add(leaderBoard);
        scrollPane.setOpaque(false);

        // Initialize the FPS Spinner
        spinnerFPS = new JSpinner();
        spinnerFPS.setOpaque(false);
        spinnerFPS.setToolTipText("Frames per second");

        // Prevent invalid input in the FPS Spinner
        JFormattedTextField txt = ((JSpinner.DefaultEditor) spinnerFPS.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

        // TODO Attention Trigger Warning!!!
        spinnerFPS.addChangeListener(e -> {
            if (spinnerFPS.getValue() != null) {
                if ((int) Double.parseDouble(spinnerFPS.getValue().toString()) >= 360) spinnerFPS.setValue(360);
                if ((int) Double.parseDouble(spinnerFPS.getValue().toString()) <= 1) spinnerFPS.setValue(1);
            }
        });
    }

    private void changeArgs(String profile, boolean reversed) {
        String javaCommand = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        String mainClass = Main.class.getName();


        ProcessBuilder processBuilder = new ProcessBuilder(javaCommand, "-cp", classpath, mainClass);

        processBuilder.command().add(profile);
        if (reversed) processBuilder.command().add("reversed");


        try {
            processBuilder.start();
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to update the leaderboard
    private void initLeaderBoard() {
        if (utils.checkSQLConnection(host, port)) {
            leaderBoard.setVisible(true);
            scrollPane.setVisible(true);
            tablePanel.setVisible(true);

            // Initialize the database
            Database.Table table = database.getTable(tableName);
            Database.Table.Column users = table.getColumn("users");
            Database.Table.Column highscores = table.getColumn("scores");
            leaderBoard.getTableHeader().setReorderingAllowed(false); // Disable column reordering

            // Fetch data from the SQL Server and populate the table
            String[] userValues = users.getValues();
            String[] scoreValues = highscores.getValues();

            // Create the table
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Cells are not editable
                }
            };

            // Add columns
            model.addColumn("Rank");
            model.addColumn("Username");
            model.addColumn("Highscore");

            // Insert data into the table
            if (userValues != null && scoreValues != null && userValues.length == scoreValues.length) {
                for (int i = 0; i < userValues.length; i++) {
                    model.addRow(new Object[]{(i + 1) + ".", userValues[i], scoreValues[i]});
                }
            }


            // Sort the table by score
            ArrayList<RowData> rowDataList = new ArrayList<>();

            // Copy data from the table to the ArrayList
            for (int i = 0; i < model.getRowCount(); i++) {
                String user = model.getValueAt(i, 1).toString();
                int score = Integer.parseInt(model.getValueAt(i, 2).toString());
                RowData rowData = new RowData(user, score);
                rowDataList.add(rowData);
            }

            // Sort the ArrayList by score
            rowDataList.sort(Comparator.comparingInt(RowData::getScore).reversed());

            // Insert updated data back into the table
            model.setRowCount(0);
            for (int i = 0; i < rowDataList.size(); i++) {
                RowData rowData = rowDataList.get(i);
                model.addRow(new Object[]{(i + 1) + ".", rowData.getUser(), rowData.getScore()});
            }


            // Configure the table
            leaderBoard.setModel(model);


            // Adjust row height
            int rowHeight = leaderBoard.getRowHeight();
            FontMetrics fontMetrics = leaderBoard.getFontMetrics(leaderBoard.getFont());
            Font boldFont = new Font("Roboto", Font.BOLD, 24);

            // Center align columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            leaderBoard.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Center align "User" column

            // Adjust row height
            for (int row = 0; row < leaderBoard.getRowCount(); row++) {
                int fontHeight = fontMetrics.getHeight() + 10;
                leaderBoard.setRowHeight(row, Math.max(rowHeight, fontHeight));
            }

            // Set font of column headers to bold
            leaderBoard.getTableHeader().setFont(boldFont);


            // Adjust width of the Rank column
            TableColumnModel columnModel = leaderBoard.getColumnModel();
            TableColumn rankColumn = columnModel.getColumn(0);
            rankColumn.setMaxWidth(800); // Set max width of the Rank column to 50
            rankColumn.setMinWidth(100); // Set min width of the Rank column to 50
            rankColumn.setPreferredWidth(50); // Set preferred width of the Rank column to 50

            // Adjust widths of remaining columns to make space for the Rank column
            for (int col = 1; col < columnModel.getColumnCount(); col++) {
                TableColumn column = columnModel.getColumn(col);
                column.setMinWidth(0);
                column.setMaxWidth(Integer.MAX_VALUE);
                column.setPreferredWidth(0);
            }

        } else { // If there is no connection to the database
            if (!updateDatabase.isRunning())
                JOptionPane.showMessageDialog(null, "Could not establish a connection to the SQL Server!", "Error", JOptionPane.ERROR_MESSAGE);
            if (updateDatabase.isRunning())
                JOptionPane.showMessageDialog(null, "Connection to the SQL Server lost, check your internet connection!", "Error", JOptionPane.ERROR_MESSAGE);
            updateDatabase.stop();
            new UI(config, utils);
            dispose();
        }
    }

    // Method to write data into the table
    private void writeLeaderBoard(String username, int score, String tableName) {
        // Read data from the table
        Database.Table table = database.getTable(tableName);
        Database.Table.Column users = table.getColumn("users");
        Database.Table.Column highscores = table.getColumn("scores");

        // Store data in ArrayLists
        ArrayList<String> usernames = new ArrayList<>(Arrays.asList(users.getValues()));
        ArrayList<String> scores = new ArrayList<>(Arrays.asList(highscores.getValues()));

        // If the username is not already present in the table, add it
        if (!users.containsLC(username)) users.addLine(username);

        // If the username is already present in the table, update the score
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
}

// Inner class for table data
class RowData {
    private final String user;
    private final int score;

    // Constructor
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