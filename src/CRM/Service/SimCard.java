package CRM.Service;

import lombok.Getter;

public class SimCard implements ServiceType {
    @Getter
    private Integer credits; // cents

    public SimCard(Integer credits) {
        this.credits = credits;
    }

    @Override
    public void spendAmount(Integer amount) {
        this.credits -= amount;
    }

    @Override
    public void addAmount(Integer amount) {
        this.credits += amount;
    }

    @Override
    public String getTypeAmount() {
        return "SIM_" + getCredits();
    }
}
