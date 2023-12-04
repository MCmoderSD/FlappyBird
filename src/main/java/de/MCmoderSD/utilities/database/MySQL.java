package de.MCmoderSD.utilities.database;

import de.MCmoderSD.utilities.json.JsonNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class MySQL {

    // Constants
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String table;

    // Attributes
    private Connection connection;
    private HashMap<String, Integer> scores;

    // Constructors
    public MySQL(JsonNode config) {
        host = config.get("host").asText();
        port = config.get("port").asInt();
        database = config.get("database").asText();
        username = config.get("username").asText();
        password = config.get("password").asText();
        table = config.get("table").asText();
        connect();
    }

    // Control

    // Connect to MySQL
    public void connect() {
        try {
            if (isConnected()) return; // already connected
            connection = java.sql.DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password); // connect
            System.out.println("MySQL connected!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    // Disconnect from MySQL
    public void disconnect() {
        try {
            if (!isConnected()) return; // already disconnected
            connection.close(); // disconnect
            System.out.println("MySQL disconnected!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    // Add a new score to the HashMap
    public void addScore(String username, int score) {
        pullFromMySQL();
        scores.put(username, score);
        pushToMySQL();
    }

    // Get encoded data from MySQL
    public HashMap<String, Integer> pullFromMySQL() {
        try {
            if (!isConnected()) return scores; // not connected

            // Select all records from the table
            String selectQuery = "SELECT * FROM " + table;
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            scores = new HashMap<>();

            // Populate the local HashMap with the data from the database
            while (resultSet.next()) {
                String username = resultSet.getString("usernames");
                int score = resultSet.getInt("scores");
                scores.put(username, score);
            }

            resultSet.close();
            selectStatement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return scores;
    }

    // Insert data into MySQL
    public void pushToMySQL() {
        try {
            if (!isConnected()) return; // not connected

            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                String username = entry.getKey();
                int score = entry.getValue();

                // Check if the username already exists in the database
                String selectQuery = "SELECT * FROM " + table + " WHERE usernames = ?";
                PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                selectStatement.setString(1, username);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    // If the username exists, update the score
                    String updateQuery = "UPDATE " + table + " SET scores = ? WHERE usernames = ?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setInt(1, score);
                    updateStatement.setString(2, username);
                    updateStatement.executeUpdate();
                    updateStatement.close();
                } else {
                    // If the username does not exist, insert a new record
                    String insertQuery = "INSERT INTO " + table + " (usernames, scores) VALUES (?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, username);
                    insertStatement.setInt(2, score);
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }

                resultSet.close();
                selectStatement.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    // Getter
    public boolean isConnected() {
        return connection != null;
    }

    // Getter Constants
    public Connection getConnection() {
        return connection;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}