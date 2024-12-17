package ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MenuController implements Initializable {
    @FXML
    private Label nameLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private ChoiceBox <String> MenuBar;

    private String[] Transaction = {"Debit","Credit","History","Saving","Credit Loan","Deposit Interest Prediction"};

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void displayName(String username,String email,String password){
        nameLabel.setText("Hello: "+username);
        System.out.println(email);
        System.out.println(password);
    }

    public void logout(ActionEvent event) {
        
        stage = (Stage)scenePane.getScene().getWindow();
        System.out.println("You succesfully logged out!");
        stage.close();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
        MenuBar.getItems().addAll(Transaction);
        MenuBar.setOnAction(arg01 -> {
            try {
                switchToFunctionPages(arg01);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void switchToFunctionPages(ActionEvent event) throws IOException {
        String selectedOption = MenuBar.getValue();

        switch(selectedOption){
            case "Debit": root = FXMLLoader.load(getClass().getResource("DebitPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
    
            stage.setScene(scene);
            stage.show();
            break;

            case "Credit": root = FXMLLoader.load(getClass().getResource("CreditPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
    
            stage.setScene(scene);
            stage.show();
            break;

            case "History": root = FXMLLoader.load(getClass().getResource("HistoryPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
    
            stage.setScene(scene);
            stage.show();
            break;

            case "Saving": root = FXMLLoader.load(getClass().getResource("SavingPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
    
            stage.setScene(scene);
            stage.show();
            break;

            case "Credit Loan": root = FXMLLoader.load(getClass().getResource("CreditLaonPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
    
            stage.setScene(scene);
            stage.show();
            break;

            case "Deposit Interest Prediction": root = FXMLLoader.load(getClass().getResource("DIPPage.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
    
            stage.setScene(scene);
            stage.show();
            break;

            default: System.out.println("Wrong Choice!");
            break;
        }
        root = FXMLLoader.load(getClass().getResource("DebitPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
