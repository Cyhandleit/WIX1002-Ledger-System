package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AccountController {

    @FXML
    private Label BalanceLabel;

    @FXML
    private Label SavingsLabel;

    @FXML
    private Label LoanLabel;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
        loadAccountDetails();
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

            // Retrieve savings and loan amounts
            double savings = 0.0;
            double loan = 0.0;
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT savings, loan FROM accounts WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        savings = rs.getDouble("savings");
                        loan = rs.getDouble("loan");
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
}