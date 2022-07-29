package Telecom30Cent;

import Telecom30Cent.Enum.STATE;
import Telecom30Cent.Enum.ServiceEnum;
import Telecom30Cent.Exceptions.ContactException;
import Telecom30Cent.Exceptions.ProductExpiredException;
import Telecom30Cent.Exceptions.ServiceException;
import Telecom30Cent.Exceptions.ServiceExistsException;
import Telecom30Cent.Service.*;
import Database.ContactService;
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
import java.util.HashSet;
import java.util.Optional;

@Getter
@ToString(doNotUseGetters = true, includeFieldNames = false)
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
        this.services = findAll();
    }

    @Override
    public boolean create(Service object) throws ServiceException, ServiceExistsException {
//        if(services == null) this.services = findAll();
        for (Service service : services) {
             if (object.getServiceType().getClass() == service.getServiceType().getClass()) {
                 throw new ServiceExistsException("Service already exists");
             }
        }
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "INSERT INTO service VALUES('%s', '%s', '%s', '%s', '%s');",
                    object.getId(), object.getServiceType().getTypeAmount(),
                    object.getCreatedDate(), object.getState(), id));
            return services.add(object);
        } catch (SQLException e){
            throw new ServiceException("Cannot add a Service to the database!");
        }
    }

    @Override
    public boolean update(Service object) {
        try {
            Connection conn = DatabaseConn.getInstance().getConnection();
            conn.createStatement().execute(String.format(
                    "UPDATE service SET serviceType = '%s' WHERE SeID = '%s';",
                    object.getServiceType().getTypeAmount() , object.getId()));
            this.services = findAll();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Service object) {
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

    private boolean checkIfItHasData() {
        for (Service service : services) {
            if(service.getServiceType() instanceof Data) return true;
        }
        return false;
    }

    private boolean checkIFItHasSMS() {
        for (Service service : services) {
            if(service.getServiceType() instanceof SMS) return true;
        }
        return false;
    }

    private HashSet<ServiceEnum> check(HashSet<ServiceEnum> set) {
        if(checkIfItHasData()) set.add(ServiceEnum.DATA);
        if(checkIFItHasSMS()) set.add(ServiceEnum.SMS);
        return set;
    }

    private boolean checkServicesForProduct(Product prod) {
        Integer mb = prod.getData().getMB();
        Integer messages = prod.getSms().getMessages();
        HashSet<ServiceEnum> set = new HashSet<>();
        if(mb > 0) set.add(ServiceEnum.DATA);
        if(messages > 0) set.add(ServiceEnum.SMS);
        return check(new HashSet<>()).containsAll(set);
    }

    public boolean buyProduct(Product prod) throws ServiceException, ProductExpiredException {
        if(prod.getToDate().isBefore(LocalDate.now())) {
            throw new ProductExpiredException();
        }
        if(checkServicesForProduct(prod)) {
            try {
                Connection conn = DatabaseConn.getInstance().getConnection();
                conn.createStatement().execute(
                        String.format(
                                "Insert into sales values('%s', '%s')",
                                this.id, prod.getId()
                        ));
                for (Service service : services) {
                    if(service.getServiceType() instanceof Data) service.getServiceType().addAmount(prod.getData().getMB());
                    if(service.getServiceType() instanceof SMS) service.getServiceType().addAmount(prod.getSms().getMessages());
                    if(service.getServiceType() instanceof Voice) service.getServiceType().addAmount(prod.getVoice().getMinutes());
                    if(service.getServiceType() instanceof SimCard) service.getServiceType().addAmount(prod.getSimCard().getCredits());
                    update(service);
                }
                return true;
            } catch (SQLException e) {
                throw new RuntimeException("Subscription: Cannot add a product!" + e.getMessage());
            }
        }
        return false;
    }
}
