package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Enum.STATE;
import CRM.Exceptions.ContactException;
import CRM.Exceptions.ContractException;
import CRM.Exceptions.CustumerException;
import CRM.Exceptions.SubscriptionException;
import CRM.Service.Service;
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
import java.util.Optional;
import java.util.Scanner;

public class CustomerManagement implements TelecomService<Customer> {

    @Getter
    private ArrayList<Customer> customers;

    public CustomerManagement() {
        this.customers = findAll();
    }

    @Override
    public boolean create(Customer object) throws CustumerException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "INSERT INTO customer VALUES('%s', '%s', '%s', '%s', '%s');"
                    , object.getId(), object.getCreatedDate(), object.getState(), object.getCustomerType(), object.getContact().getId()));
            try {
//                System.out.println("Creating contact");
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

    @Override
    public boolean update(Customer object) {
        customers.remove(object);
        try {
            Util.updateCustomer(new Scanner(System.in), object);
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "UPDATE customer SET state = '%s' WHERE CuId = '%s';"
                    , object.getState(), object.getId()));
            return customers.add(object);
        } catch (SQLException e) {
            customers.add(object);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Customer object) {
        customers = findAll();
        customers.remove(object);
        ArrayList<Contract> allContracts = object.findAll();
        allContracts.forEach(object::delete);
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format("DELETE FROM contact where CtID='%s'", object.getContact().getId()));
            conn.createStatement().execute(String.format("DELETE FROM customer where CuID='%s'", object.getId()));
        } catch (SQLException sqle) {
            throw new RuntimeException("Failed to delete on CustomerManagement: " + sqle.getMessage());
        }
        return false;
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