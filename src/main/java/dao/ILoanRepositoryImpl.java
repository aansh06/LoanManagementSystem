package dao;

import entity.Customer;
import entity.Loan;
import exception.InvalidLoanException;
import util.DBConnUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ILoanRepositoryImpl implements ILoanRepository{
    private Connection getConnection() throws SQLException {
        return DBConnUtil.getDBConn();
    }

    // a. Apply for a loan
    @Override
    public boolean applyLoan(Loan loan, int custid) throws InvalidLoanException {
        boolean isApplied = false;
        String sql = "INSERT INTO Loan ( customerId, principalAmount, interestRate, loanTerm, loanType, loanStatus) VALUES ( ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

//            ps.setInt(1, loan.getLoanId());
            ps.setInt(1, custid) ;
            ps.setDouble(2, loan.getPrincipalAmount());
            ps.setDouble(3, loan.getInterestRate());
            ps.setInt(4, loan.getLoanTerm());
            ps.setString(5, loan.getLoanType());
            ps.setString(6, "Pending");

            // Confirm loan application is done inside applyForLoan method
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                isApplied = true;
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Error applying for loan: " + e.getMessage());
        }

        return isApplied;
    }


    // b. Calculate interest for the loan by loanId
    @Override
    public double calculateInterest(int loanId) throws InvalidLoanException {
        String sql = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
        double interest = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double principalAmount = rs.getDouble("principalAmount");
                    double interestRate = rs.getDouble("interestRate");
                    int loanTerm = rs.getInt("loanTerm");

                    interest = (principalAmount * interestRate * loanTerm) / 12;
                } else {
                    throw new InvalidLoanException("Loan not found with ID: " + loanId);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Error calculating interest: " + e.getMessage());
        }

        return interest;
    }


    // Overloaded method to calculate interest based on parameters during loan creation
    @Override
    public double calculateInterest(double principalAmount, double interestRate, int loanTerm) {
        return (principalAmount * interestRate * loanTerm) / 12;
    }

    // c. Check loan status based on credit score
    @Override
    public void loanStatus(int loanId) throws InvalidLoanException {
        String sql = "SELECT c.creditScore, l.loanStatus FROM Loan l JOIN Customer c ON l.customerId = c.customerId WHERE l.loanId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int creditScore = rs.getInt("creditScore");
                    String status = (creditScore > 650) ? "Approved" : "Rejected";

                    // Update loan status
                    String updateSql = "UPDATE Loan SET loanStatus = ? WHERE loanId = ?";
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setString(1, status);
                        updatePs.setInt(2, loanId);
                        updatePs.executeUpdate();
                        System.out.println("Loan status updated to: " + status);
                    }
                } else {
                    throw new InvalidLoanException("Loan not found with ID: " + loanId);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Error checking loan status: " + e.getMessage());
        }
    }


    // d. Calculate EMI for the loan by loanId
    @Override
    public double calculateEMI(int loanId) throws InvalidLoanException {
        String sql = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
        double emi = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double principalAmount = rs.getDouble("principalAmount");
                    double interestRate = rs.getDouble("interestRate");
                    int loanTerm = rs.getInt("loanTerm");

                    // EMI formula: [P * R * (1+R)^N] / [(1+R)^N - 1]
                    double r = (interestRate / 12) / 100;
                    emi = (principalAmount * r * Math.pow(1 + r, loanTerm)) / (Math.pow(1 + r, loanTerm) - 1);
                } else {
                    throw new InvalidLoanException("Loan not found with ID: " + loanId);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Error calculating EMI: " + e.getMessage());
        }

        return emi;
    }


    // Overloaded method to calculate EMI based on parameters during loan creation
    @Override
    public double calculateEMI(double principalAmount, double r, int loanTerm) {
//        double r = (interestRate / 12) / 100;
        return (principalAmount * r * Math.pow(1 + r, loanTerm)) / (Math.pow(1 + r, loanTerm) - 1);
    }


    // e. Loan repayment: Pay EMI if the amount covers the EMI or reject
    @Override
    public void loanRepayment(int loanId, double amount) throws InvalidLoanException {
        String sql = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double principalAmount = rs.getDouble("principalAmount");
                    double interestRate = rs.getDouble("interestRate") / 100 / 12;
                    int loanTerm = rs.getInt("loanTerm");

                    double emiAmount = calculateEMI(principalAmount, interestRate, loanTerm);

                    if (amount < emiAmount) {
                        throw new InvalidLoanException("Insufficient amount to cover EMI payment.");
                    } else {
                        System.out.println("EMI paid successfully. Remaining balance updated.");
                        updateLoanBalance(loanId, amount); }
                } else {
                    throw new InvalidLoanException("Loan not found with ID: " + loanId);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Error processing repayment: " + e.getMessage());
        }
    }

    private void updateLoanBalance(int loanId, double paymentAmount) throws SQLException {

        String sql = "UPDATE Loan SET principalAmount = principalAmount - ? WHERE loanId = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, paymentAmount);
            ps.setInt(2, loanId);
            ps.executeUpdate();
        }
    }


    // f. Retrieve and return all loans from the database
    @Override
    public List<Loan> getAllLoan() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.loanId, l.customerId, l.principalAmount, l.interestRate, l.loanTerm, l.loanType, l.loanStatus, c.name, c.emailAddress, c.phoneNumber, c.address, c.creditScore FROM Loan l JOIN Customer c ON l.customerId = c.customerId";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loanId"));
                loan.setPrincipalAmount(rs.getDouble("principalAmount"));
                loan.setInterestRate(rs.getDouble("interestRate"));
                loan.setLoanTerm(rs.getInt("loanTerm"));
                loan.setLoanType(rs.getString("loanType"));
                loan.setLoanStatus(rs.getString("loanStatus"));

                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customerId"));
                customer.setName(rs.getString("name"));
                customer.setEmailAddress(rs.getString("emailAddress"));
                customer.setPhoneNumber(rs.getString("phoneNumber"));
                customer.setAddress(rs.getString("address"));
                customer.setCreditScore(rs.getInt("creditScore"));

                loan.setCustomer(customer);

                loans.add(loan);

            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all loans: " + e.getMessage());
        }

        return loans;
    }


    // g. Retrieve and return a loan by its ID
    @Override
    public Loan getLoanById(int loanId) throws InvalidLoanException {
        String sql = "SELECT * FROM Loan WHERE loanId = ?";
        Loan loan = null;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    loan = new Loan();
                    loan.setLoanId(rs.getInt("loanId"));
                    loan.setPrincipalAmount(rs.getDouble("principalAmount"));
                    loan.setInterestRate(rs.getDouble("interestRate"));
                    loan.setLoanTerm(rs.getInt("loanTerm"));
                    loan.setLoanType(rs.getString("loanType"));
                    loan.setLoanStatus(rs.getString("loanStatus"));
                    int customerId = rs.getInt("customerId");
                    loan.setCustomer(getCustomerById(customerId));
                } else {
                    throw new InvalidLoanException("Loan not found with ID: " + loanId);
                }
            }
        } catch (SQLException e) {
            throw new InvalidLoanException("Error retrieving loan by ID: " + e.getMessage());
        }

        return loan;
    }

    @Override
    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE customerId = ?";
        Customer customer = null;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setCustomerId(rs.getInt("customerId"));
                    customer.setName(rs.getString("name"));
                    customer.setEmailAddress(rs.getString("emailAddress"));
                    customer.setPhoneNumber(rs.getString("phoneNumber"));
                    customer.setAddress(rs.getString("address"));
                    customer.setCreditScore(rs.getInt("creditScore"));
                }
            }
        }

        return customer;
    }

    @Override
    public int addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer ( name, emailAddress, phoneNumber, address, creditScore) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

//            ps.setInt(1, customer.getCustomerId());
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmailAddress());
            ps.setString(3, customer.getPhoneNumber());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getCreditScore());

            int r=ps.executeUpdate();

            if (r == 0) {
                throw new SQLException("Inserting customer failed, no rows affected.");
            }

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return the generated customerId
            } else {
                throw new SQLException("Inserting customer failed, no customerId obtained.");
            }
        }
    }


}
