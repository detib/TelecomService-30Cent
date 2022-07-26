package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Enum.STATE;
import CRM.Exceptions.CustumerException;
import Database.ContactService;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.Util;
import lombok.Getter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class CustomerManagement implements TelecomService<Customer>, ContactService {

    @Getter
    private ArrayList<Customer> customers;

    public CustomerManagement() {
        this.customers = new ArrayList<>();
        ResultSet allCustomers = findAll();
        try {
            while (allCustomers.next()) {
                String id = allCustomers.getString("CuId");
                LocalDate date = LocalDate.parse(allCustomers.getString("createdDate"));
                STATE state = STATE.valueOf(allCustomers.getString("state"));
                CustomerType customerType = CustomerType.valueOf(allCustomers.getString("customerType"));
                Contact contact = findContact(allCustomers.getString("contact"));
                Customer customer = new Customer(id, date, state, customerType, contact);
                this.customers.add(customer);
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    @Override
    public boolean create(Customer object) throws CustumerException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            boolean success = conn.createStatement().execute(String.format(
                    "INSERT INTO customer VALUES('%s', '%s', '%s', '%s', '%s');"
                    ,object.getId(), object.getCreatedDate(), object.getState(), object.getCustomerType(), object.getContact().getId()));
            if(success) return customers.add(object); else return false;
        } catch (SQLException e) {
            throw new CustumerException("Cannot add a customer to the database!");
        }
    }

    @Override
    public boolean update(Customer object) {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            boolean success = conn.createStatement().execute(String.format(
                    "UPDATE customer SET state = '%s' WHERE CuId = '%s';"
                    ,object.getState(), object.getId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean delete(Customer object) {
        return false;
    }

    @Override
    public Customer findById(String id) {
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    @Override
    public ResultSet findAll() {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            return conn.createStatement().executeQuery("SELECT * FROM CUSTOMER");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Contact findContact(String id) {
        return null;
    }

}
