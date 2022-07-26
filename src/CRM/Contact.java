package CRM;

import CRM.Enum.Gender;
import CRM.Enum.STATE;
import Database.TelecomService;
import Util.ID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contact {
    @EqualsAndHashCode.Include
    private final String id;
    private String name;
    private String lastname;
    private Gender gender;
    private LocalDate dob;
    private final ID idType;
    private final LocalDate createdDate;
    @Setter
    private STATE state;
    private String customerName;

    public Contact(ID idType) { // Contacts
        this.id = ID.CONTACT.createId();
        this.idType = idType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
    }
    public Contact(String id, ID idType, LocalDate createdDate, STATE state) { // Contacts
        this.id = id;
        this.idType = idType;
        this.createdDate = createdDate;
        this.state = state;
    }



    public Contact(ID idType, String name, String lastname, Gender gender, LocalDate dob) { // Individual
        this(idType);
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.dob = dob;
    }

    public Contact(ID idType, String customerName) { // business constructor
        this(idType);
        this.customerName = customerName;
    }

    public Contact(String id, ID idType, LocalDate createdDate, STATE state, String customerName) {
        this(id, idType, createdDate, state);
        this.customerName = customerName;
    }

    public Contact(String id, String name, String lastname, Gender gender, LocalDate dob, ID idType, LocalDate createdDate, STATE state) {
        this(id, idType, createdDate, state);
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.dob = dob;
    }
}
