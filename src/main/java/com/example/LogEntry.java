package com.example;

public class LogEntry {
    private String ipAddress;
    private String timestamp;
    private String requestType;
    private String endpoint;
    private String statusCode;
    private String responseTime;
    private String referrer;
    private String userAgent;
    private String userId;
    private String action;
    private String logType;

    public LogEntry(String ipAddress, String timestamp, String requestType, String endpoint, String statusCode, String responseTime, String referrer, String userAgent, String userId, String action, String logType) {
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.requestType = requestType;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.responseTime = responseTime;
        this.referrer = referrer;
        this.userAgent = userAgent;
        this.userId = userId;
        this.action = action;
        this.logType = logType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public String getReferrer() {
        return referrer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getLogType() {
        return logType;
    }
}