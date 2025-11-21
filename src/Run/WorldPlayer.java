package Run;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import Logger.Logger;

public class WorldPlayer implements Runnable {

   private final ExecutorService onceExecutor = Executors.newSingleThreadExecutor();
    
    @Override
    public void run() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            if (Main.ticksToRun == 0) {
                Main.play = false;
                GUI.getInstance().playPauseButton.setText(Main.play ? "Pause" : "Play");
            }
            while (Main.play && (Main.ticksToRun == -1 || Main.ticksToRun > 0)) {
                int currentTick = Main.world.getTickCount();
                Logger.logEvent("WorldPlayer Tick: " + currentTick, "Starting tick");
                long startTime = System.nanoTime();

                if (Main.ticksToRun > 0) {
                    Main.ticksToRun--;
                    if (Main.ticksToRun == 0) {
                        Main.play = false;
                        GUI.getInstance().playPauseButton.setText(Main.play ? "Pause" : "Play");
                    }
                }
                Logger.logEvent("WorldPlayer Tick: " + currentTick, "Checked ticksToRun");
                Main.world.run();
                Logger.logEvent("WorldPlayer Tick: " + currentTick, "Ran world");
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

    public void queueOneTick(long invokeTime) {
        onceExecutor.submit(() -> once(invokeTime));
    }

    private void once(long invokeTime) {
        long startTime = System.nanoTime();
        if (Main.ticksToRun > 0) {
            Main.ticksToRun--;
        }
        if (Main.play) return;
        Main.world.run();
        try {
            Main.lastTickTime = System.nanoTime() - startTime;
            SwingUtilities.invokeAndWait(() -> GUI.getInstance().updateAfterTick(startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Main.lastTickTime = System.nanoTime() - startTime;
        System.out.println("Ticked! in: " + (Main.lastTickTime / 1_000_000.0) + " ms");
        System.out.println("Time since tick was invoked: " + ((System.nanoTime() - invokeTime) / 1_000_000.0) + " ms");
    }
}