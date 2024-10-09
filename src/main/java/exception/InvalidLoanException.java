package exception;

public class InvalidLoanException extends Exception {

    public InvalidLoanException() {
        super("Invalid Loan Operation");
    }

    public InvalidLoanException(String message) {
        super(message);
    }


}
