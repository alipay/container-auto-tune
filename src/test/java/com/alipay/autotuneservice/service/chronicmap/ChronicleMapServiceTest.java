package com.alipay.autotuneservice.service.chronicmap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@SpringBootTest
class ChronicleMapServiceTest {

    @Autowired
    ChronicleMapService cache;

    @Test
    void get() throws InterruptedException {
        String key = "hello";
        String val = "world";
        cache.setNx(key, val,10, TimeUnit.MILLISECONDS );
        assertEquals(val, cache.get(key));
        cache.setNx(key, "world1",10, TimeUnit.MILLISECONDS );
        assertEquals(cache.get(key), "world");

        TimeUnit.MILLISECONDS.sleep(11);
        assertEquals(null, cache.get(val));



        String val2 = "world2";

        cache.setEx(key, val2, 10, TimeUnit.MILLISECONDS);
        assertEquals(val2, cache.get(key));
    }

    @Test
    void setNx() {
    }

    @Test
    void setEx() {
    }
}