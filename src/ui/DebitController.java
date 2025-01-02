package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    @FXML
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void submit(ActionEvent event) {
        if (TransactionUtils.hasOverdueLoan(userId)) {
            System.err.println("Transaction denied: User has overdue loans.");
            return;
        }

        try {
            double debit = Double.parseDouble(DebitAmount.getText());
            String desc = DescTextField.getText();

            // Get the savings percentage from the database
            double savingsPercentage = getSavingsPercentage(userId);

            // Calculate the amounts for balance and savings
            double savingsAmount = debit * (savingsPercentage / 100);
            double balanceAmount = debit - savingsAmount;

            // Record the transactions
            TransactionUtils.recordDebitTransaction(userId, balanceAmount, desc);
            updateSavings(userId, savingsAmount);

        } catch (NumberFormatException e) {
            System.err.println("Invalid debit amount: " + e.getMessage());
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