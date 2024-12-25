package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class SavingsController {

    @FXML
    private TextField SavingsTextField;
    @FXML
    private Button ConfirmButton;

    private int userId; // Store the user ID of the logged-in user
    private double savingsPercentage; // Store the savings percentage

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void initialize() {
        // Initialize any necessary data or event handlers
    }

    @FXML
    private void setSavingsPercentage(ActionEvent event) {
        try {
            savingsPercentage = Double.parseDouble(SavingsTextField.getText());
            System.out.println("Savings percentage set to: " + savingsPercentage + "%");
        } catch (NumberFormatException e) {
            System.out.println("Invalid savings percentage: " + e.getMessage());
        }
    }

    public double getSavingsPercentage() {
        return savingsPercentage;
    }

    public void autoTransferSavings() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDayOfNextMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());

        if (today.equals(lastDayOfMonth)) {
            try (Connection connection = ledgerDB.getConnection()) {
                connection.setAutoCommit(false);

                // Deduct savings percentage from balance and add to savings
                try (PreparedStatement transferStmt = connection.prepareStatement(
                        "UPDATE accounts SET savings = savings + (balance * ? / 100), balance = balance - (balance * ? / 100) WHERE user_id = ?")) {
                    transferStmt.setDouble(1, savingsPercentage);
                    transferStmt.setDouble(2, savingsPercentage);
                    transferStmt.setInt(3, userId);
                    transferStmt.executeUpdate();
                }

                // Record the savings deduction transaction
                try (PreparedStatement transactionStmt = connection.prepareStatement(
                        "INSERT INTO transactions (user_id, amount, description, date) VALUES (?, ?, ?, ?)")) {
                    double savingsAmount = getBalance() * savingsPercentage / 100;
                    transactionStmt.setInt(1, userId);
                    transactionStmt.setDouble(2, -savingsAmount); // Record as negative amount
                    transactionStmt.setString(3, "Savings Deduction");
                    transactionStmt.setDate(4, java.sql.Date.valueOf(today));
                    transactionStmt.executeUpdate();
                }

                // Record the savings addition transaction for the next month
                try (PreparedStatement transactionStmt = connection.prepareStatement(
                        "INSERT INTO transactions (user_id, amount, description, date) VALUES (?, ?, ?, ?)")) {
                    double savingsAmount = getBalance() * savingsPercentage / 100;
                    transactionStmt.setInt(1, userId);
                    transactionStmt.setDouble(2, savingsAmount); // Record as positive amount
                    transactionStmt.setString(3, "Savings Addition");
                    transactionStmt.setDate(4, java.sql.Date.valueOf(firstDayOfNextMonth));
                    transactionStmt.executeUpdate();
                }

                connection.commit();
                System.out.println("Savings auto-transferred to balance.");
            } catch (SQLException e) {
                System.out.println("Auto-transfer failed: " + e.getMessage());
            }
        }
    }

    private double getBalance() {
        double balance = 0.0;
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT balance FROM accounts WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        balance = rs.getDouble("balance");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return balance;
    }
}