package entity;

public class CarLoan extends Loan {
    private String carModel;
    private int carValue;
    private int manufacturingYear;
    private String carType;

    public CarLoan() {
        super();
        this.carModel = "Unknown";
        this.carValue = 0;
        this.manufacturingYear = 0;
        this.carType = "Unknown";
    }

    public CarLoan(int loanId, Customer customer, double principalAmount, double interestRate, int loanTerm, String carModel, int carValue, int manufacturingYear, String carType) {
        super(loanId, customer, principalAmount, interestRate, loanTerm, "Car Loan", "Pending");
        this.carModel = carModel;
        this.carValue = carValue;
        this.manufacturingYear = manufacturingYear;
        this.carType = carType;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getCarValue() {
        return carValue;
    }

    public void setCarValue(int carValue) {
        this.carValue = carValue;
    }

    public int getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(int manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    @Override
    public String toString() {
        return "CarLoan{" +
                "carModel='" + carModel + '\'' +
                ", carValue=" + carValue +
                ", manufacturingYear=" + manufacturingYear +
                ", carType='" + carType + '\'' +
                '}';
    }
}
