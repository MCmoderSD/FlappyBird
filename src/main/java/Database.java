import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**

 Version: 0.1.7 (Beta)

 Author: Rebix

 This is a simple MySQL Database API for Java.

 This API is not finished yet, so it is not recommended to use it in production.
 */
public class Database {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
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
            logger.error(e.getMessage());
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

    @SuppressWarnings("ClassEscapesDefinedScope")
    public Table getTable(String name) {
        return new Table(name, this);
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

        public Column getColumn(String name) {
            return new Column(name, this);
        }

        @SuppressWarnings("SqlSourceToSinkFlow")
        static class Column {
            private final String name;
            private final Table table;

            public Column(String name, Table table) {
                this.name = name;
                this.table = table;
            }

            public boolean contains(String name) {
                for (String value : getValues()) {
                    if (value.equals(name)) {
                        return true;
                    }
                }
                return false;
            }

            public boolean containsLC(String name) {
                for (String value : getValues()) {
                    if (value.equalsIgnoreCase(name)) {
                        return true;
                    }
                }
                return false;
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
                    logger.error(e.getMessage());
                    return Database.ERROR;
                }
            }

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
                    logger.error(e.getMessage());
                }
                return null;
            }

            public boolean set(Column column, String key, String value) {
                try (Connection conn = getTable().getDatabase().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE " + getTable() + " SET " + this + " = ? WHERE " + column + " = ?"
                )) {
                    stmt.setString(1, value);
                    stmt.setString(2, key);

                    return stmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
                return false;
            }

            public boolean addLine(String value) {
                try (Connection conn = getTable().getDatabase().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO " + table + "(" + this + ") select '" + value + "'"
                )) {
                    return stmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    logger.error(e.getMessage());
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
