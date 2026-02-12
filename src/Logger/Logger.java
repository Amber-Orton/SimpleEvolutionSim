package Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import Main.Main;

public class Logger {
    private static final Map<String, LogEvent> logs = new ConcurrentHashMap<>();
    private static final String LOG_DIR = "log";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Logger.class.getName());
    private static FileHandler fileHandler;

    private static boolean inDepthDebugMode;
    private static boolean autoDebug;
    private static boolean loggingEnabled;
    
    public static void initialize() {
        inDepthDebugMode = Main.isSTART_IN_INDEPTH_DEBUG_MODE();
        loggingEnabled = Main.isSTART_WITH_LOGGING_ENABLED();
        autoDebug = Main.isSTART_IN_AUTO_DEBUG_MODE();
        
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
        if (!inDepthDebugMode || !loggingEnabled) return;
        LOGGER.info(String.format("[%s] %s", task, message));
    }
    
    public static void logError(String task, String message) {
        if (!loggingEnabled) return;
        LOGGER.severe(String.format("[%s] %s", task, message));
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

    public static String getMostRecentDebugInfo(int numberOfEvents) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Latest Debug Info ===\n");
        List<LogEvent> events = new ArrayList<>(logs.values());
        int start = Math.max(0, events.size() - numberOfEvents);
        for (int i = start; i < events.size(); i++) {
            sb.append(events.get(i).toString()).append("\n");
        }
        sb.append("=== End of Debug Info ===");
        return sb.toString();
    }

    public static boolean isInDepthDebugMode() {
        return inDepthDebugMode;
    }

    public static void setInDepthDebugMode(boolean inDepthDebugMode) {
        Logger.inDepthDebugMode = inDepthDebugMode;
    }

    public static boolean isAutoDebug() {
        return autoDebug;
    }

    public static void setAutoDebug(boolean autoDebug) {
        Logger.autoDebug = autoDebug;
    }

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public static void setLoggingEnabled(boolean loggingEnabled) {
        Logger.loggingEnabled = loggingEnabled;
    }
}