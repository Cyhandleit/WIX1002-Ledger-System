package ui;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;

public class InterestPredictionController {

    @FXML
    private Label bankLabel;
    @FXML
    private Label interestRateLabel;
    @FXML
    private Label dailyInterestLabel;
    @FXML
    private Label monthlyInterestLabel;
    @FXML
    private Label annuallyInterestLabel;
    @FXML
    private BarChart<String, Number> interestBarChart;
    @FXML
    private ChoiceBox<String> periodChoiceBox;

    private double dailyInterest;
    private double monthlyInterest;
    private double annuallyInterest;

    @FXML
    private void initialize() {
        periodChoiceBox.setItems(FXCollections.observableArrayList("Daily", "Monthly", "Annually"));
        periodChoiceBox.setOnAction(event -> updateGraph());
    }

    public void setInterestData(String bank, double interestRate, double balance) {
        bankLabel.setText("Bank: " + bank);
        interestRateLabel.setText(String.format("Interest Rate: %.2f%%", interestRate));

        dailyInterest = (balance * interestRate) / 365;
        monthlyInterest = (balance * interestRate) / 12;
        annuallyInterest = balance * interestRate;

        dailyInterestLabel.setText(String.format("Daily Interest: %.2f", dailyInterest));
        monthlyInterestLabel.setText(String.format("Monthly Interest: %.2f", monthlyInterest));
        annuallyInterestLabel.setText(String.format("Annually Interest: %.2f", annuallyInterest));

        periodChoiceBox.setValue("Daily"); // Default selection
        updateGraph();
    }

    private void updateGraph() {
        interestBarChart.getData().clear();
        String selectedPeriod = periodChoiceBox.getValue();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedPeriod + " Interest");

        switch (selectedPeriod) {
            case "Daily":
                for (int i = 1; i <= 7; i++) {
                    series.getData().add(new XYChart.Data<>("Day " + i, dailyInterest * i));
                }
                break;
            case "Monthly":
                for (int i = 1; i <= 12; i++) {
                    series.getData().add(new XYChart.Data<>("Month " + i, monthlyInterest * i));
                }
                break;
            case "Annually":
                for (int i = 1; i <= 5; i++) {
                    series.getData().add(new XYChart.Data<>("Year " + i, annuallyInterest * i));
                }
                break;
        }

        interestBarChart.getData().add(series);
    }
}