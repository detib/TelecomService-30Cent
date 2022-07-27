package CRM;

import CRM.Exceptions.*;
import CRM.Service.Service;
import CRM.Service.SimCard;
import CRM.Service.Voice;
import Util.Util;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class Telecom30Cent {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CustomerManagement cm = new CustomerManagement();
        ProductManagement pm = new ProductManagement();
        loop: while(true) {
            System.out.print("Exit[0], Create customer[1], View Customers[2], View Products[3]: ");
            String choice = sc.nextLine();
            if(choice.equals("1")) {
                try {
                    cm.create(Util.createCustomer(sc));
                } catch (CustumerException e) {
                    throw new RuntimeException(e);
                }

            } else if(Objects.equals(choice, "2")) {
                if(cm.getCustomers().size() != 0) {
                    cm.getCustomers().forEach(System.out::println);
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
                                                    System.out.print("Create Subscription[1], View Subscription[2], Delete Subscription[3], Update Subscription[4], Buy Product[5]: ");
                                                    choice = sc.nextLine();
                                                    if (choice.equals("1")) {
                                                        try {
                                                            contract.create(Util.getSubscription(sc));
                                                        } catch (SubscriptionException e) {
                                                            System.out.println("Could not create subscription.");
                                                        }
                                                    } else if (choice.equals("2")) {
                                                        subscriptions.forEach(System.out::println);
                                                        System.out.print("Write the subscription id to view: ");
                                                        Optional<Subscription> subscriptionToView = contract.findById(sc.nextLine());
                                                        if(subscriptionToView.isPresent()) {
                                                            Subscription subscription = subscriptionToView.get();
                                                            ArrayList<Service> subscriptionAll = subscription.findAll();
                                                            subscriptionAll.forEach(System.out::println);

                                                            System.out.print("Create Service[1], View Service[2], Delete Service[3]");
                                                            choice = sc.nextLine();
                                                            if (Objects.equals(choice, "1")){
                                                                try {
                                                                    subscription.create(Util.createService(sc));
                                                                } catch (ServiceException e){
                                                                    System.out.println("Could not create Service!");
                                                                }
                                                            } else if (choice.equals("2")) {
                                                                subscriptionAll.forEach(System.out::println);
                                                            } else if (choice.equals("3")){
                                                                System.out.print("Write the service ID to delete: ");
                                                                if (subscriptionAll.size() != 0) {
                                                                    Optional<Service> optionalService = subscription.findById(sc.nextLine());
                                                                    if (optionalService.isPresent()) {
                                                                        Service service = optionalService.get();
                                                                        if (service.getServiceType() instanceof SimCard || service.getServiceType() instanceof Voice) {
                                                                            System.out.println("You cannot delete a Service with a Sim Card");
                                                                        } else {
                                                                            subscription.delete(service);
                                                                        }
                                                                    }
                                                                } else {
                                                                    System.out.println("No services to delete");
                                                                }
                                                            }

                                                            //@TODO
                                                        } else {
                                                            System.out.println("Subscription not found.");
                                                        }
                                                    } else if (choice.equals("3")) {
                                                        System.out.println("Write the subscription id to delete: ");
                                                        Optional<Subscription> subscriptionToDelete = contract.findById(sc.nextLine());
                                                        if(subscriptionToDelete.isPresent()) {
                                                            contract.delete(subscriptionToDelete.get());
                                                        } else {
                                                            System.out.println("Subscription not found.");
                                                        }
                                                    } else if (choice.equals("4")) {
                                                        System.out.print("Write the subscription id to update: ");
                                                        choice = sc.nextLine();
//                                                        Optional<Subscription> subscriptionToUpdate = contract.findById(sc.nextLine());
//                                                        subscriptionToUpdate.ifPresent(contract::update);
                                                        Optional<Subscription> optionalSubscription = contract.findById(choice);
                                                        if(optionalSubscription.isPresent()) {
                                                            Subscription subscription = optionalSubscription.get();
                                                            contract.update(Util.updateSubscription(sc,subscription));
                                                        } else {
                                                            System.out.println("Subscription not found.");
                                                        }
                                                    } else if (choice.equals("5")) {
                                                        System.out.print("Write the subscription id to buy: ");
                                                        Optional<Subscription> subscriptionToBuy = contract.findById(sc.nextLine());
                                                        if(subscriptionToBuy.isPresent()) {
                                                            Subscription subscription = subscriptionToBuy.get();
                                                            ArrayList<Product> products = pm.findAll();
                                                            products.forEach(System.out::println);
                                                            if(products.size() != 0) {
                                                                System.out.print("Select product to buy (ID): ");
                                                                choice = sc.nextLine();
                                                                Optional<Product> product = pm.findById(choice);
                                                                if(product.isPresent()) {
                                                                    Product prod = product.get();
                                                                    if(subscription.buyProduct(prod)) {
                                                                        System.out.printf("Product %s bought!", prod);
                                                                    } else {
                                                                        System.out.println("Not allowed to purchase this product!");
                                                                    }
                                                                }
                                                            } else {
                                                                System.out.println("There are no available products.");
                                                            }
                                                        } else {
                                                            System.out.println("Subscription not found.");
                                                        }

                                                    } else {
                                                        System.out.println("Invalid choice.");
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
            } else if (choice.equals("3")){
                System.out.println("Products: ");
                pm.getProducts().forEach(System.out::println);
                System.out.print("Create[1] or Delete[2] a product: ");
                choice = sc.nextLine();

                if (choice.equals("1")){
                    try {
                        pm.create(Util.getProduct(sc));
                    } catch (ProductException e) {
                        throw new RuntimeException(e);
                    }
                } else if (choice.equals("2")){
                    System.out.print("Enter the product ID you want to delete");
                    String id = sc.nextLine();
                    Optional<Product> prod;
                    if ((prod = pm.findById(id)).isPresent()) {
                        pm.delete(prod.get());
                    } else {
                        System.out.println("No product with that ID found!");
                    }
                }

            } else if (choice.equals("0")){
                break;
            }

            else {
                System.out.println("Invalid choice.");
            }
        }
    }
}

