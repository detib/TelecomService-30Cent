package CRM.Service;

public class Voice implements ServiceType {
    private int minutes;

    public Voice(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public void spendAmount(Integer amount) {
        this.minutes -= amount;
    }

    @Override
    public void addAmount(Integer amount) {
        this.minutes += amount;
    }
}
