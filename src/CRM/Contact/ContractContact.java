package CRM.Contact;

import Database.TelecomService;
import Util.ID;

public abstract class ContractContact extends Contact {
    public ContractContact(ID idType) {
        super(idType);
    }
}
