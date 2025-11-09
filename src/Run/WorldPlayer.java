package Run;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldPlayer implements Runnable {
    
    @Override
    public void run() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            while (Main.play) {
                synchronized (Main.world) {
                    while (!Main.world.readyToTick) {
                        try {
                            Main.world.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    Main.world.readyToTick = false;
                }

                executor.submit(Main.world::run);


                int sleepMs = Main.tickMillis;
                if (sleepMs > 0) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } // if 0, run as fast as readyToTick allows (no sleep)
            }
        } finally {
            executor.shutdown();
        }
    }
}