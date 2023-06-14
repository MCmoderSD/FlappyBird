import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**

 Version: 0.1.7 (Beta)

 Author: Rebix

 This is a simple MySQL Database API for Java.

 This API is not finished yet, so it is not recommended to use it in production.
 */
@SuppressWarnings("ALL")
public class Database {
    public static String NOTING_FOUND = "N/A";
    public static String ERROR = "ERROR";
    private String host = "localhost";
    private String port = "3306";
    private String database = "test";
    private String username = "root";
    private String password = "root";

    private Connection connection;

    /**
     Constructor for the Database class.
     Initializes the database connection.
     */
    public Database() {
        connect();
    }

    /**
     Constructor for the Database class.
     Initializes the database connection with the provided parameters.
     @param host the host address of the database server
     @param port the port number of the database server
     @param database the name of the database
     @param username the username for connecting to the database
     @param password the password for connecting to the database
     */
    public Database(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        connect();
    }

    /**
     Establishes a connection to the database.
     */
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

    /**
     Checks if a connection to the database is established.
     @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     Retrieves the database connection.
     If not connected, establishes a connection before returning.
     @return the Connection object representing the database connection
     */
    public Connection getConnection() {
        if (!isConnected()) {
            connect();
        }
        return connection;
    }

    /**
     Retrieves a table from the database.
     @param name the name of the table
     @return the Table object representing the specified table
     */
    public Table getTable(String name) {
        return new Table(name, this);
    }

    /**
     Class representing operations on a database table.
     */
    static class Table {
        private final String name;
        private final Database database;

        /**
         Constructor for the Table class.
         @param name the name of the table
         @param database the Database object representing the parent database
         */
        public Table(String name, Database database) {
            this.name = name;
            this.database = database;
        }

        /**
         Retrieves the parent Database object.
         @return the parent Database object
         */
        public Database getDatabase() {
            return database;
        }

        /**
         Retrieves the name of the table.
         @return the name of the table
         */
        public String getName() {
            return name;
        }
        @Override
        public String toString() {
            return getName();
        }

        /**
         Retrieves a column from the table.
         @param name the name of the column
         @return the Column object representing the specified column
         */
        public Column getColumn(String name) {
            return new Column(name, this);
        }

        /**
         Class representing operations on a table column.
         */
        static class Column {
            private final String name;
            private final Table table;

            /**
             Constructor for the Column class.
             @param name the name of the column
             @param table the Table object representing the parent table
             */
            public Column(String name, Table table) {
                this.name = name;
                this.table = table;
            }

            /**

             Checks if a specific string is present in the column.
             @param name the string to check
             @return true if the string is present, false otherwise
             */
            public boolean contains(String name) {
                for (String value : getValues()) {
                    if (value.equals(name)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             Retrieves the value of a specific row and column.
             @param column the Column object representing the column
             @param key the key identifying the row
             @return the value of the specified row and column, or NOTING_FOUND if not found, or ERROR in case of an exception
             */
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

            /**
             Retrieves all values in the column.
             @return an array of strings containing all the values in the column
             */
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

            /**

             Sets the value of a specific row and column.

             @param column the Column object representing the column

             @param key the key identifying the row

             @param value the new value to set

             @return true if the update was successful, false otherwise
             */
            public boolean set(Column column, String key, String value) {
                try (Connection conn = getTable().getDatabase().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE " + getTable() + " SET " + this + " = ? WHERE " + column + " = ?"
                )) {
                    stmt.setString(1, value);
                    stmt.setString(2, key);

                    return stmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }

            /**
             Adds a new row to the column.
             @param value the value to add
             @return true if the addition was successful, false otherwise
             */
            public boolean addLine(String value) {
                try (Connection conn = getTable().getDatabase().getConnection(); PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO " + table + "(" + this + ") select '" + value + "'"
                )) {
                    return stmt.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return false;
            }

            /**
             Retrieves the name of the column.
             @return the name of the column
             */
            public String getName() {
                return name;
            }

            /**
             Retrieves the parent Table object.
             @return the parent Table object
             */
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
