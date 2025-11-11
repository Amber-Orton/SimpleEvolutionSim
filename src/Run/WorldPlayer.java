package Run;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldPlayer implements Runnable {
    
    @Override
    public void run() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            while (Main.play) {
                long startTime = System.currentTimeMillis();

                Main.world.run();

                long elapsedTime = System.currentTimeMillis() - startTime;
                int sleepMs = Main.tickMillis - (int) elapsedTime;
                if (sleepMs > 0) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        } finally {
            executor.shutdown();
        }
    }

    public void once() {
        Thread thread = new Thread(() -> Main.world.run());
        thread.start();
    }
}