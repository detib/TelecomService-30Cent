package CRM;

import CRM.Service.Data;
import CRM.Service.SMS;
import CRM.Service.SimCard;
import CRM.Service.Voice;
import Util.ID;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
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

    public Product(SimCard simCard, SMS sms, Voice voice, Data data,
                   LocalDate fromDate, LocalDate toDate, String productName) {
        this.id = ID.PRODUCT.createId();
        this.simCard = simCard;
        this.sms = sms;
        this.voice = voice;
        this.data = data;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.price = (simCard.getCredits() + sms.getFullPrice() + voice.getFullPrice() + data.getFullPrice());
        this.productName = productName;
    }

    public Product(String id, SimCard simCard, SMS sms, Voice voice, Data data,
                   LocalDate fromDate, LocalDate toDate, Integer price, String productName) {
        this.id = id;
        this.simCard = simCard;
        this.sms = sms;
        this.voice = voice;
        this.data = data;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.price = price;
        this.productName = productName;
    }

}
