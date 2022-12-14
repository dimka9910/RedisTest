package com.github.dimka9910.redistest.service.service;

import com.github.dimka9910.redistest.service.redis.models.CompositeId;
import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.redis.models.OrderStatusHash;

public interface RedisInteractor {

    OrderHash getByCompositeId(String compositeId);

    OrderHash removeByCompositeId(String compositeId);

    void deleteByCompositeId(String compositeId);

    void put(OrderHash messageHash);

    boolean existsStatusByCompositeId(String compositeId);
    void putStatus(OrderStatusHash orderStatusHash);

    void deleteAllOrders();
    void deleteAllStatuses();

    Long getSize();
}
