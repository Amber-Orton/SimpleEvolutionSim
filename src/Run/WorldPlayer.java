package Run;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

public class WorldPlayer implements Runnable {
    
    @Override
    public void run() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            while (Main.play && (Main.ticksToRun == -1 || Main.ticksToRun > 0)) {
                long startTime = System.nanoTime();

                if (Main.ticksToRun > 0) {
                    Main.ticksToRun--;
                    if (Main.ticksToRun == 0) {
                        Main.play = false;
                        GUI.getInstance().playPauseButton.setText(Main.play ? "Pause" : "Play");
                    }
                }
                Main.world.run();
                Main.lastTickTime = System.nanoTime() - startTime;
                try {
                    SwingUtilities.invokeAndWait(() -> GUI.getInstance().updateAfterTick(startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.play = false;
                    GUI.getInstance().playPauseButton.setText(Main.play ? "Pause" : "Play");
                    executor.shutdown();
                    return;
                }
                System.out.println("Ticked! in: " + (Main.lastTickTime / 1_000_000_000.0) + " seconds");

                // Fix elapsed/sleep calculation to avoid overflow and unit mismatch
                long elapsedMs = (System.nanoTime() - startTime) / 1_000_000L;
                long sleepMs = (long) Main.tickMillis - elapsedMs;
                if (sleepMs > 0L) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        Main.play = false;
                        GUI.getInstance().playPauseButton.setText(Main.play ? "Pause" : "Play");
                        executor.shutdown();
                        return;
                    }
                }
            }
        } finally {
            executor.shutdown();
        }
    }

    public void once() {
        if (Main.ticksToRun > 0) {
            Main.ticksToRun--;
        }
        if (Main.play) return;
        Thread thread = new Thread(() -> Main.world.run());
        thread.start();
    }
}