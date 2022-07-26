package Database;

import CRM.Exceptions.CustumerException;

import java.sql.ResultSet;

public interface TelecomService<T> {
    boolean create(T object) throws Exception;
    boolean update(T object);

    boolean delete(T object);

    T findById(String id);
    ResultSet findAll();
}
