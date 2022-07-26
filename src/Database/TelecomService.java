package Database;

import java.sql.ResultSet;

public interface TelecomService<T> {
    boolean create(T object);
    boolean update(T object);

    boolean delete(T object);

    ResultSet findById(String id);
    ResultSet findAll();
}
