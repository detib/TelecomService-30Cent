package CRM;

import CRM.Enum.STATE;
import Util.ID;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

@Getter
public abstract class Customer {
    private final String id;
    private final LocalDate createdDate;
    @Setter
    private STATE state;

    private ArrayList<Contract> contracts;

    public Customer() {
        this.id = ID.CUSTOMER.createId();
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
    }


}
