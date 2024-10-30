package com.example;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;

public class LogDataFetcher {
    private final ElasticSearchClient esClient = new ElasticSearchClient();
    private final LogDashboardApp dashboardApp;

    public LogDataFetcher(LogDashboardApp dashboardApp) {
        this.dashboardApp = dashboardApp;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<LogEntry> logs = esClient.fetchLogs("logs-index", "log");
                if (logs != null && !logs.isEmpty()) {
                    Platform.runLater(() -> dashboardApp.updateLogs(logs));
                }
            }
        }, 0, 2000);
    }
}