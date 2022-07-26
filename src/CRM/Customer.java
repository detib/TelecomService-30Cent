package CRM;

import CRM.Enum.STATE;
import Database.TelecomService;
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
public abstract class Customer implements TelecomService {
    @EqualsAndHashCode.Include
    private final String id;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    // @TODO change setter

    @ToString.Exclude
    private ArrayList<Contract> contracts;

    public Customer() {
        this.id = ID.CUSTOMER.createId();
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
    }

    public boolean addContract(Contract cr) {
        return contracts.add(cr);
    }

    // @TODO finish
    public Contract removeContract(ID id) {
        return contracts.remove(1);
    }

}
