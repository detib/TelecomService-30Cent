package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @deprecated remove
 */

public class JDBC {
    public static void main(String[] args) throws SQLException {

        Connection con =  DatabaseConn.getInstance().getConnection();

        ResultSet resultSet = con.createStatement().executeQuery("Show tables;");

        while (resultSet.next()) {
            System.out.printf("%s\n", resultSet.getString("Tables_in_Telecom_20Cent"));
        }
    }
}
