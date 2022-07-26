package Database;

import java.sql.ResultSet;

public interface TelecomService {
    boolean create();
    boolean update();

    boolean delete();

    ResultSet findById();
    ResultSet findAll();
}
