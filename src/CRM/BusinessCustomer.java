package CRM;

import java.sql.ResultSet;

public class BusinessCustomer extends Customer{

    @Override
    public boolean create() {
        return false;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public ResultSet findById() {
        return null;
    }

    @Override
    public ResultSet findAll() {
        return null;
    }
}
