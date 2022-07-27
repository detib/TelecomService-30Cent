package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
import CRM.Service.Service;
import Database.DatabaseConn;
import Database.TelecomService;
import Util.ID;
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
public class Subscription implements TelecomService<Service>{
    private final String id;
    @Setter
    private String phoneNumber;
    private ContractType contractType;
    private final LocalDate createdDate;

    @Setter
    private STATE state;
    @ToString.Exclude
    private ArrayList<Service> services;

    public Subscription(ContractType contractType, String phoneNumber) {
        this.id = ID.SUBSCRIPTION.createId();
        this.contractType = contractType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.phoneNumber = phoneNumber;
    }

    public Subscription(String id, ContractType contractType,
                        LocalDate createdDate, STATE state, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.contractType = contractType;
        this.createdDate = createdDate;
        this.state = state;
    }

    @Override
    public boolean create(Service object) throws Exception {
        try{
            Connection conn = DatabaseConn.getInstance().getConnection();
            boolean success = conn.createStatement().execute(String.format(
                    "INSERT INTO services VALUES('%s', '%s', '%s', '%s', '%s');",
                    object.getId(), object.getServiceType(), object.getCreatedDate(), object.getState()));
            if (success) return services.add(object); else return false;
        } catch (SQLException e){
            throw new SQLException("Cannot add a Service to the database!");
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
