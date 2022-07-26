package Database;


import CRM.Contact;

import java.sql.ResultSet;

public interface ContactService {
    boolean createContact(Contact object);

    ResultSet findContact(String id);
}

