package com.example;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import java.io.FileWriter;


public class ElasticSearchClient {
    private final ElasticsearchClient esClient;

    public ElasticSearchClient(String serverUrl, String apiKey) {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((TrustStrategy) (X509Certificate[] chain, String authType) -> true)
                    .build();

            RestClientBuilder builder = RestClient.builder(HttpHost.create(serverUrl))
                    .setDefaultHeaders(new Header[]{
                            new BasicHeader("Authorization", "ApiKey " + apiKey)
                    })
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder.setSSLContext(sslContext);
                        }
                    });

                    RestClient restClient = builder.build();
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper.registerModule(new JavaTimeModule());

                    ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));


            esClient = new ElasticsearchClient(transport);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Elasticsearch client", e);
        }
    }

    public List<LogEntry> fetchLogs(String index) {
        List<LogEntry> logs = new ArrayList<>();
        int pageSize = 100;
        AtomicInteger from = new AtomicInteger(0);

        try {
            boolean hasMoreResults = true;
            while (hasMoreResults) {    
                SearchResponse<LogEntry> response = esClient.search(s -> s
                        .index(index)
                        .from(from.get())
                        .size(pageSize)
                        .query(q -> q
                                .matchAll(m -> m)
                        ), LogEntry.class);

                List<Hit<LogEntry>> hits = response.hits().hits();
                if (hits.isEmpty()) {
                    hasMoreResults = false;
                } else {
                    for (Hit<LogEntry> hit : hits) {
                        logs.add(hit.source());
                    }
                    from.addAndGet(pageSize);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public void saveLogsToFile(List<LogEntry> logs, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        try (FileWriter file = new FileWriter(filename)) {
            file.write(mapper.writeValueAsString(logs));
            System.out.println("Logs saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverUrl = "https://localhost:9200";
        String apiKey = "amtLeTQ1SUJoeXBuVTRjeTRGZGg6b05ReEd4aWxRZVNLOC1mdE5yUFhaQQ==";
        ElasticSearchClient client = new ElasticSearchClient(serverUrl, apiKey);

        List<LogEntry> logs = client.fetchLogs("filebeat-logs-*");
        client.saveLogsToFile(logs, "logs.json");
        for (LogEntry log : logs) {
            System.out.println(log);
            System.out.println("Total logs collected so far: " + logs.size());
        }

        client.close();
    }

    public void close() {
        try {
            esClient._transport().close();
            System.out.println("Elasticsearch client closed successfully.");
        } catch (IOException e) {
            System.err.println("Error closing Elasticsearch transport: " + e.getMessage());
        }
    }
}