package CRM.Service;

import lombok.Getter;
@Getter
public class SimCard implements ServiceType {
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

    @Override
    public String toString (){
        return String.format("SIMCARD_%d", credits);
    }
}
