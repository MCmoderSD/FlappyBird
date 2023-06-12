import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Version: 0.1 (Beta)
 * Author:  Rebix
 * This is a simple MySQL Database API for Java.
 * This API is not finished yet, so it is not recommended to use it in production.
 */

public class Database {
    public static String NOTING_FOUND = "N/A";
    public static String ERROR = "ERROR";
    private String host = "localhost";
    private String port = "3306";
    private String database = "test";
    private String username = "root";
    private String password = "root";

    private Connection connection;

    public Database() {
        connect();
    }

    public Database(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        connect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
            connection.setAutoCommit(true);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("MySQL disconnected!");
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        if (!isConnected()) {
            connect();
        }
        return connection;
    }

    static class Table {
        private final String name;
        private final Database database;

        public Table(String name, Database database) {
            this.name = name;
            this.database = database;
        }

        public Database getDatabase() {
            return database;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }

        @SuppressWarnings("unused")
        static class Column {
            private final String name;
            private final Table table;

            public Column(String name, Table table) {
                this.name = name;
                this.table = table;
            }

            public String get(Column column, String key) {
                try (Connection conn = getTable().getDatabase().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "SELECT " + this + " FROM " + getTable() + " WHERE " + column + " = ?;"
                )) {
                    stmt.setString(1, key);
                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getString(getName());
                    }
                    stmt.close();
                    return Database.NOTING_FOUND;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return Database.ERROR;
                }
            }

            @SuppressWarnings("UnusedReturnValue")
            public String[] getValues() {
                try (Connection conn = getTable().getDatabase().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "SELECT " + this + " FROM " + getTable() + ";"
                )) {
                    ResultSet resultSet = stmt.executeQuery();
                    List<String> result = new ArrayList<>();
                    int i = 0;
                    while (resultSet.next()) {
                        result.add(resultSet.getString(getName()));
                        i++;
                    }
                    return result.toArray(new String[i]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public boolean set(Column column, String key, String value) {
                try (Connection conn = getTable().getDatabase().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE " + getTable() + " SET " + this + " = ? WHERE " + column + " = ?"
                )) {
                    stmt.setString(1, value);
                    stmt.setString(2, key);
//                    System.out.println(key + " was set to " + value + " in " + getTable());
                    return stmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }

            public String getName() {
                return name;
            }


            public Table getTable() {
                return table;
            }

            @Override
            public String toString() {
                return getName();
            }
        }
    }
}