package Logger;

import java.util.HashMap;
import java.util.Map;

public class Logger {
    private static Map<String, LogEvent> logs = new HashMap<>();

    public static void logEvent(String task, String message) {
        if (!logs.containsKey(task)) {
            logs.put(task, new LogEvent(task));
        }
        LogEvent logEvent = logs.get(task);
        logEvent.logEvent(message);
    }

    public static LogEvent getLogEvent(String task) {
        return logs.get(task);
    }
}