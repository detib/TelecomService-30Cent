package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConn {
    private static DatabaseConn instance;
    private static Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/";
    private final String databaseName = "Telecom_20Cent";
    private final String username = "root";
    private final String password = "";

    private DatabaseConn() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url + databaseName, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Connection with Database Failed: " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConn getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConn();
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConn();
        }
        return instance;
    }
}
