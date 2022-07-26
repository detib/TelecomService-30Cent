package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
import CRM.Service.Service;
import Util.ID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;

@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Subscription {
    private final String id;
    private ContractType contractType;
    private final LocalDate createdDate;

    @Setter
    private STATE state;
    @ToString.Exclude
    private ArrayList<Service> services;
    @Setter
    private boolean mobileApp;

    public Subscription(ContractType contractType, boolean mobileApp) {
        this.id = ID.SUBSCRIPTION.createId();
        this.contractType = contractType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.mobileApp = mobileApp;
    }
}
