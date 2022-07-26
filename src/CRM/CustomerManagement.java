package CRM;

import CRM.Contact.Contact;
import Database.TelecomService;

import java.sql.ResultSet;

public class CustomerManagement implements TelecomService<Customer> {

    @Override
    public boolean create(Customer object) {

        return false;
    }

    @Override
    public boolean update(Customer object) {
        return false;
    }

    @Override
    public boolean delete(Customer object) {
        return false;
    }

    @Override
    public ResultSet findById(String id) {
        return null;
    }

    @Override
    public ResultSet findAll() {
        return null;
    }
}
