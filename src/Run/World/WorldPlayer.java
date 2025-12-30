package Run.World;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Logger.Logger;
import Run.Main;
import Run.GUI.GUI;

public class WorldPlayer implements Runnable {

    private final ExecutorService onceExecutor = Executors.newSingleThreadExecutor();
    private final World world;

    public WorldPlayer(World world, GUI gui) {
        this.world = world;
    }

    @Override
    public void run() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            if (Main.getTicksToRun() == 0) {
                Main.pause();
            }
            while (Main.isPlay() && (Main.getTicksToRun() == -1 || Main.getTicksToRun() > 0)) {
                int currentTick = world.getTickCount();
                Logger.logEvent("WorldPlayer Tick: " + currentTick, "Starting tick");
                long startTime = System.nanoTime();

                if (Main.getTicksToRun() > 0) {
                    Main.setTicksToRun(Main.getTicksToRun() - 1);
                    if (Main.getTicksToRun() == 0) {
                        Main.pause();
                    }
                }
                Logger.logEvent("WorldPlayer Tick: " + currentTick, "Checked ticksToRun");
                world.run();
                Logger.logEvent("WorldPlayer Tick: " + currentTick, "Ran world");
                Main.lastTickTime = System.nanoTime() - startTime;
                try {
                    Main.updateAfterTick(startTime);
                } catch (Exception e) {
                    e.printStackTrace();
                    Main.pause();
                    Logger.logError("WorldPlayer", "Exception during GUI update: " + e.getMessage());
                    executor.shutdown();
                    return;
                }
                System.out.println("Ticked! in: " + (Main.lastTickTime / 1_000_000_000.0) + " seconds");

                // Fix elapsed/sleep calculation to avoid overflow and unit mismatch
                long elapsedMs = (System.nanoTime() - startTime) / 1_000_000L;
                long sleepMs = (long) Main.getTargetMSPT() - elapsedMs;
                if (sleepMs > 0L) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        Main.pause();
                        Logger.logError("WorldPlayer", "Interrupted during sleep: " + e.getMessage());
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
        if (Main.getTicksToRun() > 0) {
            Main.setTicksToRun(Main.getTicksToRun() - 1);
        }
        if (Main.isPlay()) return;
        world.run();
        try {
            Main.lastTickTime = System.nanoTime() - startTime;
            Main.updateAfterTick(startTime);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logError("WorldPlayer Once", "Exception during GUI update: " + e.getMessage());
        }
        Main.lastTickTime = System.nanoTime() - startTime;
        System.out.println("Ticked! in: " + (Main.lastTickTime / 1_000_000.0) + " ms");
        System.out.println("Time since tick was invoked: " + ((System.nanoTime() - invokeTime) / 1_000_000.0) + " ms");
    }
}