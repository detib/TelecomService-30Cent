package CRM.Service;

import lombok.Getter;

public class Voice implements ServiceType {
    @Getter
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
