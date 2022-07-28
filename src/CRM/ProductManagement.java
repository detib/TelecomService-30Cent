package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Exceptions.ProductException;
import CRM.Service.Data;
import CRM.Service.SMS;
import CRM.Service.SimCard;
import CRM.Service.Voice;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.Util;
import lombok.Getter;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import Util.ID;

public class ProductManagement implements TelecomService<Product> {

    @Getter
    private ArrayList<Product> products;

    public ProductManagement() {
        this.products = findAll();
    }

    @Override
    public boolean create(Product object) throws ProductException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(
                    String.format(
                            "INSERT INTO product(productID, SimCard, SMS, Voice, Data, fromDate, toDate, price, productName, contractType) values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                            object.getId(), object.getSimCard().getCredits(), object.getSms().getMessages(), object.getVoice().getMinutes(),
                            object.getData().getMB(), object.getFromDate(), object.getToDate(), object.getPrice(), object.getProductName(), object.getContractType()
                    ));
            products.add(object);
        } catch (SQLException e) {
            throw new ProductException("ProductManagement: Cannot Create Product: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean update(Product object) {
        return false;
    }

    @Override
    public boolean delete(Product object) {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format("UPDATE product set state='DEACTIVATE' where productID='%s'", object.getId()));
            products.remove(object);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public Optional<Product> findById(String id) {
        for (Product product : products) {
            if(product.getId().equals(id)) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Product> findAll() {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            ResultSet allProducts = conn.createStatement().executeQuery("select * from product where state != 'DEACTIVATE')");
            ArrayList<Product> tempProducts = new ArrayList<>();
            while(allProducts.next()) {
                String id = allProducts.getString("productID");
                SimCard simCard = new SimCard(allProducts.getInt("SimCard"));
                SMS sms = new SMS(allProducts.getInt("SMS"));
                Voice voice = new Voice(allProducts.getInt("Voice"));
                Data data = new Data(allProducts.getInt("Data"));
                LocalDate fromDate = LocalDate.parse(allProducts.getString("fromDate"));
                LocalDate toDate = LocalDate.parse(allProducts.getString("toDate"));
                Integer price = allProducts.getInt("price");
                String productName = allProducts.getString("productName");
                ContractType contractType = ContractType.valueOf(allProducts.getString("contractType"));
                tempProducts.add(new Product(id, simCard, sms, voice, data, fromDate, toDate, price, productName, contractType));
            }
            return tempProducts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Subscription> findCustomersByProducts(Product prod, CustomerManagement cm) {
        Product product = findById(prod.getId()).get();
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            ResultSet allCustomers = conn.createStatement().executeQuery(
                    String.format(
                            "select * from sales where productId = '%s'", product.getId()
                    ));
            ArrayList<String> tempSubscribersId = new ArrayList<>();
            while(allCustomers.next()) {
                String id = allCustomers.getString("subscriberId");
                tempSubscribersId.add(id);
            }
            ArrayList<Subscription> tempSubscriptions = new ArrayList<>();
            cm.findAll().forEach(
                    customer -> customer.findAll()
                            .forEach(contract -> contract.findAll()
                                    .forEach(subscription -> {
                                        if(tempSubscribersId.contains(subscription.getId())) {
                                            tempSubscriptions.add(subscription);
                                        }
                                    }))
                                );
            return tempSubscriptions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ProductManagement pm = new ProductManagement();
        Scanner sc = new Scanner(System.in);
//        for (int i = 0; i < 5; i++) {
//            try {
//                pm.create(Util.getProduct(sc));
//            } catch (ProductException e) {
//                throw new RuntimeException(e);
//            }
//        }
        pm.products.forEach(System.out::println);
    }
}
