package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
import CRM.Exceptions.ContactException;
import CRM.Exceptions.ContractException;
import CRM.Exceptions.SubscriptionException;
import CRM.Service.Service;
import Database.ContactService;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;
import Util.Util;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Contract implements TelecomService<Subscription>, ContactService {
    private final String id;
    private ContractType contractType;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    private Contact contact;
    @ToString.Exclude
    private ArrayList<Subscription> subscription;

    public Contract(ContractType contractType, Contact contact) {
        this.id = ID.CONTRACT.createId();
        this.contractType = contractType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.contact = contact;
    }

    public Contract(String id, ContractType contractType, LocalDate createdDate, STATE state, Contact contact) {
        this.id = id;
        this.contractType = contractType;
        this.createdDate = createdDate;
        this.state = state;
        this.contact = contact;
    }

    @Override
    public boolean create(Subscription object) throws SubscriptionException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "INSERT INTO Subscription VALUES('%s', '%s', '%s', " +
                            "'%s', '%s', '%s')",
                    object.getId(), object.getPhoneNumber(), object.getContractType(), object.getContractType(),
                    object.getState(), this.id));
            return subscription.add(object);
        } catch (SQLException e) {
            throw new SubscriptionException("Cannot add a Subscription to the database!");
        }
    }

    @Override
    public boolean update(Subscription object) {
        subscription.remove(object);
        try {
            Util.updateSubscription(new Scanner(System.in), object);
            Connection conn = DatabaseConn.getInstance().getConnection();
            return conn.createStatement().execute(String.format(
                    "UPDATE Subscription SET state = '%s' WHERE SuID='%s';",
                    object.getState(), object.getId())) && subscription.add(object);
        } catch (SQLException e) {
            subscription.add(object);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Subscription object) {
        return false;
    }

    @Override
    public Optional<Subscription> findById(String id) {
        return Optional.empty();
    }

    @Override
    public ArrayList<Subscription> findAll() {
        return null;
    }

    @Override
    public void createContact() throws ContactException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(
                    String.format(
                            "INSERT INTO contact(CtID, IdType, CreatedDate, State, CustomerName) " +
                                    "VALUES('%s', '%s', '%s', '%s')",
                            contact.getId(), contact.getIdType(), contact.getCreatedDate(),
                            contact.getState(), contact.getCustomerName()));
        } catch (SQLException e) {
            throw new ContactException("Contract: Could not create contact: " + e.getMessage());
        }
    }
}
