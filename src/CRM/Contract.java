package CRM;

import CRM.Enum.ContractType;
import CRM.Enum.STATE;
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
public class Contract {
    private final String id;
    private ContractType contractType;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    @ToString.Exclude
    private ArrayList<Subscription> subscription;

    public Contract(ContractType contractType) {
        this.id = ID.CONTRACT.createId();
        this.contractType = contractType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
    }

}
