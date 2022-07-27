package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.CustomerType;
import CRM.Enum.STATE;
import CRM.Exceptions.ContactException;
import CRM.Exceptions.ContractException;
import CRM.Exceptions.CustumerException;
import CRM.Service.*;
import CRM.Service.Data;
import Database.ContactService;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;
import Util.Util;
import lombok.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Customer implements TelecomService<Contract>, ContactService {
    @EqualsAndHashCode.Include
    private final String id;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    private CustomerType customerType;

    private Contact contact;
    @ToString.Exclude
    private ArrayList<Contract> contracts;

//    { this.contracts = findAll(); }

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
            conn.createStatement().execute(String.format(
                    "INSERT INTO contract VALUES('%s', '%s', '%s','%s', '%s', '%s')",
                    object.getId(), object.getContractType(), object.getCreatedDate(),
                    object.getState(), this.id, object.getContact().getId()));
            try {
                object.createContact();
            } catch (ContactException e) {
                System.out.println("Customer: cannot add contact to contract: " + e.getMessage());
            }
            if(this.contracts == null) this.contracts = new ArrayList<>();
            return contracts.add(object);
        } catch (SQLException e) {
            throw new ContractException("Cannot add a contract to the database!");
        }
    }

    @Override
    public boolean update(Contract object) {
        contracts.remove(object);
        try {
            Util.updateContract(new Scanner(System.in), object);
            Connection conn = DatabaseConn.getInstance().getConnection();
            return conn.createStatement().execute(String.format(
                    "UPDATE contract SET state = '%s' WHERE CoID = '%s';"
                    ,object.getState(), object.getId())) && contracts.add(object);
        } catch (SQLException e) {
            contracts.add(object);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Contract object) {
        ArrayList<Subscription> allSubscriptions = object.findAll();
        allSubscriptions.forEach(object::delete);
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format("DELETE FROM contact where CtId='%s'", object.getContact().getId()));
            conn.createStatement().execute(String.format("DELETE FROM contract where CoID='%s'", object.getId()));
            contracts.remove(object);
        } catch (SQLException sqle) {
            throw new RuntimeException("Failed to delete on Customer:" + sqle.getMessage());
        }
        return false;
    }

    @Override
    public Optional<Contract> findById(String id) {
        // @TODO recheck
        this.contracts = findAll();
        for (Contract contract : contracts) {
            if (contract.getId().equals(id)) {
                return Optional.of(contract);
            }
        }
        return Optional.empty();
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
                STATE state = STATE.valueOf(resultSetContracts.getString("state"));
                Optional<Contact> optionalContact = Util.findContactById(resultSetContracts.getString("contact"));
                Contact contact;
                if(optionalContact.isPresent()) {
                    contact = optionalContact.get();
                    Contract contract = new Contract(id, contractType, createdDate, state, contact);
                    allContracts.add(contract);
                } else {
                    throw new RuntimeException("Customer: contact not existing");
                }
            }
            return allContracts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ContactException cte) {
            System.out.println("Error on querying contact:" + cte.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void createContact() throws ContactException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            switch (this.customerType) {
                case BUSINESS -> conn.createStatement().execute(
                        String.format(
                                "INSERT INTO contact(CtId, IdType, CreatedDate, State, CustomerName)" +
                                        "VALUES('%s', '%s', '%s', '%s', '%s')",
                                contact.getId(), contact.getIdType(),
                                contact.getCreatedDate(), contact.getState(), contact.getCustomerName()
                        ));
                case INDIVIDUAL -> conn.createStatement().execute(
                        String.format(
                                "INSERT INTO contact(CtId, Name, LastName, Gender, Dob, IdType, CreatedDate, State) " +
                                        "VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                                contact.getId(), contact.getName(), contact.getLastname(), contact.getGender(),
                                contact.getDob(), contact.getIdType(), contact.getCreatedDate(), contact.getState()
                        ));
            }

        } catch (SQLException e) {
            throw new ContactException("Customer: Could not create contact: " + e.getMessage());
        }
    }
}
