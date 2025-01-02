package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DIPController {

    @FXML
    private int userId;

    @FXML
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private ChoiceBox<String> bankChoiceBox;
    @FXML
    private Button checkButton;
    @FXML
    private Map<String, Double> bankInterestRates;

    @FXML
    private void initialize() {
        bankInterestRates = new HashMap<>();
        bankInterestRates.put("RHB", 2.6);
        bankInterestRates.put("Maybank", 2.5);
        bankInterestRates.put("Hong Leong", 2.3);
        bankInterestRates.put("Alliance", 2.85);
        bankInterestRates.put("AmBank", 2.55);
        bankInterestRates.put("Standard Chartered", 2.65);

        bankChoiceBox.getItems().addAll(bankInterestRates.keySet());
    }

    @FXML
    private void checkInterest(ActionEvent event) {
        String selectedBank = bankChoiceBox.getValue();
        if (selectedBank == null) {
            System.out.println("No bank selected.");
            return;
        }
        System.out.println("Selected bank: " + selectedBank);
        double interestRate = bankInterestRates.get(selectedBank);
        double balance = getCurrentBalance();
        System.out.println("Interest rate: " + interestRate);
        System.out.println("Current balance: " + balance);

        showInterestPredictionWindow(selectedBank, interestRate, balance);
    }

    private double getCurrentBalance() {
        double balance = 0.0;
        try (Connection connection = ledgerDB.getConnection()) {
            System.out.println("Database connection established.");
            String query = "SELECT balance FROM accounts WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                System.out.println("Executing query: " + query + " with user_id: " + userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        balance = rs.getDouble("balance");
                        System.out.println("Balance retrieved: " + balance);
                    } else {
                        System.out.println("No balance found for user_id: " + userId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return balance;
    }

    private void showInterestPredictionWindow(String bank, double interestRate, double balance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InterestPredictionWindow.fxml"));
            Parent root = loader.load();

            InterestPredictionController controller = loader.getController();
            controller.setInterestData(bank, interestRate, balance);

            Stage stage = new Stage();
            stage.setTitle("Interest Prediction");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
