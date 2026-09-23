package ru.mealcard.service;

import ru.mealcard.Base;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShedulerService extends Base {

    private static final ShedulerService instance = new ShedulerService();
    public static ShedulerService getInstance() { return instance; }

    private final ScheduledExecutorService sheduler = Executors.newScheduledThreadPool(5);

    private ShedulerService() {
        info("ShedulerService initialized with 5 threads");
    }

    public void shedule(LocalDateTime sheduledTime, Runnable task) {
        long diff = Duration.between(
                LocalDateTime.now(ZoneId.of(getConfig().getZone())),
                sheduledTime).toMillis();

        if (diff > 0) {
            sheduler.schedule(task, diff, TimeUnit.MILLISECONDS);
            info("Task sheduled for {} (delay {} ms)", sheduledTime, diff);
        } else {
            task.run();
        }
    }
}