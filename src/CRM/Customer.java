package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Enum.STATE;
import CRM.Exceptions.ContractException;
import CRM.Exceptions.CustumerException;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;
import lombok.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Customer implements TelecomService<Contract> {
    @EqualsAndHashCode.Include
    private final String id;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    private CustomerType customerType;

    private Contact contact;
    @ToString.Exclude
    private ArrayList<Contract> contracts;

    public Customer(CustomerType customerType, Contact contact) {
        this.id = ID.CUSTOMER.createId();
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.customerType = customerType;
        this.contact = contact;
    }

    public Customer(String id, LocalDate createdDate, STATE state, CustomerType customerType, Contact contact) {
        this.id = id;
        this.createdDate = createdDate;
        this.state = state;
        this.customerType = customerType;
        this.contact = contact;
    }


    @Override
    public boolean create(Contract object) throws ContractException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            boolean success = conn.createStatement().execute(String.format(
                    "INSERT INTO contract VALUES('%s', '%s', '%s','%s', '%s')",
                    object.getId(), object.getContractType(), object.getCreatedDate(), object.getState(), this.id));
            if(success) return contracts.add(object); else return false;
        } catch (SQLException e) {
            throw new ContractException("Cannot add a contract to the database!");
        }
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
        return null;
    }

    @Override
    public ArrayList<Contract> findAll() {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            ResultSet resultSetContracts = conn.createStatement()
                    .executeQuery(String.format(
                            "SELECT * FROM contract where customerId = '%s'", this.id));
            ArrayList<Contract> allContracts = new ArrayList<>();
            while (resultSetContracts.next()) {
                String id = resultSetContracts.getString("CoId");
                ContractType contractType = ContractType.valueOf(resultSetContracts.getString("contractType"));
                LocalDate createdDate = LocalDate.parse(resultSetContracts.getString("createdDate"));
                STATE customerType = STATE.valueOf(resultSetContracts.getString("state"));
                Contract contract = new Contract(id, contractType, createdDate, state);
                allContracts.add(contract);
            }
            return allContracts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

    }
}
