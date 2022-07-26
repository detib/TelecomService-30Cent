package CRM.Service;

import CRM.Enum.STATE;
import Util.ID;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class Service {
    private final String id;
    private ServiceType serviceType;
    private final LocalDate createdDate;
    @Setter
    private STATE state;

    public Service(ServiceType serviceType) {
        this.id = ID.SERVICE.createId();
        this.serviceType = serviceType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
    }

    public Service(String id, ServiceType serviceType, LocalDate createdDate, STATE state) {
        this.id = id;
        this.serviceType = serviceType;
        this.createdDate = createdDate;
        this.state = state;
    }


}
