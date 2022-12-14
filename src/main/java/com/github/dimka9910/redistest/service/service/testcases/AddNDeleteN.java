package com.github.dimka9910.redistest.service.service.testcases;

import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.service.InitialDataCreator;
import com.github.dimka9910.redistest.service.service.RedisInteractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AddNDeleteN {

    @Autowired
    @Qualifier("RedisInteractorCrud")
    RedisInteractor redisInteractor;

    private final Integer MAX_THREADS = 5;

    private static AtomicInteger counter = new AtomicInteger(0);

    ConcurrentLinkedQueue<OrderHash> list = InitialDataCreator.generateInitialData();
    ConcurrentLinkedQueue<OrderHash> listToDelete = new ConcurrentLinkedQueue<>();

    public void run() throws Exception {
        log.info("start scenario");
        log.info("List size is " + list.size());
        log.info("----------- start test with " + MAX_THREADS + " threads -------------");

        CountDownLatch latch = new CountDownLatch(MAX_THREADS);
        redisInteractor.deleteAllOrders();
        redisInteractor.deleteAllStatuses();


        ExecutorService es = Executors.newFixedThreadPool(MAX_THREADS);
        for (int i = 1; i <= MAX_THREADS; i++) {
            es.submit(() -> {
                final long startTime = System.nanoTime();
                long saveTime = 0;
                long deleteTime = 0;
                long processedCounter = 0;
                long removedCounter = 0;

                while (!list.isEmpty()) {
                    try {

                        OrderHash orderHash = list.poll();
                        listToDelete.add(orderHash);
                        assert orderHash != null;

                        long tmp = System.nanoTime();
                        redisInteractor.put(orderHash);
                        while (redisInteractor.getByCompositeId(orderHash.getCompositeId()) == null) {
                        }
                        saveTime += System.nanoTime() - tmp;
                        processedCounter++;
                        counter.incrementAndGet();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        break;
                    }
                }

                log.info("\nTHREAD ID: " + Thread.currentThread().getId() + " adding is finished ");
                log.info("List size is " + listToDelete.size());


                while (!listToDelete.isEmpty()) {
                    try {
                        OrderHash orderHash = listToDelete.poll();
                        assert orderHash != null;

                        long tmp = System.nanoTime();

                        orderHash = redisInteractor.removeByCompositeId(orderHash.getCompositeId());
                        while (orderHash != null) {
                            orderHash = redisInteractor.getByCompositeId(orderHash.getCompositeId());
                        }

                        deleteTime += System.nanoTime() - tmp;
                        removedCounter++;
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        break;
                    }
                }


                log.info("\nTHREAD ID: " + Thread.currentThread().getId() +
                        "\nTOTAL PROCESSED BY THREAD: " + processedCounter +
                        "\nAVG SAVING TIME: " + saveTime / (1000000.0 * processedCounter) +
                        "\nAVG DELETING TIME: " + deleteTime / (1000000.0 * processedCounter) +
                        "\nPROCESSED: " + processedCounter +
                        "\nDELETED: " + removedCounter +
                        "\n ELEMENTS IN REDIS: " + counter);

                log.info(Thread.currentThread().getId() + " used: " + (System.nanoTime() - startTime) / 1_000_000_000.0);
                latch.countDown();
            });
        }
        es.shutdown();
        latch.await();
    }


}
