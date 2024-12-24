package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

        if (today.equals(lastDayOfMonth)) {
            try (Connection connection = ledgerDB.getConnection()) {
                connection.setAutoCommit(false);

                // Transfer savings to balance
                try (PreparedStatement transferStmt = connection.prepareStatement(
                        "UPDATE accounts SET balance = balance + savings, savings = 0 WHERE user_id = ?")) {
                    transferStmt.setInt(1, userId);
                    transferStmt.executeUpdate();
                }

                connection.commit();
                System.out.println("Savings auto-transferred to balance.");
            } catch (SQLException e) {
                System.out.println("Auto-transfer failed: " + e.getMessage());
            }
        }
    }
}