package dao;

import entity.Loan;
import exception.InvalidLoanException;

import java.util.List;

public interface ILoanRepository {

        // a. Apply for a loan
        boolean applyLoan(Loan loan) throws InvalidLoanException;

        // b. Calculate interest
        double calculateInterest(int loanId) throws InvalidLoanException;

        // Overloaded calculateInterest
        double calculateInterest(double principalAmount, double interestRate, int loanTerm);

        // c. Check  loan status (Approved/Rejected)
        void loanStatus(int loanId) throws InvalidLoanException;

        // d. Calculate EMI
        double calculateEMI(int loanId) throws InvalidLoanException;

        // Overloaded calculateEMI
        double calculateEMI(double principalAmount, double interestRate, int loanTerm);

        // e. Repay the loan by specifying loanId and amount;
        void loanRepayment(int loanId, double amount) throws InvalidLoanException;

        // f. Return all loans
        List<Loan> getAllLoan();

        // g. Return a loan by its ID
        Loan getLoanById(int loanId) throws InvalidLoanException;


}
