package Telecom30Cent;

import Database.TelecomService;
import java.util.ArrayList;
import java.util.Optional;

public class ContractManagement implements TelecomService<Contract> {
    @Override
    public boolean create(Contract object) throws Exception {
        return false;
    }

    @Override
    public boolean update(Contract object) {
        return false;
    }

    @Override
    public boolean delete(Contract object) {
        return false;
    }

    @Override
    public Optional<Contract> findById(String id) {
        return Optional.empty();
    }

    @Override
    public ArrayList<Contract> findAll() {
        return null;
    }
}
