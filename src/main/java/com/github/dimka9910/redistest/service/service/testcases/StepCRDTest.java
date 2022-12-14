package com.github.dimka9910.redistest.service.service.testcases;

import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.redis.models.OrderStatusHash;
import com.github.dimka9910.redistest.service.service.InitialDataCreator;
import com.github.dimka9910.redistest.service.service.RedisInteractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class StepCRDTest {


    @Autowired
    @Qualifier("RedisInteractorCrud")
    RedisInteractor redisInteractor;

    private final Integer MAX_THREADS = 5;

    private static AtomicInteger counter = new AtomicInteger(0);

    ConcurrentLinkedQueue<OrderHash> list = InitialDataCreator.generateInitialData();

    ConcurrentLinkedQueue<OrderHash> listToCheck = new ConcurrentLinkedQueue<>();

    public void run() throws Exception {
        log.info("start scenario");
        log.debug("List size is " + list.size());
        log.info("----------- start test with " + MAX_THREADS + " threads -------------");

        CountDownLatch latch = new CountDownLatch(MAX_THREADS);
        ConcurrentLinkedQueue<OrderHash> concurrentLinkedQueue = new ConcurrentLinkedQueue<>(list);
        redisInteractor.deleteAllOrders();
        redisInteractor.deleteAllStatuses();


        /**
         * // DOCKER - CRUD
         *
         TOTAL PROCESSED BY THREAD: 12629
         2022-12-06T23:10:42.281910100Z AVG SAVING TIME: 0.5128123366854066
         2022-12-06T23:10:42.281912500Z AVG SAVING2 TIME: 0.7069209596959379
         2022-12-06T23:10:42.281924700Z AVG DELETING TIME: 1.1794662522765065
         2022-12-06T23:10:42.281927400Z  ELEMENTS IN REDIS: 962664

         // local CRUD
         THREAD ID: 281
         TOTAL PROCESSED BY THREAD: 10000
         AVG SAVING TIME: 2.45280947
         AVG SAVING2 TIME: 3.30962175
         AVG DELETING TIME: 5.58325451
         ELEMENTS IN REDIS: 50057


         // local redis template ops for hash
         TOTAL PROCESSED BY THREAD: 10000
         AVG SAVING TIME: 0.77543967
         AVG SAVING2 TIME: 1.70005525
         AVG DELETING TIME: 2.23522582
         ELEMENTS IN REDIS: 950640

         // TWO HASHES LOCAL CRUD
         TOTAL PROCESSED BY THREAD: 2000
         AVG SAVING TIME: 2.47884065
         AVG SAVING2 TIME: 3.4177724
         AVG STATUS SAVING TIME: 3.35663555
         AVG DELETING TIME: 5.76818905
         AVG CHECK STATUS EXIST TIME: 0.9095535
         ELEMENTS IN REDIS: 60073

         20003) "Order:18902_68"
         20004) "Order:14032_80"
         20005) "OrderStatus:11082_58"
         20006) "Order:16298_134"
         20007) "Order:19928_88"

         // RedisTemplate OpsForHash
         TOTAL PROCESSED BY THREAD: 2000
         AVG SAVING TIME: 0.75764195
         AVG SAVING2 TIME: 1.5349357
         AVG STATUS SAVING TIME: 1.4362857
         AVG DELETING TIME: 2.2315365
         AVG CHECK STATUS EXIST TIME: 0.73393415
         ELEMENTS IN REDIS: 40012

         // CLUSTER CRUD
         TOTAL PROCESSED BY THREAD: 857
         2022-12-12T23:26:26.151982100Z AVG SAVING TIME: 0.4891253208868145
         2022-12-12T23:26:26.151986900Z AVG SAVING2 TIME: 0.6493669778296383
         2022-12-12T23:26:26.151991400Z AVG STATUS SAVING TIME: 0.6446040840140024
         2022-12-12T23:26:26.151995500Z AVG DELETING TIME: 1.0318717619603268
         2022-12-12T23:26:26.151999500Z AVG CHECK STATUS EXIST TIME: 0.15545519253208867


         */


        ExecutorService es = Executors.newFixedThreadPool(MAX_THREADS);

        for (int i = 1; i <= MAX_THREADS; i++) {
            es.submit(() -> {
                try {
                    final long startTime = System.nanoTime();
                    long saveTime = 0;
                    long saveTime2 = 0;

                    long statusSaving = 0;

                    long deleteTime = 0;
                    long existTime = 0;
                    long deleteTime2 = 0;
                    long checkInterval = 10000;
                    long processedCounter = 0;


                    while (!concurrentLinkedQueue.isEmpty()) {
                        while (!concurrentLinkedQueue.isEmpty() && processedCounter < checkInterval) {

                            //TWO SAVINGS
                            OrderHash orderHash = concurrentLinkedQueue.poll();
                            long tmp = System.nanoTime();

                            try {
                                tmp = System.nanoTime();
                                redisInteractor.put(orderHash);
                                saveTime += System.nanoTime() - tmp;
                                listToCheck.add(orderHash);
                            } catch (RedisSystemException e) {
                                log.error(e.getMessage(), e);
                                Thread.sleep(5000);
                            }


                            orderHash = concurrentLinkedQueue.poll();
                            if (orderHash != null) {
                                try {
                                    // one more saving
                                    tmp = System.nanoTime();
                                    redisInteractor.put(orderHash);
                                    OrderHash orderHash2 = null;
                                    while (orderHash2 == null) {
                                        orderHash2 = redisInteractor.getByCompositeId(orderHash.getCompositeId());
                                    }
                                    saveTime2 += System.nanoTime() - tmp;
                                } catch (RedisSystemException e) {
                                    log.error(e.getMessage(), e);
                                    Thread.sleep(5000);
                                }

                                OrderStatusHash orderStatusHash = new OrderStatusHash();
                                orderStatusHash.setOrderId(orderHash.getOrderId());
                                orderStatusHash.setVersionId(orderHash.getVersionId());
                                orderStatusHash.setInfo("COMPLETED");
                                orderStatusHash.setCompositeId();
                                try {
                                    // saving status
                                    tmp = System.nanoTime();
                                    redisInteractor.putStatus(orderStatusHash);
                                    while (!redisInteractor.existsStatusByCompositeId(orderStatusHash.getCompositeId())) {
                                    }
                                    statusSaving += System.nanoTime() - tmp;
                                    //
                                } catch (RedisSystemException e) {
                                    log.error(e.getMessage(), e);
                                    Thread.sleep(5000);
                                }

                                try {
                                    // ONE DELETING
                                    tmp = System.nanoTime();
                                    orderHash = redisInteractor.removeByCompositeId(orderHash.getCompositeId());
                                    while (redisInteractor.getByCompositeId(orderHash.getCompositeId()) != null) {
                                    }
                                    deleteTime += System.nanoTime() - tmp;
                                } catch (RedisSystemException e) {
                                    log.error(e.getMessage(), e);
                                    Thread.sleep(5000);
                                }

                                try {
                                    // check status
                                    tmp = System.nanoTime();
                                    boolean b = redisInteractor.existsStatusByCompositeId(orderStatusHash.getCompositeId());
                                    existTime += System.nanoTime() - tmp;
                                } catch (RedisSystemException e) {
                                    log.error(e.getMessage(), e);
                                    Thread.sleep(5000);
                                }


                                counter.incrementAndGet();
                                processedCounter++;
                            }
                        }

                        log.info("\nTHREAD ID: " + Thread.currentThread().getId() +
                                "\nTOTAL PROCESSED BY THREAD: " + processedCounter +
                                "\nAVG SAVING TIME: " + saveTime / (1000000.0 * processedCounter) +
                                "\nAVG SAVING2 TIME: " + saveTime2 / (1000000.0 * processedCounter) +
                                "\nAVG STATUS SAVING TIME: " + statusSaving / (1000000.0 * processedCounter) +
                                "\nAVG DELETING TIME: " + deleteTime / (1000000.0 * processedCounter) +
                                "\nAVG CHECK STATUS EXIST TIME: " + existTime / (1000000.0 * processedCounter) +
//                                "\nAVG EMPTY DELETING TIME: " + deleteTime / (1000000.0 * processedCounter) +
                                "\n ELEMENTS IN REDIS: " + counter);

                        saveTime = 0;
                        saveTime2 = 0;
                        statusSaving = 0;
                        deleteTime = 0;
                        existTime = 0;
                        deleteTime2 = 0;
                        processedCounter = 0;
                    }

                    log.info(Thread.currentThread().getId() + " used: " + (System.nanoTime() - startTime) / 1_000_000_000.0);
                    latch.countDown();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
        es.shutdown();
        latch.await();
    }


    @Scheduled(cron = "${notification-service.scheduled.cron:#{'0/20 * * * * ?'}}")
    public void autoSendScheduledNotifications() {
        Random rand = new Random();

        if (listToCheck.size() > 2000) {
            int startRangeToCheck = rand.nextInt(listToCheck.size() - 2000);
            log.info("testing data exist");
            listToCheck.stream().skip(startRangeToCheck).limit(2000).forEach(
                    v -> {
                        if (redisInteractor.getByCompositeId(v.getCompositeId()) == null)
                            log.error("element not found");
                    }
            );
            log.info("FINISHED testing data exist");
        }
    }

}
