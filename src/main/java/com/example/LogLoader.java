package com.example;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LogLoader {
    public List<LogEntry> loadLogsFromFile(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        List<LogEntry> logs = null;
        try {
            logs = List.of(mapper.readValue(new File(filename), LogEntry[].class));
            System.out.println("Loaded " + logs.size() + " logs from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }
}