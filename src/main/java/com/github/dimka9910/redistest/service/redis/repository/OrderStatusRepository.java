package com.github.dimka9910.redistest.service.redis.repository;

import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.redis.models.OrderStatusHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends CrudRepository<OrderStatusHash, String> {
}
