package org.example;
import dao.ILoanRepository;
import dao.ILoanRepositoryImpl;
import entity.Customer;
import entity.HomeLoan;
import entity.CarLoan;
import entity.Loan;
import exception.InvalidLoanException;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ILoanRepository loanRepository = new ILoanRepositoryImpl();

    public static void main(String[] args) throws InvalidLoanException, SQLException {
        while (true) {
            System.out.println("Welcome to the Loan Management System");
            System.out.println("1 Apply for a Loan");
            System.out.println("2 Get All Loans");
            System.out.println("3 Get Loan by ID");
            System.out.println("4 Make Loan Repayment");
            System.out.println("5 Exit");
            System.out.print("Please enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {
                case 1:
                    applyForLoan();
                    break;
                case 2:
                    getAllLoans();
                    break;
                case 3:
                    getLoanById();
                    break;
                case 4:
                    makeLoanRepayment();
                    break;
                case 5:
                    System.out.println("Exiting the system. Thank you!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void applyForLoan() throws InvalidLoanException, SQLException {
        System.out.print("Enter Customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();


        Customer existingCustomer = loanRepository.getCustomerById(customerId);
        if (existingCustomer == null) {
            System.out.print("Customer not found. Would you like to create a new customer? (Yes/No): ");
            String createNewCustomer = scanner.nextLine();
            if (createNewCustomer.equalsIgnoreCase("Yes")) {
                System.out.print("Enter Customer Name: ");
                String name = scanner.nextLine();

                System.out.print("Enter Customer Email: ");
                String email = scanner.nextLine();

                System.out.print("Enter Customer Phone: ");
                String phone = scanner.nextLine();

                System.out.print("Enter Customer Address: ");
                String address = scanner.nextLine();

                System.out.print("Enter Customer Credit Score: ");
                int creditScore = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Create new customer and save to database
                existingCustomer = new Customer(customerId, name, email, phone, address, creditScore);
                customerId = loanRepository.addCustomer(existingCustomer);
            } else {
                System.out.println("Loan application cancelled.");
                return;
            }
        }

        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();

        System.out.print("Enter Loan Amount: ");
        double principalAmount = scanner.nextDouble();

        System.out.print("Enter Interest Rate: ");
        double interestRate = scanner.nextDouble();

        System.out.print("Enter Loan Term (in months): ");
        int loanTerm = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Loan Type (Home/Car): ");
        String loanType = scanner.nextLine();

        Loan loan;
        if (loanType.equalsIgnoreCase("Home")) {
            System.out.print("Enter Property Address: ");
            String propertyAddress = scanner.nextLine();

            System.out.print("Enter Property Value: ");
            int propertyValue = scanner.nextInt();

            System.out.print("Is First Time Buyer? (true/false): ");
            boolean firstTimeBuyer = scanner.nextBoolean();

            System.out.print("Enter Home Area (in sqft): ");
            double homeArea = scanner.nextDouble();

            loan = new HomeLoan(loanId, existingCustomer, principalAmount, interestRate, loanTerm, propertyAddress, propertyValue, firstTimeBuyer, homeArea);
        } else if (loanType.equalsIgnoreCase("Car")) {
            System.out.print("Enter Car Model: ");
            String carModel = scanner.nextLine();

            System.out.print("Enter Car Value: ");
            int carValue = scanner.nextInt();

            System.out.print("Enter Manufacturing Year: ");
            int manufacturingYear = scanner.nextInt();

            System.out.print("Enter Car Type: ");
            String carType = scanner.next();

            loan = new CarLoan(loanId, existingCustomer, principalAmount, interestRate, loanTerm, carModel, carValue, manufacturingYear, carType);
        } else {
            System.out.println("Invalid loan type.");
            return;
        }

        System.out.print("Confirm to apply for this loan (Yes/No): ");
        String confirmation = scanner.next();
        if (confirmation.equalsIgnoreCase("Yes")) {
            loanRepository.applyLoan(loan,customerId);
            System.out.println("Loan application submitted successfully.");
        } else {
            System.out.println("Loan application cancelled.");
        }
    }

    private static void getAllLoans() {
        List<Loan> loans = loanRepository.getAllLoan();
        if (loans.isEmpty()) {
            System.out.println("No loans available.");
        } else {
            for (Loan loan : loans) {
                System.out.println(loan);
            }
        }
    }

    private static void getLoanById() {
        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();
        try {
            Loan loan = loanRepository.getLoanById(loanId);
            System.out.println(loan);
        } catch (InvalidLoanException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void makeLoanRepayment() {
        System.out.print("Enter Loan ID: ");
        int loanId = scanner.nextInt();

        System.out.print("Enter Amount to Repay: ");
        double amount = scanner.nextDouble();

        try {
            loanRepository.loanRepayment(loanId, amount);
            System.out.println("Repayment successful.");
        } catch (InvalidLoanException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Repayment failed: " + e.getMessage());
        }
    }
}
