package ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MenuController implements Initializable {
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;
    @FXML
    private Label WelcomeLabel;

    public void displayName(String username,String email,String password){
        WelcomeLabel.setText("== Welcome, "+username+" ==");
        System.out.println(email);
        System.out.println(password);
    }

    private int userId; // Store the user ID of the logged-in user

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        // Initialization code
    }

    @FXML
    private void AccountPage(MouseEvent event) {
        loadPage("AccountPage");
    }

    @FXML
    private void DebitPage(MouseEvent event) {
        loadPage("DebitPage");
    }

    @FXML
    private void CreditPage(MouseEvent event) {
        loadPage("CreditPage");
    }

    @FXML
    private void HistoryPage(MouseEvent event) {
        loadPage("HistoryPage");
    }

    @FXML
    private void SavingPage(MouseEvent event) {
        loadPage("SavingPage");
    }

    @FXML
    private void CreditLoanPage(MouseEvent event) {
        loadPage("CreditLoanPage");
    }

    @FXML
    private void DIPPage(MouseEvent event) {
        loadPage("DIPPage");
    }

    private void loadPage(String page) {
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page + ".fxml"));
            root = loader.load();

            // Pass the user_id to the AccountController
            if (page.equals("AccountPage")) {
                AccountController accountController = loader.getController();
                accountController.setUserId(userId);
            }

        } catch (IOException e) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, e);
        }

        bp.setCenter(root);
    }

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToCoverPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}