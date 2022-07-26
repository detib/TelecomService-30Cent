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

        ResultSet resultSet = con.createStatement().executeQuery("Select * from studenti");



        while (resultSet.next()) {
            System.out.printf("Name: %s, Surname: %s, Qyteti: %s%n", resultSet.getString("emri"), resultSet.getString("mbiemri"), resultSet.getString("qyteti"));
        }

    }
}
