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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MenuController implements Initializable {
    @FXML
    private BorderPane bp;
    @FXML
    private Label WelcomeLabel;

    public void displayName(String username, String email, String password) {
        WelcomeLabel.setText("== Welcome, " + username + " ==");
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
    private void AccountPage() {
        loadPage("AccountPage");
    }

    @FXML
    private void DebitPage() {
        loadPage("DebitPage");
    }

    @FXML
    private void CreditPage() {
        loadPage("CreditPage");
    }

    @FXML
    private void HistoryPage() {
        loadPage("HistoryPage");
    }

    @FXML
    private void SavingsPage() {
        loadPage("SavingsPage");
    }

    @FXML
    private void CreditLoanPage() {
        loadPage("CreditLoanPage");
    }

    @FXML
    private void DIPPage() {
        loadPage("DIPPage");
    }

    private void loadPage(String page) {
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page + ".fxml"));
            root = loader.load();

            // Pass the user_id to the appropriate controller
            if (page.equals("AccountPage")) {
                AccountController accountController = loader.getController();
                accountController.setUserId(userId);
            } else if (page.equals("DebitPage")) {
                DebitController debitController = loader.getController();
                debitController.setUserId(userId);
                debitController.setSavingsController(getSavingsController());
            } else if (page.equals("CreditPage")) {
                CreditController creditController = loader.getController();
                creditController.setUserId(userId);
            } else if (page.equals("SavingsPage")) {
                SavingsController savingsController = loader.getController();
                savingsController.setUserId(userId);
            } else if (page.equals("HistoryPage")) {
                HistoryController historyController = loader.getController();
                historyController.setUserId(userId);
            }

        } catch (IOException e) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, e);
        }

        bp.setCenter(root);
    }

    private SavingsController getSavingsController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SavingsPage.fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void switchToCoverPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}