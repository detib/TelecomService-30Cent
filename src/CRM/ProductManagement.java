package CRM;

import Database.TelecomService;

import java.util.ArrayList;
import java.util.Optional;

public class ProductManagement implements TelecomService<Product> {

    @Override
    public boolean create(Product object) throws Exception {
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
        return Optional.empty();
    }

    @Override
    public ArrayList<Product> findAll() {
        return null;
    }
}
