package Logger;

import java.util.ArrayList;
import java.util.List;

public class LogEvent {
    private String task;
    private List<Long> timestamps;
    private List<String> messages;

    protected LogEvent(String task) {
        this.task = task;
        this.timestamps = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    protected void logEvent(String message) {
        timestamps.add(System.nanoTime());
        messages.add(message);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LogEvent for task: ").append(task).append("\n");
        for (int i = 0; i < timestamps.size(); i++) {
            sb.append("Timestamp: ").append(timestamps.get(i)).append("ns, Message: ").append(messages.get(i)).append("\n");
        }
        return sb.toString();
    }

    public long getLastEventTime() {
        if (timestamps.isEmpty()) {
            return -1;
        }
        return timestamps.get(timestamps.size() - 1);
    }
}