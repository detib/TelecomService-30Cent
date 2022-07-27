package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Enum.STATE;
import CRM.Exceptions.ContactException;
import CRM.Exceptions.ContractException;
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
                    ,object.getId(), object.getCreatedDate(), object.getState(), object.getCustomerType(), object.getContact().getId()));
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
            return conn.createStatement().execute(String.format(
                    "UPDATE customer SET state = '%s' WHERE CuId = '%s';"
                    ,object.getState(), object.getId())) && customers.add(object);
        } catch (SQLException e) {
            customers.add(object);
            throw new RuntimeException(e);
        }
    }

    // @TODO add delete function
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
                Optional<Contact> optionalContact = Util.findContactById(resultSetCustomers.getString("contact"));
                Contact contact;
                if(optionalContact.isPresent()) {
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


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CustomerManagement cm = new CustomerManagement();
//        cm.customers.forEach(System.out::println);
//        System.out.print("Update Customer: ");
//        Optional<Customer> cus_1000008 = cm.findById("CUS_1000008");
//        try {
//            cm.create(Util.createCustomer(sc));
//        } catch (CustumerException e) {
//            System.out.printf("Cannot create a new customer: %s" , e.getMessage());
//        }
        System.out.print("Create customer[1], View Customers[2]: ");
        String choice = sc.nextLine();
        if(Objects.equals(choice, "1")) {
            try {
                cm.create(Util.createCustomer(sc));
            } catch (CustumerException e) {
                throw new RuntimeException(e);
            }
        } else if(Objects.equals(choice, "2")) {
            if(cm.customers.size() != 0) {
                cm.customers.forEach(System.out::println);
                System.out.print("Enter a customer id: ");
                Optional<Customer> cust;
                if((cust = cm.findById(sc.nextLine())).isPresent()) {
                    Customer customer = cust.get();
                    System.out.print("How many contracts do you want to create: ");
                    Integer contracts = Integer.parseInt(sc.nextLine());

                    for (int i = 0; i < contracts; i++) {
                        try {
                            customer.create(Util.createContract(sc));
                        } catch (ContractException e) {
                            System.out.println("Could not create contract.");
                        }
                    }
                    ArrayList<Contract> all = customer.findAll();
                    all.forEach(System.out::println);
                } else {
                    System.out.println("Account does not exist!");
                }
            } else {
                System.out.println("No Customers available;");
            }
        }
    }
}
