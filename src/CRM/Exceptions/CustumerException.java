package CRM.Exceptions;

import java.sql.SQLException;

public class CustumerException extends Exception {
    public CustumerException(String message) {
        super(message);
    }
}
