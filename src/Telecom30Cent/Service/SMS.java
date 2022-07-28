package Telecom30Cent.Service;

import lombok.Getter;

@Getter
public class SMS implements ServiceType {
    private final Integer price = 4; //Cents
    private final Integer fullPrice;
    private Integer messages;


    /*
        Constructor for SMS
    */
    public SMS(Integer messages) {
        this.messages = messages;
        this.fullPrice = messages * price;
    }

    /*
        Overrides the spendAmount method from the ServiceType interface
        It accepts an integer amount, and subtracts it from the messages variable
     */
    @Override
    public void spendAmount(Integer amount) {
        this.messages -= amount;
    }

    /*
      Overrides the addAmount method from the ServiceType interface
        It accepts an integer amount, and adds it to the messages variable
    */
    @Override
    public void addAmount(Integer amount) {
        this.messages += amount;
    }

    /*
        Overrides the getTypeAmount method from the ServiceType interface
        It returns a String containing the type of service and the amount of messages
     */
    @Override
    public String getTypeAmount() {
        return "SMS_" + getMessages();
    }

    /*
        Overrides the toString method and returns a String with the format:
        SMS_<amount of messages>
     */
    @Override
    public String toString (){
        return String.format("SMS_%d", messages);
    }
}
