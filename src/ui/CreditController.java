package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CreditController {

    @FXML
    private TextField DebitAmount;
    @FXML
    private TextField DescTextField;
    @FXML
    private Button ConfirmButton;

    double debit;
    String desc;

    @FXML
    private void submit (ActionEvent event){
        try {
            debit = Double.parseDouble(DebitAmount.getText());
            desc = DescTextField.getText();
            System.out.println(debit);
            System.out.println(desc);
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
}
