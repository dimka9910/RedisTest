package com.github.dimka9910.redistest.service.service.impl;

import com.github.dimka9910.redistest.service.redis.models.CompositeId;
import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.redis.models.OrderStatusHash;
import com.github.dimka9910.redistest.service.redis.repository.OrderRepository;
import com.github.dimka9910.redistest.service.redis.repository.OrderStatusRepository;
import com.github.dimka9910.redistest.service.service.RedisInteractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component("RedisInteractorTemplate")
public class RedisInteractorTemplate implements RedisInteractor {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public final String HASH_NAME = "Order";
    public final String HASH_STATUS_NAME = "OrderStatus";

    @Override
    public OrderHash getByCompositeId(String compositeId) {
        return (OrderHash) redisTemplate.opsForHash().get(HASH_NAME, compositeId);
    }

    @Override
    public OrderHash removeByCompositeId(String compositeId) {
        OrderHash orderHash = (OrderHash) redisTemplate.opsForHash().get(HASH_NAME, compositeId);
        if (orderHash != null) {
            redisTemplate.opsForHash().delete(HASH_NAME, compositeId);
        }
        return orderHash;
    }

    @Override
    public void deleteByCompositeId(String compositeId) {
        redisTemplate.opsForHash().delete(HASH_NAME, compositeId);
    }

    @Override
    public void put(OrderHash orderHash) {
        redisTemplate.opsForHash().put(HASH_NAME,orderHash.getCompositeId(), orderHash);
    }

    @Override
    public boolean existsStatusByCompositeId(String compositeId) {
        return redisTemplate.opsForHash().hasKey(HASH_STATUS_NAME, compositeId);
    }

    @Override
    public void putStatus(OrderStatusHash orderStatusHash) {
        redisTemplate.opsForHash().put(HASH_STATUS_NAME, orderStatusHash.getCompositeId(), orderStatusHash);
    }

    @Override
    public void deleteAllOrders() {
    }

    @Override
    public void deleteAllStatuses() {
//        orderStatusRepository.deleteAll();
//        ;
    }

    @Override
    public Long getSize() {
        return redisTemplate.opsForHash().size(HASH_STATUS_NAME);
    }
}
