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
        loop: while(true) {
            System.out.print("Exit[0], Create customer[1], View Customers[2]: ");
            String choice = sc.nextLine();
            if(choice.equals("1")) {
                try {
                    cm.create(Util.createCustomer(sc));
                } catch (CustumerException e) {
                    throw new RuntimeException(e);
                }
            } else if(Objects.equals(choice, "2")) {
                if(cm.customers.size() != 0) {
                    cm.customers.forEach(System.out::println);
                    System.out.println("Exit[0], View a specific customer [1], Delete a customer[2]: ");
                    choice = sc.nextLine();
                    switch (choice) {
                        case "1" -> {
                            System.out.print("Enter a customer id: ");
                            Optional<Customer> cust;
                            if ((cust = cm.findById(sc.nextLine())).isPresent()) {
                                Customer customer = cust.get();
                                System.out.println(customer);
                                System.out.print("Create Contracts[1], View Contracts[2]: ");
                                choice = sc.nextLine();
                                if (choice.equals("1")) {
                                    try {
                                        customer.create(Util.createContract(sc));
                                    } catch (ContractException e) {
                                        System.out.println("Could not create contract.");
                                    }
                                } else if (choice.equals("2")) {
                                    ArrayList<Contract> contracts = customer.findAll();
                                    contracts.forEach(System.out::println);
                                    if (contracts.size() != 0) {
                                        System.out.print("Exit[0], Update Contract[1], View Contract[2]: ");
                                        choice = sc.nextLine();
                                        switch (choice) {
                                            case "0" -> {
                                                break loop;
                                            }
                                            case "1" -> {
                                                System.out.print("Write the contract id to update: ");
                                                Optional<Contract> contractToUpdate = customer.findById(sc.nextLine());
                                                contractToUpdate.ifPresent(customer::update);
                                            }
                                            case "2" -> {
                                                System.out.print("Write the contract id to view: ");
                                                Optional<Contract> contractToView = customer.findById(sc.nextLine());
                                                if(contractToView.isPresent()) {
                                                    Contract contract = contractToView.get();
                                                    ArrayList<Subscription> subscriptions = contract.findAll();
                                                    subscriptions.forEach(System.out::println);
                                                    System.out.print("Create Subscription[1], View Subscription[2]: ");
                                                    choice = sc.nextLine();
                                                    if (choice.equals("1")) {
                                                        try {
                                                            contract.create(Util.getSubscription(sc));
                                                        } catch (SubscriptionException e) {
                                                            System.out.println("Could not create subscription.");
                                                        }
                                                    } else if (choice.equals("2")) {
                                                        System.out.print("Write the subscription id to view: ");
                                                        Optional<Subscription> subscriptionToView = contract.findById(sc.nextLine());
                                                        if(subscriptionToView.isPresent()) {
                                                            Subscription subscription = subscriptionToView.get();
//                                                            System.out.println(subscription);
                                                            ArrayList<Service> subscriptionAll = subscription.findAll();
                                                            subscriptionAll.forEach(System.out::println);
                                                            //@TODO
                                                        }
                                                    }
                                                } else {
                                                    System.out.println("Could not find contract.");
                                                }
                                            }
                                            default -> {
                                                System.out.println("Invalid choice.");
                                            }
                                        }
                                    } else {
                                        System.out.println("No contracts found.");
                                    }
                                } else {
                                    System.out.println("Invalid choice.");
                                }
                            } else {
                                System.out.println("Could not find customer.");
                            }
                        }
                        case "2" -> {
                            System.out.print("Enter a customer id: ");
                            Optional<Customer> cust;
                            if ((cust = cm.findById(sc.nextLine())).isPresent()) {
                                Customer customer = cust.get();
                                System.out.print("Enter a contract id: ");
                                Optional<Contract> contract = customer.findById(sc.nextLine());
                                if(contract.isPresent()) {
                                    customer.delete(contract.get());
                                } else {
                                    System.out.println("Could not find contract.");
                                }
                            } else {
                                System.out.println("Could not find customer.");
                            }
//                            break;
                        }
                        default -> {
                            System.out.println("Invalid choice.");
                        }
                    }
                } else {
                    System.out.println("No customers found.");
                }
            } else if(choice.equals("0")) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}