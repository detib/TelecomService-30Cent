package CRM.Service;

public class Data implements ServiceType {
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
}
