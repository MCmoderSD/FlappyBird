package de.MCmoderSD.utilities.database;

import de.MCmoderSD.utilities.json.JsonNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

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

    // Constructors
    public MySQL(JsonNode config, boolean isReverse) {
        host = config.get("host").asText();
        port = config.get("port").asInt();
        database = config.get("database").asText();
        username = config.get("username").asText();
        password = config.get("password").asText();
        table = isReverse ? config.get("reversedTable").asText() : config.get("table").asText();
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

    // Get encoded data from MySQL
    public HashMap<String, Integer> pullFromMySQL() {
        HashMap<String, Integer> scores;
        try {
            if (!isConnected()) return null; // not connected

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

            return scores;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    // Insert data into MySQL
    public void pushToMySQL(String username, int score) {
        try {
            if (!isConnected()) return; // not connected

            // Prepare an SQL INSERT statement
            String insertQuery = "INSERT INTO " + table + " (usernames, scores) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            // Set the parameters of the prepared statement
            insertStatement.setString(1, username);
            insertStatement.setInt(2, score);

            // Execute the prepared statement
            insertStatement.executeUpdate();

            insertStatement.close();
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