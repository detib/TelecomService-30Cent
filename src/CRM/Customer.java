package CRM;

import CRM.Enum.CustomerType;
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
public class Customer {
    @EqualsAndHashCode.Include
    private final String id;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    private CustomerType customerType;
    private Contact contact;
    @ToString.Exclude
    private ArrayList<Contract> contracts;

    public Customer(CustomerType customerType, Contact contact) {
        this.id = ID.CUSTOMER.createId();
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
        this.customerType = customerType;
        this.contact = contact;
    }

    public boolean addContract(Contract cr) {
        return contracts.add(cr);
    }

    // @TODO finish
    public Contract removeContract(ID id) {
        return contracts.remove(1);
    }

}
