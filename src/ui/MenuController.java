package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MenuController {
    @FXML
    private Label nameLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private AnchorPane scenePane;

    Stage stage;

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
}
