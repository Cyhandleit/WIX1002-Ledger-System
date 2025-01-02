package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
            TransactionUtils.recordTransaction(userId, debit, desc);
        } catch (NumberFormatException e) {
            System.err.println("Invalid debit amount: " + e.getMessage());
        }
    }
}