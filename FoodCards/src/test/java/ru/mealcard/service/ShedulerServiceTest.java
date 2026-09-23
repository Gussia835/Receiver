package ru.mealcard.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class ShedulerServiceTest {

    private final ShedulerService scheduler = ShedulerService.getInstance();

    @Test
    void testPastTimeRunsImmediately() {
        AtomicBoolean ran = new AtomicBoolean(false);
        scheduler.shedule(LocalDateTime.now().minusHours(1), () -> ran.set(true));
        await().atMost(2, SECONDS).untilTrue(ran);
    }

    @Test
    void testFutureTaskNotRunBeforeDelay() {
        AtomicBoolean ran = new AtomicBoolean(false);
        scheduler.shedule(
                LocalDateTime.now(ZoneId.of("Europe/Moscow")),
                () -> ran.set(true));

        await().during(200, TimeUnit.MILLISECONDS)
                .atMost(3, SECONDS)
                .untilTrue(ran);
    }

    @Test
    void testVeryFarFutureTaskNotRunNow() {
        AtomicBoolean ran = new AtomicBoolean(false);
        scheduler.shedule(
                LocalDateTime.now(ZoneId.of("Europe/Moscow")).plusHours(24),
                () -> ran.set(true));
        assertThat(ran.get()).isFalse();
    }

    @Test
    void testMultipleTasksScheduledIndependently() throws Exception {
        AtomicBoolean first = new AtomicBoolean(false);
        AtomicBoolean second = new AtomicBoolean(false);

        scheduler.shedule(LocalDateTime.now(ZoneId.of("Europe/Moscow")), () -> first.set(true));
        scheduler.shedule(LocalDateTime.now(ZoneId.of("Europe/Moscow")), () -> second.set(true));

        await().atMost(2, SECONDS).untilTrue(first);
        await().atMost(2, SECONDS).untilTrue(second);
    }

    @Test
    void testImmediateTaskExecutesRunnable() {
        AtomicBoolean ran = new AtomicBoolean(false);
        scheduler.shedule(LocalDateTime.now().minusHours(1), () -> ran.set(true));

        await().atMost(2, SECONDS).untilTrue(ran);
    }
}