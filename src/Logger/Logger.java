package Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import Run.Main;

public class Logger {
    private static final Map<String, LogEvent> logs = new ConcurrentHashMap<>();
    private static final String LOG_DIR = "log";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static FileHandler fileHandler;

    public static void initialize() {
        try {
            Path logPath = Paths.get(LOG_DIR);
            Files.createDirectories(logPath);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String logFile = logPath.resolve("log_" + timestamp + ".txt").toString();
            
            fileHandler = new FileHandler(logFile, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
            
            LOGGER.info("=== Evolution Simulator Log Started ===");
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static void logEvent(String task, String message) {
        if (!Main.IN_DEPTH_DEBUG_MODE) return;
        LOGGER.info(String.format("[%s] %s", task, message));
        clearOldLogs(50_000_000_000_000L);
    }

    public static void logError(String task, String message) {
        LOGGER.severe(String.format("[%s] %s", task, message));
        clearOldLogs(50_000_000_000_000L);
    }

    public static void close() {
        if (fileHandler != null) {
            LOGGER.info("=== Evolution Simulator Log Ended ===");
            fileHandler.close();
        }
    }

    public static LogEvent getLogEvent(String task) {
        return logs.get(task);
    }
    
    public static void clear() {
        logs.clear();
    }

    public static void clearOldLogs(long olderThanNanos) {
        logs.entrySet().removeIf(entry -> {
            LogEvent event = entry.getValue();
            return event.getLastEventTime() < (System.nanoTime() - olderThanNanos);
        });
    }
}