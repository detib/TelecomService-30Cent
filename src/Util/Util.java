package Util;

import CRM.*;
import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Enum.Gender;
import CRM.Enum.STATE;
import CRM.Exceptions.ContactException;
import CRM.Exceptions.ContractException;
import CRM.Service.*;
import Database.DatabaseConn;
import lombok.Getter;

import java.lang.reflect.GenericSignatureFormatError;
import java.nio.file.FileAlreadyExistsException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Util {
    public final static String phoneNumberRegex = "3834[4-6]\\d{6}";

    public static Customer createCustomer(Scanner sc) { // Create Customer and Contact
        System.out.print("What type of customer are you? (Individual, Business):");
        CustomerType customerType = CustomerType.valueOf(sc.nextLine().toUpperCase());
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
            String gender = sc.nextLine();
            System.out.print("Date of Birth: ");
            String dob = sc.nextLine();

            Contact contact = new Contact(ID.CUSTOMER, name, lastname, Gender.valueOf(gender), LocalDate.parse(dob));
            return new Customer(CustomerType.INDIVIDUAL, contact);
        }
        return null;
    }

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

    public static Subscription updateSubscription(Scanner sc, Subscription subscription){
        System.out.printf("Your State is: %s", subscription.getState());
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

    public static Service createService(Scanner sc) {
        System.out.print("What do you want to add (DATA[1], SMS[2]): ");
        String choice;
        while (true) {
            choice = sc.nextLine();
            if (Objects.equals(choice, "1") || Objects.equals(choice, "2")) break;
            System.out.print("Enter one of the choices (DATA[1], SMS[2]): ");
        }
        if (Objects.equals(choice, "1")) {
            System.out.print("How many MB do you want to add: ");
            Integer data;
            while (true) {
                try {
                    data = sc.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a number: ");
                    sc.nextInt();
                }
            }
            return new Service(new Data(data));
        } else if (Objects.equals(choice, "2")) {
            System.out.print("How many SMS do you want to add: ");
            Integer message;
            while (true) {
                try {
                    message = sc.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a number: ");
                    sc.nextInt();
                }
            }
            return new Service(new SMS(message));
        }

        return null;
    }
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

}
