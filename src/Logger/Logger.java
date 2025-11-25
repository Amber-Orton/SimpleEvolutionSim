package Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import Run.Main;

public class Logger {
    private static Map<String, LogEvent> logs = new HashMap<>();
    private static final String LOG_DIR = "log";

    public static void logEvent(String task, String message) {
        if (!Main.IN_DEPTH_DEBUG_MODE) return;
        if (!logs.containsKey(task)) {
            logs.put(task, new LogEvent(task));
        }
        LogEvent logEvent = logs.get(task);
        logEvent.logEvent(message);
    }

    public static void logError(String task, String message) {
        if (!logs.containsKey(task)) {
            logs.put(task, new LogEvent(task));
        }
        LogEvent logEvent = logs.get(task);
        logEvent.logEvent("ERROR: " + message);
    }

    public static void write() {
        try {
            // Create log directory if it doesn't exist
            Path logPath = Paths.get(LOG_DIR);
            if (!Files.exists(logPath)) {
                Files.createDirectories(logPath);
            }

            // Generate filename with date and time
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            File logFile = new File(LOG_DIR + "/log_" + timestamp + ".txt");

            // Write all logs to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                writer.write("=== Evolution Simulator Log ===\n");
                writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
                
                for (Map.Entry<String, LogEvent> entry : logs.entrySet()) {
                    writer.write("Task: " + entry.getKey() + "\n");
                    writer.write(entry.getValue().toString());
                    writer.write("\n" + "=".repeat(80) + "\n\n");
                }
                
                System.out.println("Log written to: " + logFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to write log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static LogEvent getLogEvent(String task) {
        return logs.get(task);
    }
    
    public static void clear() {
        logs.clear();
    }
}