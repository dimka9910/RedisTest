package com.github.dimka9910.redistest.service.service.impl;

import com.github.dimka9910.redistest.service.redis.models.CompositeId;
import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.redis.models.OrderStatusHash;
import com.github.dimka9910.redistest.service.redis.repository.OrderRepository;
import com.github.dimka9910.redistest.service.redis.repository.OrderStatusRepository;
import com.github.dimka9910.redistest.service.service.RedisInteractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("RedisInteractorCrud")
public class RedisInteractorCrud implements RedisInteractor {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Override
    public OrderHash getByCompositeId(String compositeId) {
        return orderRepository.findById(compositeId).orElse(null);
    }

    @Override
    public OrderHash removeByCompositeId(String compositeId) {
        OrderHash orderHash = orderRepository.findById(compositeId).orElse(null);
        if (orderHash != null) {
            orderRepository.deleteById(compositeId);
        }
        return orderHash;
    }

    @Override
    public void deleteByCompositeId(String compositeId) {
        orderRepository.deleteById(compositeId);
    }

    @Override
    public void put(OrderHash orderHash) {
        orderRepository.save(orderHash);
    }

    @Override
    public boolean existsStatusByCompositeId(String compositeId) {
        return orderStatusRepository.existsById(compositeId);
    }


    @Override
    public void putStatus(OrderStatusHash orderStatusHash) {
        orderStatusRepository.save(orderStatusHash);
    }

    @Override
    public void deleteAllOrders() {
        orderRepository.deleteAll();
        ;
    }

    @Override
    public void deleteAllStatuses() {
        orderStatusRepository.deleteAll();
        ;
    }

    @Override
    public Long getSize() {
        return orderRepository.count();
    }
}
