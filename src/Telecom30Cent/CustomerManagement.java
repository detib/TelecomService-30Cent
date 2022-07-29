package Telecom30Cent;

import Telecom30Cent.Enum.CustomerType;
import Telecom30Cent.Enum.STATE;
import Telecom30Cent.Exceptions.ContactException;
import Telecom30Cent.Exceptions.CustumerException;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.Util;
import lombok.Getter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class CustomerManagement implements TelecomService<Customer> {

    @Getter
    private ArrayList<Customer> customers;

    public CustomerManagement() {
        this.customers = findAll();
    }

    /**
     * create a new customer and add it to the database
     * @param object the customer to be created
     * @return true if the customer was created successfully, false otherwise
     * @throws CustumerException if the customer already exists
     */
    @Override
    public boolean create(Customer object) throws CustumerException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "INSERT INTO customer VALUES('%s', '%s', '%s', '%s', '%s');"
                    , object.getId(), object.getCreatedDate(), object.getState(), object.getCustomerType(), object.getContact().getId()));
            try {
                object.createContact();
            } catch (ContactException e) {
                System.out.println(e.getMessage());
                return false;
            }
            return customers.add(object);
        } catch (SQLException e) {
            throw new CustumerException("Cannot add a customer to the database!");
        }
    }

    /**
     * update a customer in the database
     * @param object the customer to be updated
     * @return true if the customer was updated
     */
    @Override
    public boolean update(Customer object) {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "UPDATE customer SET state = '%s' WHERE CuId = '%s';"
                    , object.getState(), object.getId()));
            this.customers = findAll();
            return true;
        } catch (SQLException e) {
            customers.add(object);
            throw new RuntimeException(e);
        }
    }

    /**
     * delete a customer from the database
     * @param object the customer to be deleted
     * @return true if the customer was deleted successfully, false otherwise
     */
    @Override
    public boolean delete(Customer object) {
        customers.remove(object);
        ArrayList<Contract> allContracts = object.findAll();
        allContracts.forEach(object::delete);
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format("DELETE FROM contact where CtID='%s'", object.getContact().getId()));
            conn.createStatement().execute(String.format("DELETE FROM customer where CuID='%s'", object.getId()));
            return true;
        } catch (SQLException sqle) {
            customers.add(object);
            throw new RuntimeException("Failed to delete on CustomerManagement: " + sqle.getMessage());
        }
    }

    @Override
    public Optional<Customer> findById(String id) {
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Customer> findAll() {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            ResultSet resultSetCustomers = conn.createStatement().executeQuery("SELECT * FROM CUSTOMER");
            ArrayList<Customer> allCustomers = new ArrayList<>();
            while (resultSetCustomers.next()) {
                String id = resultSetCustomers.getString("CuId");
                LocalDate date = LocalDate.parse(resultSetCustomers.getString("createdDate"));
                STATE state = STATE.valueOf(resultSetCustomers.getString("state"));
                CustomerType customerType = CustomerType.valueOf(resultSetCustomers.getString("customerType"));
                Optional<Contact> optionalContact = Util.findContactById(resultSetCustomers.getString("contact"));
                Contact contact;
                if (optionalContact.isPresent()) {
                    contact = optionalContact.get();
                    Customer customer = new Customer(id, date, state, customerType, contact);
                    allCustomers.add(customer);
                }
            }
            return allCustomers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ContactException e) {
            System.out.println("Customer Management: Could not find contact: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}