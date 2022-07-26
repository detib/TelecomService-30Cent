package Telecom30Cent;

import Telecom30Cent.Enum.ContractType;
import Telecom30Cent.Service.Data;
import Telecom30Cent.Service.SMS;
import Telecom30Cent.Service.SimCard;
import Telecom30Cent.Service.Voice;
import Util.ID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@ToString(includeFieldNames = false)
public class Product {
    private final String id;
    private SimCard simCard;
    private SMS sms;
    private Voice voice;
    private Data data;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    @Setter
    private Integer price;
    private String productName;
    private ContractType contractType;

    public Product(SimCard simCard, SMS sms, Voice voice, Data data,
                   LocalDate fromDate, LocalDate toDate, String productName, ContractType contractType) {
        this.id = ID.PRODUCT.createId();
        this.simCard = simCard;
        this.sms = sms;
        this.voice = voice;
        this.data = data;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.price = (simCard.getCredits() + sms.getFullPrice() + voice.getFullPrice() + data.getFullPrice());
        this.productName = productName;
        this.contractType = contractType;
    }

    public Product(String id, SimCard simCard, SMS sms,
                   Voice voice, Data data, LocalDate fromDate,
                   LocalDate toDate, Integer price, String productName,
                   ContractType contractType) {
        this.id = id;
        this.simCard = simCard;
        this.sms = sms;
        this.voice = voice;
        this.data = data;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.price = price;
        this.productName = productName;
        this.contractType = contractType;
    }

}
