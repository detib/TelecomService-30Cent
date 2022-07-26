package Telecom30Cent.Service;

import lombok.Getter;

@Getter
public class Voice implements ServiceType {
    private final Integer price = 10; // cents
    private final Integer fullPrice;
    private Integer minutes;

    public Voice(Integer minutes) {
        this.minutes = minutes;
        this.fullPrice = minutes * price;
    }

    @Override
    public void spendAmount(Integer amount) {
        this.minutes -= amount;
    }

    @Override
    public void addAmount(Integer amount) {
        this.minutes += amount;
    }

    @Override
    public String getTypeAmount() {
        return "VOI_" + getMinutes();
    }

    @Override
        public String toString (){
            return String.format("VOICE_%d", minutes);
    }
}
