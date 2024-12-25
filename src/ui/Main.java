package ui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        launch(args);
    }

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        try {
            Parent root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

            scheduleSavingsTransfer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleSavingsTransfer() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        long initialDelay = java.time.Duration.between(today.atStartOfDay(), lastDayOfMonth.atStartOfDay()).toMillis();

        scheduler.scheduleAtFixedRate(() -> {
            SavingsController savingsController = new SavingsController();
            savingsController.autoTransferSavings();
        }, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    public void changeScene(String MenuPagel) throws Exception {
        Parent pane = FXMLLoader.load(getClass().getResource("MenuPage.fxml"));
        stage.getScene().setRoot(pane);
    }
}