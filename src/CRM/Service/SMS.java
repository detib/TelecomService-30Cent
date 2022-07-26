package CRM.Service;

public class SMS implements ServiceType {
    private int credits;

    @Override
    public int spendAmount() {
        return 0;
    }

    @Override
    public int buyAmount() {
        return 0;
    }
}
