package CRM;

import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;

@Getter
public abstract class Customer {
    private final String id;
    private final LocalDate createdDate;
    private STATE state;
    private ArrayList<Contract> contracts;

    public Customer() {
    }

}
