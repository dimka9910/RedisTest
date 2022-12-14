package com.github.dimka9910.redistest.service.redis.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHash implements Serializable {


    @Id
    public String compositeId;
    Integer orderId;
    Integer versionId;
    String data;

    public void setCompositeId() {
        compositeId = orderId + "_" + versionId;
    }
}
