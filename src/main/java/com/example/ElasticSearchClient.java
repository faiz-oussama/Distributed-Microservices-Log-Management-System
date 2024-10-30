package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticSearchClient {
    private final String elasticSearchUrl = "http://localhost:9200/";

    public List<LogEntry> fetchLogs(String index, String type) {
        List<LogEntry> logs = new ArrayList<>();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = elasticSearchUrl + index + "/_search?type=" + type;
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getEntity().getContent());
            JsonNode hits = rootNode.path("hits").path("hits");

            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                LogEntry logEntry = new LogEntry(
                        source.path("ipAddress").asText(),
                        source.path("timestamp").asText(),
                        source.path("requestType").asText(),
                        source.path("endpoint").asText(),
                        source.path("statusCode").asText(),
                        source.path("responseTime").asText(),
                        source.path("referrer").asText(),
                        source.path("userAgent").asText(),
                        source.path("userId").asText(),
                        source.path("action").asText(),
                        source.path("logType").asText()
                );
                logs.add(logEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }
}