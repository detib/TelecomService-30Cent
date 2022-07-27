package CRM.Service;

import lombok.Getter;
@Getter
public class SimCard implements ServiceType {
    private final Integer price = 300; // 300 Cents
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
