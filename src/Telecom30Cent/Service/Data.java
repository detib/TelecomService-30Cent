package Telecom30Cent.Service;

import lombok.Getter;

public class Data implements ServiceType {
    private final Integer price = 2; // cents
    @Getter
    private final Integer fullPrice;
    @Getter
    private Integer mB;

    public Data(Integer mB) {
        this.mB = mB;
        this.fullPrice = mB * price;
    }

    @Override
    public void spendAmount(Integer amount) {
        this.mB -= amount;
    }

    @Override
    public void addAmount(Integer amount) {
        this.mB += amount;
    }

    @Override
    public String getTypeAmount() {
        return "DAT_" + getMB();
    }
    @Override
    public String toString() {
        return String.format("DATA_%d", mB);
    }
}
