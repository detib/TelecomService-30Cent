package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
import CRM.Exceptions.ContactException;
import CRM.Exceptions.ServiceException;
import CRM.Service.*;
import Database.ContactService;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;
import Util.PhoneNumber;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Subscription implements TelecomService<Service>, ContactService {
    private final String id;
    private final String phoneNumber;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    private Contact contact;
    @ToString.Exclude
    private ArrayList<Service> services;

    public Subscription(Contact contact, PhoneNumber phoneNumber) {
        this.id = ID.SUBSCRIPTION.createId();
        this.phoneNumber = phoneNumber.generateNumber();
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.contact = contact;
    }

    public Subscription(String id,String phoneNumber, LocalDate createdDate, STATE state, Contact contact) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.createdDate = createdDate;
        this.state = state;
        this.contact = contact;
    }

    @Override
    public boolean create(Service object) throws ServiceException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "INSERT INTO service VALUES('%s', '%s', '%s', '%s', '%s');",
                    object.getId(), object.getServiceType().getTypeAmount(),
                    object.getCreatedDate(), object.getState(), id));
            if(services == null) this.services = new ArrayList<>();
            return services.add(object);
        } catch (SQLException e){
            throw new ServiceException("Cannot add a Service to the database!");
        }
    }

    @Override
    public boolean update(Service object) {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean delete(Service object) {
//        if(object.getServiceType() instanceof SimCard) return false;
//        if(object.getServiceType() instanceof Voice) return false;
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format("DELETE FROM service where SeID='%s'", object.getId()));
            services.remove(object);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Service> findById(String id) {
        this.services = findAll();
        for (Service service : services){
            if (service.getId().equals(id)){
                return Optional.of(service);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Service> findAll() {
        try{
            Connection conn = DatabaseConn.getInstance().getConnection();
            ResultSet servicesResultSet = conn.createStatement().executeQuery(
                    String.format("SELECT * FROM SERVICE WHERE SuID='%s';", this.id));
            ArrayList<Service> allServices = new ArrayList<>();
            while (servicesResultSet.next()){
                String id = servicesResultSet.getString("SeID");
                String type = servicesResultSet.getString("serviceType");
                LocalDate createdDate = LocalDate.parse(servicesResultSet.getString("createdDate"));
                STATE state = STATE.valueOf(servicesResultSet.getString("state"));
                String[] service = type.split("_");
                switch (service[0]) {
                    case "SIM" -> allServices.add(new Service(id, new SimCard(Integer.parseInt(service[1])), createdDate, state));
                    case "VOI" -> allServices.add(new Service(id, new Voice(Integer.parseInt(service[1])), createdDate, state));
                    case "DAT" -> allServices.add(new Service(id, new Data(Integer.parseInt(service[1])), createdDate, state));
                    case "SMS" -> allServices.add(new Service(id, new SMS(Integer.parseInt(service[1])), createdDate, state));
                }
            }
            return allServices;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createContact() throws ContactException {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(
                    String.format(
                            "INSERT INTO contact(CtID, IdType, CreatedDate, State)" +
                                    "VALUES('%s', '%s', '%s', '%s')",
                                contact.getId(), contact.getIdType(),
                                contact.getCreatedDate(), contact.getState()
                            )
            );
        } catch (SQLException e) {
            throw new ContactException("Subscription: Cannot create contact:  "+ e.getMessage());
        }
    }
}
