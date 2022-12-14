package com.github.dimka9910.redistest.service.redis.repository;

import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<OrderHash, String> {
}
