package Main;

import Telecom30Cent.Customer;
import Telecom30Cent.CustomerManagement;
import Telecom30Cent.Exceptions.CustumerException;
import Telecom30Cent.ProductManagement;
import Util.Util;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ProductManagement pm = new ProductManagement();
        CustomerManagement cm = new CustomerManagement();
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.print("Exit[0], Create Customer[1], View Customers[2], View Products[3]: ");
            String choice = sc.nextLine();
            switch (choice){
                case "0" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                case "1" -> {
                    try {
                        cm.create(Objects.requireNonNull(Util.createCustomer(sc)));
                    } catch (CustumerException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "2" -> {
                    System.out.println("+++++++++++++++++++++++++++++++++++");
                    System.out.println("Customers: ");
                    cm.getCustomers().forEach(System.out::println);
                    System.out.println("+++++++++++++++++++++++++++++++++++");
                }
                case "3" -> {}
            }
        }
    }
}
