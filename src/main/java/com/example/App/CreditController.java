package com.example.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreditController {

    @FXML
    private TextField CreditAmount;
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
            double credit = Double.parseDouble(CreditAmount.getText());
            String desc = DescTextField.getText();

            if (credit <= 0) {
                errorLabel.setText("Invalid credit amount: Amount must be positive.");
                transactionStatusLabel.setText("Transaction failed: Invalid amount.");
                return;
            }

            TransactionUtils.recordCreditTransaction(userId, credit, desc);
            errorLabel.setText(""); // Clear error message on successful transaction
            transactionStatusLabel.setText("Transaction successful!");

        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid credit amount: " + e.getMessage());
            transactionStatusLabel.setText("Transaction failed: Invalid input.");
        }
    }
}