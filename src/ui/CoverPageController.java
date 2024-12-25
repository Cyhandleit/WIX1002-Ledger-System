package ui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class CoverPageController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label wrongLogIn;

    @FXML
    public void switchToRegisterPage(MouseEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("RegisterPage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToCoverPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToLoginPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void userLogIn(ActionEvent event) throws IOException {
        String inputUsername = username.getText();
        String inputPassword = password.getText();

        System.out.println("Attempting to log in with username: " + inputUsername);

        try (Connection connection = ledgerDB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM users WHERE username = ? AND password = ?")) {

            preparedStatement.setString(1, inputUsername);
            preparedStatement.setString(2, inputPassword);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                wrongLogIn.setText("Success!");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuPage.fxml"));
                root = loader.load();

                MenuController menuController = loader.getController();
                menuController.displayName(inputUsername, resultSet.getString("email"), inputPassword);

                // Pass the user_id to the MenuController
                menuController.setUserId(resultSet.getInt("user_id"));

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            } else {
                wrongLogIn.setText("Wrong username or password!");
                System.out.println("Login failed: Wrong username or password.");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            wrongLogIn.setText("Database error occurred!");
        } catch (IOException e) {
            System.err.println("FXML loading error: " + e.getMessage());
            e.printStackTrace();
            wrongLogIn.setText("FXML loading error occurred!");
        }
    }
}
