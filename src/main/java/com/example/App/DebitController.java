package com.example.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DebitController {

    @FXML
    private TextField DebitAmount;
    @FXML
    private TextField DescTextField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label transactionStatusLabel;
    @FXML
    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    @FXML
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void submit(ActionEvent event) {
        if (TransactionUtils.hasOverdueLoan(userId)) {
            errorLabel.setText("Transaction denied: User has overdue loans.");
            transactionStatusLabel.setText("Transaction failed: Overdue loans.");
            return;
        }

        try {
            double debit = Double.parseDouble(DebitAmount.getText());
            String desc = DescTextField.getText();

            if (debit <= 0) {
                errorLabel.setText("Invalid debit amount: Amount must be positive.");
                transactionStatusLabel.setText("Transaction failed: Invalid amount.");
                return;
            }

            // Get the savings percentage from the database
            double savingsPercentage = getSavingsPercentage(userId);

            // Calculate the amounts for balance and savings
            double savingsAmount = debit * (savingsPercentage / 100);
            double balanceAmount = debit - savingsAmount;

            // Record the transactions
            TransactionUtils.recordDebitTransaction(userId, balanceAmount, desc);
            updateSavings(userId, savingsAmount);
            errorLabel.setText(""); // Clear error message on successful transaction
            transactionStatusLabel.setText("Transaction successful!");

        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid debit amount: " + e.getMessage());
            transactionStatusLabel.setText("Transaction failed: Invalid input.");
        }
    }

    private double getSavingsPercentage(int userId) {
        double percentage = 0.0;
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT savings_percentage FROM savings WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        percentage = rs.getDouble("savings_percentage");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting savings percentage: " + e.getMessage());
            e.printStackTrace();
        }
        return percentage;
    }

    private void updateSavings(int userId, double savingsAmount) {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "UPDATE accounts SET savings = savings + ? WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setDouble(1, savingsAmount);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating savings: " + e.getMessage());
            e.printStackTrace();
        }
    }
}