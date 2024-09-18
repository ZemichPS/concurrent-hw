package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerTest {

    private Consumer consumer;
    private Topic topic;
    private List<String> eventList = new ArrayList<>();
    private CountDownLatch latch = new CountDownLatch(1);

    @BeforeEach
    void init(){
        topic = new Topic("OrderCanceledEvent", 1);
        consumer = new Consumer(topic, 14, latch, eventList);
    }

    @Test
    @DisplayName("Consumer must consume 1 message and put it to message list")
    void run() throws InterruptedException {
        Thread thread = new Thread(consumer);
        thread.start();
        topic.publish("id: 154, reason: the buyer changed his mind");
        latch.await();
        Assertions.assertEquals(1, eventList.size());
    }
}