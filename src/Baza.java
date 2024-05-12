import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Baza {

    private static Baza instance = null;

    private Connection connection;
    private String url = "jdbc:postgresql://abul.db.elephantsql.com/itlhvtsi";
    private String username = "itlhvtsi";
    private String password = "Fd8-T0J4xPqsL-5N6z_xlPIOAcPW6WCO";

    // Private constructor to prevent instantiation from outside
    private Baza() throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    // Method to get the singleton instance
    public static synchronized Baza getInstance() throws SQLException {
        if (instance == null) {
            instance = new Baza();
        }
        return instance;
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
