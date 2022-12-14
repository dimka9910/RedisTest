package com.github.dimka9910.redistest.service.redis.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class CompositeId {

    Integer orderId;
    Integer versionId;

    public CompositeId(Integer orderId, Integer versionId) {
        this.orderId = orderId;
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        return orderId + "_" + versionId;
    }
}
