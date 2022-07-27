package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
import CRM.Exceptions.ServiceException;
import CRM.Service.Service;
import CRM.Service.ServiceType;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;
import Util.PhoneNumber;
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
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Subscription implements TelecomService<Service> {
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
                    "INSERT INTO services VALUES('%s', '%s', '%s', '%s', '%s');",
                    object.getId(), object.getServiceType(), object.getCreatedDate(), object.getState()));
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
        return false;
    }

    @Override
    public Optional<Service> findById(String id) {
        for (Service service : services){
            if (service.getId().equals(id)){
                return Optional.of(service);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Service> findAll() {
//        try{
//            Connection conn = DatabaseConn.getInstance().getConnection();
//            return conn.createStatement().executeQuery("SELECT * FROM Service");
//        } catch (SQLException e){
//            throw new RuntimeException(e);
//        }
        return null;
    }

}
