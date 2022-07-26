package CRM.Contact;

import CRM.Enum.STATE;
import Database.TelecomService;
import Util.ID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Contact implements TelecomService {
    @EqualsAndHashCode.Include
    private final String id;
    private final ID idType;
    private final LocalDate createdDate;
    private STATE state;

    public Contact(ID idType) {
        this.id = ID.CONTACT.createId();
        this.idType = idType;
        this.createdDate = LocalDate.now();
        this.state = STATE.ACTIVE;
    }
}
