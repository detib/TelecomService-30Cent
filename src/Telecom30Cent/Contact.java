package Telecom30Cent;

import Telecom30Cent.Enum.Gender;
import Telecom30Cent.Enum.STATE;
import Util.ID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString(includeFieldNames = false)
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

    /**
     * Contact constructor to create a new contact
     * @param idType ID type
     */
    public Contact(ID idType) {
        this.id = ID.CONTACT.createId();
        this.idType = idType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
    }

    /**
     * Contact constructor to create a new contact from database
     * @param id ID
     * @param idType ID type
     * @param createdDate  created date
     * @param state state
     */
    public Contact(String id, ID idType, LocalDate createdDate, STATE state) {
        this.id = id;
        this.idType = idType;
        this.createdDate = createdDate;
        this.state = state;
    }

    /**
     * Contact constructor for the Individual Customer
     * @param idType ID type
     * @param name name
     * @param lastname lastname
     * @param gender gender
     * @param dob date of birth
     */
    public Contact(ID idType, String name, String lastname, Gender gender, LocalDate dob) {
        this(idType);
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.dob = dob;
    }

    /**
     * Contact constructor for the Business Customer
     */
    public Contact(ID idType, String customerName) {
        this(idType);
        this.customerName = customerName;
    }

    /**
     * Contact constructor for the Business Customer from the database
     * @param id ID
     * @param idType ID type
     * @param createdDate created date
     * @param state state
     * @param customerName customer name
     */
    public Contact(String id, ID idType, LocalDate createdDate, STATE state, String customerName) {
        this(id, idType, createdDate, state);
        this.customerName = customerName;
    }

    /**
     * Contact constructor individual from the database
     * @param id ID
     * @param name name
     * @param lastname lastname
     * @param gender gender
     * @param dob date of birth
     * @param idType ID type
     * @param createdDate Created date
     * @param state state
     */
    public Contact(String id, String name, String lastname, Gender gender, LocalDate dob, ID idType, LocalDate createdDate, STATE state) {
        this(id, idType, createdDate, state);
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.dob = dob;
    }
}
