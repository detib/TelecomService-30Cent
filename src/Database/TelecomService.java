package Database;

import CRM.Exceptions.CustumerException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;

public interface TelecomService<T> {
    boolean create(T object) throws Exception, SubscriptionException;
    boolean update(T object);

    boolean delete(T object);

    Optional<T> findById(String id);
    ArrayList<T> findAll();
}
