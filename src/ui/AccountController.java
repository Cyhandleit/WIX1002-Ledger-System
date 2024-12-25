package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

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
    public void setUserId(int userId) {
        this.userId = userId;
        loadAccountDetails();
        loadSpendingDistribution();
    }

    public void loadAccountDetails() {
        try (Connection connection = ledgerDB.getConnection()) {
            // Calculate the balance by summing all transactions
            double balance = 0.0;
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT SUM(amount) AS total_balance FROM transactions WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        balance = rs.getDouble("total_balance");
                    }
                }
            }

            // Retrieve savings and loan amounts from transactions
            double savings = 0.0;
            double loan = 0.0;
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT SUM(amount) AS total_savings FROM transactions WHERE user_id = ? AND description = 'savings'")) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        savings = rs.getDouble("total_savings");
                    }
                }
            }
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT SUM(amount) AS total_loan FROM transactions WHERE user_id = ? AND description = 'loan'")) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        loan = rs.getDouble("total_loan");
                    }
                }
            }

            displayAccount(balance, savings, loan);

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
                            case "savings":
                                totalSavings += totalAmount;
                                break;
                            case "loan":
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
        spendingDistributionChart.getData().add(new PieChart.Data("Debit", Math.abs(totalDebit)));
        spendingDistributionChart.getData().add(new PieChart.Data("Credit", Math.abs(totalCredit)));
        spendingDistributionChart.getData().add(new PieChart.Data("Savings", Math.abs(totalSavings)));
        spendingDistributionChart.getData().add(new PieChart.Data("Loan", Math.abs(totalLoan)));
    }
}