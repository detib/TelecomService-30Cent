package Telecom30Cent;

import Telecom30Cent.Enum.ContractType;
import Telecom30Cent.Enum.STATE;
import Telecom30Cent.Exceptions.*;
import Telecom30Cent.Service.Service;
import Telecom30Cent.Service.SimCard;
import Telecom30Cent.Service.Voice;
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

@Getter
@ToString(doNotUseGetters = true, includeFieldNames = false)
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

    /**
     * Constructor for Contract
     * @param contractType type of contract
     * @param contact contact of contract
     */
    public Contract(ContractType contractType, Contact contact) {
        this.id = ID.CONTRACT.createId();
        this.contractType = contractType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.contact = contact;
    }

    /**
     * Constructor for database Contract
     * @param id Contract id
     * @param contractType Contract type
     * @param createdDate Contract creation date
     * @param state Contract state
     * @param contact Contact object
     */
    public Contract(String id, ContractType contractType,
                    LocalDate createdDate, STATE state, Contact contact) {
        this.id = id;
        this.contractType = contractType;
        this.createdDate = createdDate;
        this.state = state;
        this.contact = contact;
    }

    /**
     * This method Overrides the create method in TelecomService
     * It adds a new subscription to the contract in the database
     * It adds the subscription to the contract's subscriptions arraylist
     * Also, it creates a contact, and a Service for the subscription
     * @param object Subscription
     * @return boolean true if a subscription is added successfully, false if not
     * @throws SubscriptionException
     */
    @Override
    public boolean create(Subscription object) throws SubscriptionException {
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

    /**
     * This method Overrides the update method in TelecomService
     * It updates a subscription in the database
     * It updates the subscription in the contract's subscriptions arraylist
     * @param object Subscription
     * @return true if the subscription was updated, false otherwise
     */
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

    /**
     * This method Overrides the delete method in TelecomService
     * It deletes a subscription and its contact from the database
     * It deletes the subscription from the contract's subscriptions arraylist
     * @param object the subscription to be deleted
     * @return true if the subscription was deleted, false otherwise
     */
    @Override
    public boolean delete(Subscription object) { // delete a subscription and its contact from the database
        ArrayList<Service> services = object.findAll();
        services.forEach(object::delete);
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format("DELETE FROM contact where CtId='%s'", object.getContact().getId())); // delete the contact
            conn.createStatement().execute(String.format("DELETE FROM subscription where SuID='%s'", id)); // delete the subscription
            conn.createStatement().execute(String.format("DELETE FROM sales where subscriberId='%s'", object.getId())); // delete the sales
            subscriptions.remove(object); // remove the subscription from the ArrayList
        } catch (SQLException sqle) {
            throw new RuntimeException("Failed to delete on Contract: " + sqle.getMessage());
        }
        return false;
    }

    /**
     * This method Overrides the findById method in TelecomService
     * It is used to find the subscription with the given id
     * @param id the id of the subscription to find
     * @return the subscription with the given id
     */
    @Override
    public Optional<Subscription> findById(String id) { // find a subscription by its id, can return null
        this.subscriptions = findAll();
        for (Subscription subscription : subscriptions){ // iterate through the subscriptions
            if (subscription.getId().equals(id)){
                return Optional.of(subscription);
            }
        }
        return Optional.empty();
    }

    /**
     * This method Overrides the findAll method in TelecomService
     * It is used to find all the subscriptions of the contract from the database
     * @return an ArrayList of all the subscriptions of the contract
     */
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

    /**
     * This method Overrides the createContact method in ContactService
     * It creates a contact in the database
     * @throws ContactException if the contact already exists
     */
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
