package com.example;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogDashboardApp extends Application {
    private TextArea logArea = new TextArea();
    private XYChart chart;
    private VBox root; // Declare root as a class-level variable

    public void createChart(VBox root) {
        this.root = root; // Assign root to the class-level variable
        chart = new XYChartBuilder().width(800).height(600)
                .title("Response Time Chart")
                .xAxisTitle("Log Entry Index")
                .yAxisTitle("Response Time (ms)")
                .build();

        // Save the chart as a PNG image
        try {
            BitmapEncoder.saveBitmap(chart, "./chart", BitmapFormat.PNG);
            Image chartImage = new Image(new File("./chart.png").toURI().toString());
            ImageView imageView = new ImageView(chartImage);
            root.getChildren().add(imageView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        root = new VBox(); // Initialize root
        logArea.setEditable(false);
        root.getChildren().add(logArea);

        createChart(root); // Call your existing method to create the chart

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Log Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start the data fetcher
        LogDataFetcher dataFetcher = new LogDataFetcher(this);
        dataFetcher.start();
    }

    public void updateLogs(List<LogEntry> logs) {
        logArea.clear();
        double[] xData = new double[logs.size()];
        double[] yData = new double[logs.size()];

        for (int i = 0; i < logs.size(); i++) {
            LogEntry log = logs.get(i);
            logArea.appendText(log.toString() + "\n");
            xData[i] = i;
            yData[i] = Double.parseDouble(log.getResponseTime());
        }

        // Update the chart with new data
        Platform.runLater(() -> {
            chart.updateXYSeries("Response Times", xData, yData, null);
            try {
                BitmapEncoder.saveBitmap(chart, "./chart", BitmapFormat.PNG);
                Image chartImage = new Image(new File("./chart.png").toURI().toString());
                ImageView imageView = new ImageView(chartImage);
                root.getChildren().set(1, imageView); // Replace the old chart image with the new one
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}