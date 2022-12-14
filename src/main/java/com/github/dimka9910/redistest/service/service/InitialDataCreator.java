package com.github.dimka9910.redistest.service.service;

import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InitialDataCreator {

    public static ConcurrentLinkedQueue<OrderHash> generateInitialData() {
        Random rand = new Random();
        LinkedList<OrderHash> list = new LinkedList<>();
        for (int i = 10001; i <= 20000; i++) {
            int maxV = rand.nextInt(100) + 50;
            for (int j = 1; j < maxV; j++) {
                OrderHash messageHash = new OrderHash();
                messageHash.setOrderId(i);
                messageHash.setVersionId(j);
                messageHash.setData("safdddddddddddddddddddddasdfasdfwuh21o3r7yb07840b7f8c340rf8734yrb8v773tvb9234rc32b8reuwlikuhcoiwry934icy3298ytbasafdddddddddddddddddddddasdfasdfwuh2assasaasas1o3r7yb07840b7f8c340rf8734yrb8v773tvb9234rc32b8reuwlikuhcoiwry9348cyetriytb3cbo3w4ytitewcrubyotuiwecbtuicy3298ytbasafdddddddddddddddddddddasdfasdfwuh21o3r7yb07840b7f8c340rf8734yrb8v773tvb9234rc32b8reuwlikuhcoiwry9348cyetriytb3cbo3w4ytitewcrubyotuiwecbtuicy3298ytbasafdddddddddddddddddddddasdfasdfwuh21o3r7yb07840b7f8c340rf8734yrb8v773tvb9234rc32b8reuwlikuhcoiwry9348cyetriytb3cbo3w4ytitewcrubyotuiwecbtuicy3298ytbasafddddddddddddddddddddddasdfasdfwuh21o3r7yb07840b7f8c340rf8734yrb8v773tvb9234rc32b8reuwlikuhcoiwry9348cyetriytb3cbo3w4ytitewcrubyotuiwecbtuicy3298ytbasafdddddddddddddddddddddasdfasdfwuh21o3r7yb07840b7f8c340rf8734yrb8v773tvb9234rc32b8reuwlikuhcoiwry9348cyetriytb3cbo3w4ytitewcrubyotuiwecbtuicy3298ytbasafdddddddddddddddddddddasdfasdfwuh21o3r7yb07840b7f8c340rf8734yrb8v773tvb9234rc32b8reuwlikuhcoiwry9348cyetriytb3cbo3w4ytitewcrubyotuiwecbtuicy3298ytbad");
                messageHash.setCompositeId();
                list.add(messageHash);
            }
        }

        Collections.shuffle(list);
        return new ConcurrentLinkedQueue<>(list);
    }

}
