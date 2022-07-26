package Util;

import CRM.Contact;
import CRM.Contract;
import CRM.Customer;
import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Enum.Gender;
import CRM.Enum.STATE;
import CRM.Subscription;

import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

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

    public static Contract createContract(Scanner sc) { // Create Contract and Subscription
        System.out.print("What type of contract do you want to create (PREPAID, POSTPAID): ");
        ContractType contractType;
        while(true){
            try{
                contractType = ContractType.valueOf(sc.nextLine().toUpperCase());
                break;
            } catch (IllegalArgumentException iex){
                System.out.println("Wrong Type... Please try again!");
                System.out.print("What type of contract do you want to create (PREPAID, POSTPAID): ");
            }

        }   return new Contract(contractType, new Contact(ID.CONTRACT));
    }

    public static Contract updateContract(Scanner sc, Contract contract) { // Update Contract
        System.out.printf("Currently your STATE is: %s", contract.getState());
        System.out.print("What do you want to change the State to (PREPAID, POSTPAID): ");
        while (true) {
            String updateChoice = sc.nextLine().toUpperCase();
            try {
                contract.setState(STATE.valueOf(updateChoice));
                break;
            } catch (IllegalArgumentException ex){
                System.out.println("State must be PREPAID/POSTPAID");
            }
        }
        return contract;
    }

//    public static Subscription createSubsciption(Scanner sc, )
}
