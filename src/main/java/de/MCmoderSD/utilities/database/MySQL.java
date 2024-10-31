package de.MCmoderSD.utilities.database;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MySQL extends Driver {

    // Constants
    private final String table;

    // Constructors
    public MySQL(JsonNode config, boolean isReverse) {
        super(config);
        table = isReverse ? config.get("reversedTable").asText() : config.get("table").asText();
        connect();
    }

    // Get encoded data from MySQL
    public HashMap<String, Integer> pullFromMySQL() {


        try {
            if (!isConnected()) connect();

            // Variables
            LinkedHashMap<String, Integer> scores = new LinkedHashMap<>();

            // Select all records from the table
            String selectQuery = "SELECT usernames, scores FROM " + table + " ORDER BY scores DESC";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            // Populate the local HashMap with the data from the database
            while (resultSet.next()) scores.put(resultSet.getString("usernames"), resultSet.getInt("scores"));

            // Close the resources
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
            if (!isConnected()) connect();

            // Prepare an SQL INSERT statement
            String insertQuery = "INSERT INTO " + table + " (usernames, scores) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, username);
            insertStatement.setInt(2, score);
            insertStatement.executeUpdate();

            // Close the resources
            insertStatement.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}