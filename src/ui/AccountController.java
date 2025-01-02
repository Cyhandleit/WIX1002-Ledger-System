package ui;

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

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    @FXML
    public void setUserId(int userId) {
        this.userId = userId;
        loadAccountDetails();
        loadSpendingDistribution();
    }

    @FXML
    public void loadAccountDetails() {
        try (Connection connection = ledgerDB.getConnection()) {
            // Retrieve balance and savings amounts from the accounts table
            double balance = 0.0;
            double savings = 0.0;
            double totalLoanAmount = 0.0;

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

            // Calculate the total loan amount after interest
            try (PreparedStatement loanStmt = connection.prepareStatement(
                    "SELECT SUM(total_repayment) AS total_loan_amount FROM loans WHERE user_id = ? AND total_repayment > 0")) {
                loanStmt.setInt(1, userId);
                try (ResultSet rs = loanStmt.executeQuery()) {
                    if (rs.next()) {
                        totalLoanAmount = rs.getDouble("total_loan_amount");
                    }
                }
            }

            displayAccount(balance, savings, totalLoanAmount);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void displayAccount(double balance, double savings, double loan) {
        BalanceLabel.setText(String.format("%.2f", balance));
        SavingsLabel.setText(String.format("%.2f", savings));
        LoanLabel.setText(String.format("%.2f", loan));
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
                                    totalCredit += totalAmount;
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
        addPieChartData("Debit", Math.abs(totalDebit), totalDebit + totalCredit + totalSavings + totalLoan);
        addPieChartData("Credit", Math.abs(totalCredit), totalDebit + totalCredit + totalSavings + totalLoan);
        addPieChartData("Savings", Math.abs(totalSavings), totalDebit + totalCredit + totalSavings + totalLoan);
        addPieChartData("Loan", Math.abs(totalLoan), totalDebit + totalCredit + totalSavings + totalLoan);
    }

    @FXML
    private void addPieChartData(String name, double value, double total) {
        PieChart.Data data = new PieChart.Data(name, value);
        spendingDistributionChart.getData().add(data);
        double percentage = (value / total) * 100;
        Tooltip tooltip = new Tooltip(String.format("%s: %.2f (%.2f%%)", name, value, percentage));
        Tooltip.install(data.getNode(), tooltip);
    }
}