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
import java.util.Optional;
import java.util.Scanner;

public class CustomerManagement implements TelecomService<Customer>, ContactService {

    @Getter
    private ArrayList<Customer> customers;

    public CustomerManagement() {
        this.customers = findAll();
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
        customers.remove(object);
        try {
            Util.updateCustomer(new Scanner(System.in), object);
            Connection conn = DatabaseConn.getInstance().getConnection();
            return conn.createStatement().execute(String.format(
                    "UPDATE customer SET state = '%s' WHERE CuId = '%s';"
                    ,object.getState(), object.getId()));
        } catch (SQLException e) {
            customers.add(object);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Customer object) {
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
                Contact contact = findContact(resultSetCustomers.getString("contact"));
                Customer customer = new Customer(id, date, state, customerType, contact);
                allCustomers.add(customer);
            }
            return allCustomers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Contact findContact(String id) {
        return null;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CustomerManagement cm = new CustomerManagement();
        cm.customers.forEach(System.out::println);
        System.out.print("Update Customer: ");
//        Optional<Customer> cus_1000008 = cm.findById("CUS_1000008");
        try {
            cm.create(Util.createCustomer(sc));
        } catch (CustumerException e) {
            System.out.printf("Cannot create a new customer: %s" , e.getMessage());
        }
//        Optional<Customer> cust;
//        if((cust = cm.findById(sc.nextLine())).isPresent()) {
//            cm.update(cust.get());
//        } else {
//            System.out.println("Account does not exist!");
//        }
    }
}
