package Telecom30Cent.Service;

import Telecom30Cent.Enum.STATE;
import Util.ID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@ToString(callSuper = false)
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
