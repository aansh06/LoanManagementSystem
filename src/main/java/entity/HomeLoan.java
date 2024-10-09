package entity;

public class HomeLoan extends Loan {

    private String propertyAddress;
    private int propertyValue;
    private boolean firstTimeBuyer;
    private double homeArea;

    public HomeLoan() {
        super();
        this.propertyAddress = "Unknown";
        this.propertyValue = 0;
        this.firstTimeBuyer = false;
        this.homeArea = 0;
    }

    public HomeLoan(int loanId, Customer customer, double principalAmount, double interestRate, int loanTerm, String loanType, String loanStatus, String propertyAddress, int propertyValue, boolean firstTimeBuyer, double homeArea) {
        super(loanId, customer, principalAmount, interestRate, loanTerm, "Home Loan", loanStatus);
        this.propertyAddress = propertyAddress;
        this.propertyValue = propertyValue;
        this.firstTimeBuyer = firstTimeBuyer;
        this.homeArea = homeArea;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public int getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(int propertyValue) {
        this.propertyValue = propertyValue;
    }

    public boolean isFirstTimeBuyer() {
        return firstTimeBuyer;
    }

    public void setFirstTimeBuyer(boolean firstTimeBuyer) {
        this.firstTimeBuyer = firstTimeBuyer;
    }

    public double getHomeArea() {
        return homeArea;
    }

    public void setHomeArea(double homeArea) {
        this.homeArea = homeArea;
    }

    @Override
    public String toString() {
        return "HomeLoan{" +
                "propertyAddress='" + propertyAddress + '\'' +
                ", propertyValue=" + propertyValue +
                ", firstTimeBuyer=" + firstTimeBuyer +
                ", homeArea=" + homeArea +
                '}';
    }
}
