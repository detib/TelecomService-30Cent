package CRM;

import CRM.Enum.CustomerType;
import Database.TelecomService;

import java.sql.ResultSet;
import java.util.ArrayList;

public class CustomerManagement implements TelecomService<Customer> {

    private ArrayList<Customer> customers;

    public CustomerManagement() {
        this.customers = new ArrayList<>();
    }

    @Override
    public boolean create(Customer object) {


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
    public Customer findById(String id) {
//        customers.stream()
//                .filter(c -> c.getId().equals(id))
//                .findFirst()
//                .ifPresent(c -> {
//                    return c;
//                });
        return null;
    }

    @Override
    public ResultSet findAll() {
        return null;
    }

}
