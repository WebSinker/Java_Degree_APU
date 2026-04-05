package ui;

import models.User;
import models.Receipt;
import services.AnalysisService;
import utils.FileHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Map;

import java.util.Map;

public class ReportView {
    private Stage stage;
    private User manager;

    public ReportView(Stage stage, User manager) {
        this.stage = stage;
        this.manager = manager;
    }

    public Scene createScene() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Automotive Service Centre Analytics");
        title.getStyleClass().add("title-text");

        GridPane chartsGrid = new GridPane();
        chartsGrid.setHgap(30);
        chartsGrid.setVgap(30);
        chartsGrid.setAlignment(Pos.CENTER);

        // 1. Pie Chart: Service Category Distribution
        Map<String, Long> dist = AnalysisService.getServiceCategoryDistribution();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        dist.forEach((cat, count) -> pieData.add(new PieChart.Data(cat + " (" + count + ")", count)));
        
        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Services by Category");
        pieChart.setLabelsVisible(true);
        pieChart.setPrefSize(400, 350);

        // 2. Bar Chart: Average Price per Category
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Avg Price by Category (RM)");
        xAxis.setLabel("Category");
        yAxis.setLabel("Price");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Average Price");
        AnalysisService.getAverageRevenueByCategory().forEach((cat, avg) -> {
            series.getData().add(new XYChart.Data<>(cat, avg));
        });
        barChart.getData().add(series);
        barChart.setPrefSize(400, 350);

        chartsGrid.add(pieChart, 0, 0);
        chartsGrid.add(barChart, 1, 0);

        // 3. Line Chart: Monthly Revenue Trends
        CategoryAxis lineX = new CategoryAxis();
        NumberAxis lineY = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(lineX, lineY);
        lineChart.setTitle("Business Performance (Monthly Revenue)");
        lineX.setLabel("Month");
        lineY.setLabel("Revenue (RM)");

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("RM Revenue");
        
        Map<String, Double> trends = AnalysisService.getMonthlyRevenueTrend();
        double totalRevenue = 0;
        for (Map.Entry<String, Double> entry : trends.entrySet()) {
            revenueSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            totalRevenue += entry.getValue();
        }
        lineChart.getData().add(revenueSeries);
        lineChart.setPrefSize(800, 300);
        
        VBox financialBox = new VBox(10, lineChart, new Label(String.format("Total Revenue to Date: RM %.2f", totalRevenue)));
        financialBox.setAlignment(Pos.CENTER);

        // 4. Raw Data Table for Review
        Label logTitle = new Label("Detailed Transaction Log (Materials for Analysis)");
        logTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        
        TableView<Receipt> logTable = new TableView<>();
        TableColumn<Receipt, String> rIdCol = new TableColumn<>("Receipt ID");
        rIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Receipt, String> appIdCol = new TableColumn<>("App ID");
        appIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Receipt, Double> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        TableColumn<Receipt, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        
        logTable.getColumns().addAll(rIdCol, appIdCol, amtCol, dateCol);
        logTable.setItems(FXCollections.observableArrayList(FileHandler.loadAllReceipts()));
        logTable.setPrefHeight(200);
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Summary Label
        double avgRating = AnalysisService.getAverageCustomerRating();
        Label summaryLabel = new Label(String.format("Current Average Customer Satisfaction: %.1f / 5.0", avgRating));
        summaryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4facfe;");

        Button backBtn = new Button("Back to Dashboard");
        backBtn.getStyleClass().add("secondary-button");

        root.getChildren().addAll(title, chartsGrid, financialBox, logTitle, logTable, summaryLabel, backBtn);

        backBtn.setOnAction(e -> {
            ManagerDashboardView dashboard = new ManagerDashboardView(stage, manager);
            stage.setScene(dashboard.createScene());
        });

        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        return scene;
    }
}
