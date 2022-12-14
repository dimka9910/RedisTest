package com.github.dimka9910.redistest.rest;

import com.github.dimka9910.redistest.service.service.RedisInteractor;
import com.github.dimka9910.redistest.service.service.RedisService;
import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import com.github.dimka9910.redistest.service.redis.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/rest/redis")
@RequiredArgsConstructor
public class RedisRest {

    @Autowired
    RedisService redisService;

    @Autowired
    @Qualifier("RedisInteractorCrud")
    RedisInteractor redisInteractor;



    @Operation(description = "search")
    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public OrderHash getMessage(@PathVariable("id") String id) {
        return redisInteractor.getByCompositeId(id);
    }



    @Operation(description = "search")
    @GetMapping(value = "/size", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Long getSize() {
        return redisInteractor.getSize();
    }
}
