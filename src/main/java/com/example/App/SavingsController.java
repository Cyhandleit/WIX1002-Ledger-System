package com.example.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class SavingsController {

    @FXML
    private TextField SavingsTextField;

    @FXML
    private Button activateButton;

    @FXML
    private Button cancelButton;

    private int userId;
    private double savingsPercentage = 0.0;
    private boolean isActive = false;
    private LocalDate activationDate;

    public void setUserId(int userId) {
        this.userId = userId;
        checkSavingsStatus();
    }

    private void checkSavingsStatus() {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT savings_percentage, activation_date FROM savings WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        savingsPercentage = rs.getDouble("savings_percentage");
                        activationDate = rs.getDate("activation_date").toLocalDate();
                        isActive = true;
                        updateUI();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking savings status: " + e.getMessage());
        }
    }

    @FXML
    private void activateSavings(ActionEvent event) {
        try {
            double newPercentage = Double.parseDouble(SavingsTextField.getText());
            if (newPercentage <= 0 || newPercentage > 100) {
                showAlert("Invalid Percentage", "Percentage must be between 0 and 100");
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Activate Savings");
            confirmation.setHeaderText("Confirm Activation");
            confirmation.setContentText("Are you sure you want to activate the savings system with " + newPercentage + "%?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                activateSavingsSystem(newPercentage);
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid percentage");
        }
    }

    private void activateSavingsSystem(double percentage) {
        try (Connection connection = ledgerDB.getConnection()) {
            connection.setAutoCommit(false);

            String query = "INSERT INTO savings (user_id, savings_percentage, activation_date) VALUES (?, ?, ?) " +
                          "ON DUPLICATE KEY UPDATE savings_percentage = ?, activation_date = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setDouble(2, percentage);
                stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                stmt.setDouble(4, percentage);
                stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
            }

            connection.commit();
            isActive = true;
            activationDate = LocalDate.now();
            savingsPercentage = percentage;
            updateUI();
            showAlert("Success", "Savings system activated successfully!");
        } catch (SQLException e) {
            System.err.println("Error activating savings: " + e.getMessage());
            showAlert("Error", "Failed to activate savings system");
        }
    }

    @FXML
    private void cancelSavings(ActionEvent event) {
        if (activationDate == null || LocalDate.now().isBefore(activationDate.plusMonths(1))) {
            showAlert("Cannot Cancel", "You can only cancel the savings system after one month of activation");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Savings");
        confirmation.setHeaderText("Confirm Cancellation");
        confirmation.setContentText("Are you sure you want to cancel the savings system?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cancelSavingsSystem();
        }
    }

    private void cancelSavingsSystem() {
        try (Connection connection = ledgerDB.getConnection()) {
            connection.setAutoCommit(false);

            String query = "DELETE FROM savings WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            connection.commit();
            isActive = false;
            savingsPercentage = 0.0;
            activationDate = null;
            updateUI();
            showAlert("Success", "Savings system cancelled successfully!");
        } catch (SQLException e) {
            System.err.println("Error cancelling savings: " + e.getMessage());
            showAlert("Error", "Failed to cancel savings system");
        }
    }

    private void updateUI() {
        if (isActive) {
            activateButton.setDisable(true);
            cancelButton.setDisable(false);
            SavingsTextField.setText(String.valueOf(savingsPercentage));
            SavingsTextField.setDisable(true);
        } else {
            activateButton.setDisable(false);
            cancelButton.setDisable(true);
            SavingsTextField.setDisable(false);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public double getSavingsPercentage() {
        return savingsPercentage;
    }
}