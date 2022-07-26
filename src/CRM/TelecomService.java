package CRM;

import CRM.Contact.IndividualContact;
import CRM.Enum.Gender;
import Util.ID;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Scanner;

public class TelecomService implements Database.TelecomService {

    @Override
    public boolean create() {
        return false;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public ResultSet findById() {
        return null;
    }

    @Override
    public ResultSet findAll() {
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to our Telecom Service CRM");

        Scanner myObj = new Scanner(System.in);
        System.out.println("1. Create a Customer\n2. Update a Customer\n3.Delete a Customer\n" +
                "4. Find a Customer by ID\n5. Find all Customers ");

        String userInput = myObj.nextLine();

        if (Objects.equals(userInput, "1")){
            System.out.println("1. Individual Customer\n2. Business Customer");
            String customerChoice = myObj.nextLine();

            if (Objects.equals(customerChoice, "1")){ // Create an Individual Customer
                System.out.print("Name: ");
                String name = myObj.nextLine();
                System.out.print("Lastname: ");
                String lastname = myObj.nextLine();
                System.out.print("ID Number: ");
                Long idNumber = myObj.nextLong();
                System.out.print("Gender: ");
                String gender1 = myObj.next();
                Gender gender = Gender.valueOf(gender1);
                System.out.print("Date of Birth: ");
                LocalDate dob = LocalDate.parse(myObj.next());

                IndividualContact cont = new IndividualContact(name, lastname, gender, dob);
                IndividualCustomer customer = new IndividualCustomer(
                        name, lastname, idNumber, gender, dob, cont);

                cont.create();
                customer.create();
            }
        }
    }
}
