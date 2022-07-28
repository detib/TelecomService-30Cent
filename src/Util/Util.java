package Util;

import Telecom30Cent.*;
import Telecom30Cent.Enum.ContractType;
import Telecom30Cent.Enum.CustomerType;
import Telecom30Cent.Enum.Gender;
import Telecom30Cent.Enum.STATE;
import Telecom30Cent.Exceptions.ContactException;
import Telecom30Cent.Service.*;
import Database.DatabaseConn;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Util {

    /**
     * Customer class constructor
     * Checks if the customer is individual or business and creates the appropriate object
     * @param sc Scanner object
     * @return Customer object
     */
    public static Customer createCustomer(Scanner sc) {
        System.out.print("What type of customer are you? (Individual, Business):");
        CustomerType customerType;
        while (true) {
            try {
                customerType = CustomerType.valueOf(sc.nextLine().toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid input. (INDIVIDUAL, BUSINESS): ");
            }
        }
        if(customerType == CustomerType.BUSINESS) {
            System.out.print("What is your Customer Name: ");
            String customerName = sc.nextLine();
            Contact contact = new Contact(ID.CUSTOMER, customerName);
            return new Customer(CustomerType.BUSINESS, contact);
        } else if (customerType == CustomerType.INDIVIDUAL) {
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Lastname: ");
            String lastname = sc.nextLine();
            System.out.print("Gender: ");
            Gender gender;
            while(true){
                try {
                    gender = Gender.valueOf(sc.nextLine().toUpperCase());
                    break;
                } catch (IllegalArgumentException iae) {
                    System.out.print("Wrong gender format! (M, F): ");
                }
            }
            System.out.print("Date of Birth: ");
            LocalDate dob;
            while(true) {
                try {
                    dob = LocalDate.parse(sc.nextLine());
                    break;
                } catch (DateTimeParseException e) {
                    System.out.print("Wrong date format! (YYYY-MM-DD): ");
                }
            }

            Contact contact = new Contact(ID.CUSTOMER, name, lastname, gender, dob);
            return new Customer(CustomerType.INDIVIDUAL, contact);
        }
        return null;
    }

    /**
     * updateCustomer method updates the Customer state to Active, Deactive, or Inactive, based on the user input
     * @param sc Scanner object
     * @param customer Customer object
     * @return Customer object
     */
    public static Customer updateCustomer(Scanner sc, Customer customer) {
        System.out.printf("Currently your STATE is: %s\n", customer.getState());
        System.out.print("What do you want to change the State to (ACTIVE/DEACTIVE/INACTIVE) or 'Q' to exit: ");
        while (true){
            String updateChoice = sc.nextLine().toUpperCase();
            if (updateChoice.equalsIgnoreCase("q")) return customer;
            try {
                customer.setState(STATE.valueOf(updateChoice));
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println("State must be ACTIVE/DEACTIVE/INACTIVE");
            }

        }
        return customer;
    }

    /**
     * createContract method creates a Contract object based on the user input
     * @param sc Scanner object
     * @return Contract object
     */
    public static Contract createContract(Scanner sc) { // Create Contract
        System.out.print("What type of contract do you want to create (PREPAID, POSTPAID): ");
        ContractType contractType;
        while(true){
            try {
                String contype = sc.nextLine();
                contractType = ContractType.valueOf(contype.toUpperCase());
                break;
            } catch (IllegalArgumentException iex) {
                System.out.print("Wrong Type... Please try again!");
                System.out.print("What type of contract do you want to create (PREPAID, POSTPAID): ");
            }
        }
        return new Contract(contractType, new Contact(ID.CONTRACT));
    }

    /**
     * updateContract method updates the Contract state to Active, Deactive, or Inactive, based on the user input
     * @param sc Scanner object
     * @param contract Contract object
     * @return Contract object
     */
    public static Contract updateContract(Scanner sc, Contract contract) { // Update Contract
        System.out.printf("Currently your STATE is: %s", contract.getState());
        System.out.print("What do you want to change the State to (ACTIVE/DEACTIVE/INACTIVE) or 'Q' to exit: ");
        while (true) {
            String updateChoice = sc.nextLine().toUpperCase();
            if(updateChoice.equalsIgnoreCase("q")) return contract;
            try {
                contract.setState(STATE.valueOf(updateChoice));
                break;
            } catch (IllegalArgumentException ex){
                System.out.println("State must be (ACTIVE/DEACTIVE/INACTIVE)");
            }
        }
        return contract;
    }

    /**
     * getSubscription method creates a Subscription object based on the user input
     * @param sc Scanner object
     * @return Subscription object
     */
    public static Subscription getSubscription(Scanner sc) {
        System.out.print("Which Phone Number option do you want (044, 045, 046): ");
        loop: while (true){
            String phoneChoice = sc.nextLine();
            PhoneNumber phoneNumber;
            switch (phoneChoice) {
                case "044" -> {
                    phoneNumber = PhoneNumber.FOUR;
                    return new Subscription(new Contact(ID.SUBSCRIPTION), phoneNumber);
                }
                case "045" -> {
                    phoneNumber = PhoneNumber.FIVE;
                    return new Subscription(new Contact(ID.SUBSCRIPTION), phoneNumber);
                }
                case "046" -> {
                    phoneNumber = PhoneNumber.SIX;
                    return new Subscription(new Contact(ID.SUBSCRIPTION), phoneNumber);
                }
                default -> System.out.print("Choose from the following (044, 045, 046): ");
            }
        }

    }

    /**
     * updateSubscription method updates the Subscription state to Active, Deactive, or Inactive, based on the user input
     * @param sc Scanner object
     * @param subscription Subscription object
     * @return Subscription object
     */
    public static Subscription updateSubscription(Scanner sc, Subscription subscription){
        System.out.printf("Your State is: %s\n", subscription.getState());
        System.out.print("What do you want to change the State to (ACTIVE, DEACTIVE, INACTIVE) or 'Q' to exit");
        while(true){
            String updateChoice = sc.nextLine();
            if(updateChoice.equalsIgnoreCase("q")) return subscription;
            try {
                subscription.setState(STATE.valueOf(updateChoice));
                break;
            } catch (IllegalArgumentException ex){
                System.out.println("State must be (ACTIVE, DEACTIVE, INACTIVE)!");
            }
        }
        return subscription;
    }

    /**
     * findContactById method finds a Contact object based on the id
     * @param id ID object
     * @return Contact object
     * @throws ContactException if the Contact is not found
     */
    public static Optional<Contact> findContactById(String id) throws ContactException{
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            ResultSet resultContact = conn.createStatement()
                    .executeQuery(String.format(
                            "SELECT * FROM contact where CtID='%s'", id));
            while(resultContact.next()) {
                String ctID = resultContact.getString("CtID");
                String name = resultContact.getString("Name");
                String lastname = resultContact.getString("Lastname");
                ID idType = ID.valueOf(resultContact.getString("IdType"));
                LocalDate createdDate = LocalDate.parse(resultContact.getString("CreatedDate"));
                STATE state = STATE.valueOf(resultContact.getString("State"));
                String customerName = resultContact.getString("CustomerName");
                if(customerName != null) {
                    return Optional.of(new Contact(ctID, idType, createdDate, state, customerName));
                } else if(name != null) {
                    LocalDate dob = LocalDate.parse(resultContact.getString("Dob"));
                    Gender gender = Gender.valueOf(resultContact.getString("Gender"));
                    return Optional.of(new Contact(id, name, lastname, gender, dob, idType, createdDate, state));
                } else {
                    return Optional.of(new Contact(ctID, idType, createdDate, state));
                }
            }
        } catch (SQLException e) {
            throw new ContactException(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * createService method creates a Service object based on the user input
     * @param sc Scanner object
     * @return Service object
     */
    public static Service createService(Scanner sc) {
        System.out.print("What do you want to add (DATA[1], SMS[2]): ");
        while (true) {
            String choice = sc.nextLine();
            if (choice.equals("1")) {
                return new Service(new Data(0));
            } else if(choice.equals("2")) {
                return new Service(new SMS(0));
            } else {
                System.out.print("Enter one of the choices (DATA[1], SMS[2]): ");
            }
        }
    }

    /**
     * getProduct method creates a Product object based on the user input
     * @param sc Scanner object
     * @return Product object
     */
    public static Product getProduct(Scanner sc) {
        System.out.print("How many sim credits do you want: ");
        Integer simCredits;
        while (true) {
            try {
                simCredits = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a number: ");
                sc.nextLine();
            }
        }
        System.out.print("How many voice minutes do you want: ");
        Integer voiceMinutes;
        while (true) {
            try {
                voiceMinutes = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a number: ");
                sc.nextLine();
            }
        }
        System.out.print("How many data MB do you want: ");
        Integer dataMB;
        while (true) {
            try {
                dataMB = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a number: ");
                sc.nextLine();
            }
        }
        System.out.print("How many text messages do you want: ");
        Integer textMessages;
        while (true) {
            try {
                textMessages = sc.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a number: ");
                sc.nextLine();
            }
        }
        sc.nextLine();
        System.out.print("What do you want to call this product? : ");
        String productName = sc.nextLine();
        System.out.print("What type of product do you want (PREPAID, POSTPAID): ");
        ContractType productType;
        while (true) {
            try {
                productType = ContractType.valueOf(sc.nextLine());
                break;
            } catch (IllegalArgumentException iae) {
                System.out.println("Wrong Type... Please try again!");
                System.out.print("What type of product do you want (PREPAID, POSTPAID): ");
            }

        }
        LocalDate fromDate;
        while (true) {
            System.out.println("When do you want this product to start? (YYYY-MM-DD)");
            try {
                fromDate = LocalDate.parse(sc.nextLine());
                break;
            } catch (DateTimeParseException e) {
                System.out.print("Please enter a valid date(yyyy-mm-dd): ");
            }
        }
        LocalDate toDate;
        while (true) {
            System.out.println("When do you want this product to end? (YYYY-MM-DD)");
            try {
                toDate = LocalDate.parse(sc.nextLine());
                break;
            } catch (DateTimeParseException e) {
                System.out.print("Please enter a valid date(yyyy-mm-dd): ");
            }
        }
        return new Product(
                new SimCard(simCredits), new SMS(textMessages),
                new Voice(voiceMinutes), new Data(dataMB),
                fromDate, toDate, productName, productType);
    }

    public static ArrayList<Product> findProductsCheaperThan(Integer price, ProductManagement pm) {
        ArrayList<Product> prod = new ArrayList<>();
        pm.findAll().forEach(product -> {
            if (product.getPrice() < price) {
                prod.add(product);
            }
        });
        return prod;
    }

    public static ArrayList<Product> findProductsthatWillExpire(Integer date, ProductManagement pm) {
        ArrayList<Product> prod = new ArrayList<>();
        pm.findAll().forEach(product -> {
            if (product.getToDate().isBefore(LocalDate.now().plusDays(date))) {
                prod.add(product);
            }
        });
        return prod;
    }

    public static ArrayList<Product> findProductsByType(ContractType type, ProductManagement pm) {
        ArrayList<Product> prod = new ArrayList<>();
        pm.findAll().forEach(product -> {
            if (product.getContractType() == type) {
                prod.add(product);
            }
        });
        return prod;
    }
}
