package CRM.Service;

import lombok.Getter;

public class Data implements ServiceType {
    @Getter
    private int kB;

    public Data(int kB) {
        this.kB = kB;
    }

    @Override
    public void spendAmount(Integer amount) {
        this.kB -= amount;
    }

    @Override
    public void addAmount(Integer amount) {
        this.kB += amount;
    }

    @Override
    public String getTypeAmount() {
        return "DAT_" + getKB();
    }
}
