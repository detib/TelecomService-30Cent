package CRM;

import CRM.Exceptions.ProductException;
import CRM.Service.Data;
import CRM.Service.SMS;
import CRM.Service.SimCard;
import CRM.Service.Voice;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.Util;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

public class ProductManagement implements TelecomService<Product> {

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
                            "INSERT INTO product values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
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
            ResultSet allProducts = conn.createStatement().executeQuery("select * from products");
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
                tempProducts.add(new Product(id, simCard, sms, voice, data, fromDate, toDate, price, productName));
            }
            return tempProducts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
