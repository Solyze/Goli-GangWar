package io.github.solyze.goligangwar.utility;

import io.github.solyze.goligangwar.GoliGangWar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public interface Countdown {

    default int[] getBroadcastNumbers() {
        return new int[] { 1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 60, 120, 180, 240, 300 };
    }
    int getSeconds();
    void onBroadcast(int seconds);
    void onComplete();
    default void start() {
        AtomicInteger countAtomic = new AtomicInteger(getSeconds());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                int count = countAtomic.get();
                for (int broadcastNumber : getBroadcastNumbers()) {
                    if (broadcastNumber == count) {
                        onBroadcast(count);
                    }
                }
                int newCount = countAtomic.decrementAndGet();
                if (newCount < 0) {
                    cancel();
                    onComplete();
                }
            }
        };
        runnable.runTaskTimer(GoliGangWar.getInstance(), 0L, 20L);
    }

}
