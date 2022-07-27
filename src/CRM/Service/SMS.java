package CRM.Service;

import lombok.Getter;

@Getter
public class SMS implements ServiceType {
    private final Integer price = 4; //Cents
    private final Integer fullPrice;
    private Integer messages;


    public SMS(Integer messages) {
        this.messages = messages;
        this.fullPrice = messages * price;
    }

    @Override
    public void spendAmount(Integer amount) {
        this.messages -= amount;
    }

    @Override
    public void addAmount(Integer amount) {
        this.messages += amount;
    }

    @Override
    public String getTypeAmount() {
        return "SMS_" + getMessages();
    }
}
