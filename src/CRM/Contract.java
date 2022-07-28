package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
import CRM.Exceptions.*;
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
import java.sql.DataTruncation;
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

    {this.subscriptions = new ArrayList<>();} // runs before constructor to remove null pointer exception

    public Contract(ContractType contractType, Contact contact) { //  Constructor for Contract
        this.id = ID.CONTRACT.createId();
        this.contractType = contractType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.contact = contact;
    }

    public Contract(String id, ContractType contractType, LocalDate createdDate, STATE state, Contact contact) { // constructor database connection
        this.id = id;
        this.contractType = contractType;
        this.createdDate = createdDate;
        this.state = state;
        this.contact = contact;
    }

    @Override
    public boolean create(Subscription object) throws SubscriptionException { // create a contact for a subscription
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "INSERT INTO Subscription VALUES('%s', '%s', '%s', '%s', '%s', '%s')",
                    object.getId(), object.getPhoneNumber(), object.getCreatedDate(), object.getState(),
                    object.getContact().getId(), this.id));
            try {
                object.createContact();
                object.create(new Service(new SimCard(300))); // create a sim card
                object.create(new Service(new Voice(60))); // create a voice service
            } catch (ServiceException | ContactException | ServiceExistsException e) {
                throw new RuntimeException(e);
            }
            if(subscriptions == null) this.subscriptions = new ArrayList<>(); // initialize subscriptions to remove null pointer exception
            return subscriptions.add(object);
        } catch (SQLException e) {
            throw new SubscriptionException("Cannot add a Subscription to the database!");
        }
    }

    @Override
    public boolean update(Subscription object) {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "UPDATE Subscription SET state = '%s' WHERE SuID='%s';",
                    object.getState(), object.getId()));
            this.subscriptions = findAll();
            return true;
        } catch (SQLException e) {
            subscriptions.add(object);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Subscription object) { // delete a subscription and its contact from the database
        ArrayList<Service> services = object.findAll();
        services.forEach(object::delete);
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format("DELETE FROM contact where CtId='%s'", object.getContact().getId())); // delete the contact
            conn.createStatement().execute(String.format("DELETE FROM subscription where SuID='%s'", id)); // delete the subscription
            subscriptions.remove(object); // remove the subscription from the ArrayList
        } catch (SQLException sqle) {
            throw new RuntimeException("Failed to delete on Contract: " + sqle.getMessage());
        }
        return false;
    }

    @Override
    public Optional<Subscription> findById(String id) { // find a subscription by its id, can return null
        if(subscriptions == null) this.subscriptions = findAll();
        for (Subscription subscription : subscriptions){ // iterate through the subscriptions
            if (subscription.getId().equals(id)){
                return Optional.of(subscription);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Subscription> findAll() { // find all subscriptions of a contract
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
                if(optionalContact.isPresent()) { // if the contact is found, add it to the subscription
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
    public void createContact() throws ContactException { // create a contact for a subscription
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(
                    String.format(
                            "INSERT INTO contact(CtID, IdType, CreatedDate, State) " +
                                    "VALUES('%s', '%s', '%s', '%s')",
                            contact.getId(), contact.getIdType(), contact.getCreatedDate(),
                            contact.getState()));
        } catch (SQLException e) {
            throw new ContactException("Contract: Could not create contact: " + e.getMessage());
        }
    }
}
