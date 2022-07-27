package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
import CRM.Exceptions.ContactException;
import CRM.Exceptions.ServiceException;
import CRM.Exceptions.SubscriptionException;
import CRM.Service.Service;
import CRM.Service.ServiceType;
import CRM.Service.SimCard;
import CRM.Service.Voice;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Contract implements TelecomService<Subscription>, ContactService {
    @EqualsAndHashCode.Include
    private final String id;
    private ContractType contractType;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    private Contact contact;
    @ToString.Exclude
    private ArrayList<Subscription> subscriptions;

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
                    "INSERT INTO Subscription VALUES('%s', '%s', '%s', '%s', '%s', '%s')",
                    object.getId(), object.getPhoneNumber(), object.getCreatedDate(), object.getState(),
                    object.getContact().getId(), this.id));
            try {
                object.create(new Service(new SimCard()));
                object.create(new Service(new Voice()));
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
            if(subscriptions == null) this.subscriptions = new ArrayList<>();
            return subscriptions.add(object);
        } catch (SQLException e) {
            throw new SubscriptionException("Cannot add a Subscription to the database!");
        }
    }

    @Override
    public boolean update(Subscription object) {
        subscriptions.remove(object);
        try {
            Util.updateSubscription(new Scanner(System.in), object);
            Connection conn = DatabaseConn.getInstance().getConnection();
            return conn.createStatement().execute(String.format(
                    "UPDATE Subscription SET state = '%s' WHERE SuID='%s';",
                    object.getState(), object.getId())) && subscriptions.add(object);
        } catch (SQLException e) {
            subscriptions.add(object);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Subscription object) {
        return false;
    }

    @Override
    public Optional<Subscription> findById(String id) {
        this.subscriptions = findAll();
        for (Subscription subscription : subscriptions){
            if (subscription.getId().equals(id)){
                return Optional.of(subscription);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Subscription> findAll() {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            ResultSet resultSetSubscriptions = conn.createStatement()
                    .executeQuery(String.format(
                            "SELECT * FROM subscription where contractId='%s'", this.id));
            ArrayList<Subscription> allSubscriptions = new ArrayList<>();
            while (resultSetSubscriptions.next()) {
                String id = resultSetSubscriptions.getString("SuID");
                String phoneNumber = resultSetSubscriptions.getString("phoneNumber");
                LocalDate createdDate = LocalDate.parse(resultSetSubscriptions.getString("createdDate"));
                STATE state = STATE.valueOf(resultSetSubscriptions.getString("state"));
                Optional<Contact> optionalContact = Util.findContactById(resultSetSubscriptions.getString("contact"));
                Contact contact;
                if(optionalContact.isPresent()) {
                    contact = optionalContact.get();
                    Subscription subscription = new Subscription(id, phoneNumber, createdDate, state, contact);
                    allSubscriptions.add(subscription);
                } else {
                    throw new RuntimeException("Contract: contact not existing");
                }
            }
            return allSubscriptions;
        } catch (SQLException sqle){
            throw new RuntimeException(sqle);
        } catch (ContactException cte){
            System.out.println("Error on querying contact:" + cte.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void createContact() throws ContactException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(
                    String.format(
                            "INSERT INTO contact(CtID, IdType, CreatedDate, State, CustomerName) " +
                                    "VALUES('%s', '%s', '%s', '%s', '%s')",
                            contact.getId(), contact.getIdType(), contact.getCreatedDate(),
                            contact.getState(), contact.getCustomerName()));
        } catch (SQLException e) {
            throw new ContactException("Contract: Could not create contact: " + e.getMessage());
        }
    }
}
