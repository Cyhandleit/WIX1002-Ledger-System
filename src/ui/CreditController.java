package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreditController {

    @FXML
    private TextField CreditAmount;
    @FXML
    private TextField DescTextField;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
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
            double credit = Double.parseDouble(CreditAmount.getText());
            String desc = DescTextField.getText();
            TransactionUtils.recordTransaction(userId, -credit, desc);
        } catch (NumberFormatException e) {
            System.err.println("Invalid credit amount: " + e.getMessage());
        }
    }
}