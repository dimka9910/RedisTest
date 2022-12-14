package com.github.dimka9910.redistest.service.redis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("OrderStatus")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHash implements Serializable {  //Serializable requires for REDIS TEMPLATE

    @Id
    public String compositeId;
    Integer orderId;
    Integer versionId;
    String info;

    public void setCompositeId() {
        compositeId = orderId + "_" + versionId;
    }
}
