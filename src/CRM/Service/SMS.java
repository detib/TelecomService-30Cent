package CRM.Service;

public class SMS implements ServiceType {
    private Integer messages;

    public SMS(Integer messages) {
        this.messages = messages;
    }

    @Override
    public void spendAmount(Integer amount) {
        this.messages -= amount;
    }

    @Override
    public void addAmount(Integer amount) {
        this.messages += amount;
    }
}
