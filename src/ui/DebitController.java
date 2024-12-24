package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DebitController {

    @FXML
    private TextField DebitAmount;
    @FXML
    private TextField DescTextField;

    private int userId; // Store the user ID of the logged-in user
    private SavingsController savingsController;

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSavingsController(SavingsController savingsController) {
        this.savingsController = savingsController;
    }

    @FXML
    private void submit(ActionEvent event) {
        try {
            double debit = Double.parseDouble(DebitAmount.getText());
            String desc = DescTextField.getText();

            // Calculate the savings amount
            double savingsAmount = debit * (savingsController.getSavingsPercentage() / 100);
            double debitAmountAfterSavings = debit - savingsAmount;

            // Insert the transaction into the database
            try (Connection connection = ledgerDB.getConnection()) {
                connection.setAutoCommit(false); // Start transaction

                // Insert the transaction into the transactions table
                try (PreparedStatement transactionStmt = connection.prepareStatement(
                        "INSERT INTO transactions (user_id, amount, description) VALUES (?, ?, ?)")) {
                    transactionStmt.setInt(1, userId);
                    transactionStmt.setDouble(2, debitAmountAfterSavings);
                    transactionStmt.setString(3, desc);
                    transactionStmt.executeUpdate();
                }

                // Update the savings amount
                try (PreparedStatement savingsStmt = connection.prepareStatement(
                        "UPDATE accounts SET savings = savings + ? WHERE user_id = ?")) {
                    savingsStmt.setDouble(1, savingsAmount);
                    savingsStmt.setInt(2, userId);
                    savingsStmt.executeUpdate();
                }

                connection.commit(); // Commit transaction

                System.out.println("Transaction saved successfully!");

                // Reload account details
                reloadAccountDetails(event);

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void reloadAccountDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountPage.fxml"));
            Parent root = loader.load();
            AccountController accountController = loader.getController();
            accountController.setUserId(userId);
            accountController.loadAccountDetails();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}