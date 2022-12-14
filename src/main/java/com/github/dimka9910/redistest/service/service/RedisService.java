package com.github.dimka9910.redistest.service.service;

import com.github.dimka9910.redistest.dto.DialogDto;
import com.github.dimka9910.redistest.mapper.MessageMapper;
import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.redis.repository.OrderRepository;
import com.github.dimka9910.redistest.service.service.testcases.AddNDeleteN;
import com.github.dimka9910.redistest.service.service.testcases.StepCRDTest;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService implements CommandLineRunner {

    @Autowired
    StepCRDTest stepCRDTest;
    @Autowired
    AddNDeleteN addNDeleteN;

    @Override
    public void run(String... args) throws Exception {
        try {
            RedisURI redisUri = RedisURI.Builder.redis("redis-cluster").build();

            RedisClusterClient clusterClient = RedisClusterClient.create(redisUri);
            StatefulRedisClusterConnection<String, String> connection = clusterClient.connect();
            RedisAdvancedClusterCommands<String, String> syncCommands = connection.sync();

            log.info(syncCommands.dbsize().toString());
            log.info(syncCommands.set("key1", "val1"));
            log.info(syncCommands.set("key2", "val2"));
            log.info(syncCommands.get("key1"));
            log.info(syncCommands.get("key2"));

            connection.close();
            clusterClient.shutdown();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


}
