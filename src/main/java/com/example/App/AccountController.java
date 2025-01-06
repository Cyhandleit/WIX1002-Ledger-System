package com.example.App;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class AccountController {

    @FXML
    private Label BalanceLabel;

    @FXML
    private Label SavingsLabel;

    @FXML
    private Label LoanLabel;

    @FXML
    private PieChart spendingDistributionChart;

    private int userId;

    @FXML
    public void setUserId(int userId) {
        this.userId = userId;
        loadAccountDetails();
        loadSpendingDistribution();
    }

    @FXML
    public void loadAccountDetails() {
        try (Connection connection = ledgerDB.getConnection()) {
            double balance = 0.0;
            double savings = 0.0;
            double totalLoanAmount = 0.0;

            // Fetch balance and savings
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT balance, savings FROM accounts WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        balance = rs.getDouble("balance");
                        savings = rs.getDouble("savings");
                    }
                }
            }

            // Fetch total loan amount
            try (PreparedStatement loanStmt = connection.prepareStatement(
                    "SELECT SUM(total_repayment) AS total_loan_amount FROM loans WHERE user_id = ? AND total_repayment > 0")) {
                loanStmt.setInt(1, userId);
                try (ResultSet rs = loanStmt.executeQuery()) {
                    if (rs.next()) {
                        totalLoanAmount = rs.getDouble("total_loan_amount");
                    }
                }
            }

            // Display the fetched data
            displayAccount(balance, savings, totalLoanAmount);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void displayAccount(double balance, double savings, double loan) {
        BalanceLabel.setText(String.format("RM%.2f", balance));
        SavingsLabel.setText(String.format("RM%.2f", savings));
        LoanLabel.setText(String.format("RM%.2f", loan));
    }

    @FXML
    private void loadSpendingDistribution() {
        double totalDebit = 0.0;
        double totalCredit = 0.0;
        double totalSavings = 0.0;
        double totalLoan = 0.0;

        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT description, SUM(amount) AS total_amount FROM transactions WHERE user_id = ? GROUP BY description";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String description = rs.getString("description");
                        double totalAmount = rs.getDouble("total_amount");

                        switch (description) {
                            case "Savings":
                                totalSavings += totalAmount;
                                break;
                            case "Loan":
                                totalLoan += totalAmount;
                                break;
                            default:
                                if (totalAmount < 0) {
                                    totalCredit += Math.abs(totalAmount);
                                } else {
                                    totalDebit += totalAmount;
                                }
                                break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        spendingDistributionChart.getData().clear();
        addPieChartData("Debit", totalDebit, totalDebit + totalCredit + totalSavings + totalLoan);
        addPieChartData("Credit", totalCredit, totalDebit + totalCredit + totalSavings + totalLoan);
        addPieChartData("Savings", totalSavings, totalDebit + totalCredit + totalSavings + totalLoan);
        addPieChartData("Loan", totalLoan, totalDebit + totalCredit + totalSavings + totalLoan);
    }

    @FXML
    private void addPieChartData(String name, double value, double total) {
        if (total > 0) {
            PieChart.Data data = new PieChart.Data(name, value);
            spendingDistributionChart.getData().add(data);
            double percentage = (value / total) * 100;
            Tooltip tooltip = new Tooltip(String.format("%s: $%.2f (%.2f%%)", name, value, percentage));
            Tooltip.install(data.getNode(), tooltip);
        }
    }
}