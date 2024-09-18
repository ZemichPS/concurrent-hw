package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.api.TopicService;
import service.impl.TopicServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    TopicService topicService = new TopicServiceImpl();
    private String topicName = "Breaking news";
    CountDownLatch latch;
    Topic topic;
    List<String> consumedMessages;
    ExecutorService executorService;


    @BeforeEach
    public void init() {
        topic = new Topic(topicName, 3);
        latch = new CountDownLatch(2);
        consumedMessages = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(2);
    }

    @Test
    @DisplayName("Should return topic name")
    void getName() {
        Assertions.assertEquals(topicName, topic.getName());
    }

    @Test
    @DisplayName("Topic must save message to internal storage and notify all consumers about new message")
    void publish() throws InterruptedException {
        String message = "Exploding walkie-talkies injure more than 100 in new attacks across Lebanon";
        topic.publish(message);
        Assertions.assertEquals(message, topic.consume(0));
    }

    @Test
    @DisplayName("Topic must return published message")
    void consume() throws InterruptedException {
        String message = "Locals urging us to put phones away";
        topic.publish(message);
        Assertions.assertEquals(message, topic.consume(0));
    }

    @Test
    @DisplayName("Topic must ")
    void acquireConsumerSlot() throws InterruptedException {

        Stream.generate(() -> new Consumer(topic, new Random().nextInt(), latch, consumedMessages))
                .limit(2)
                .forEach(executorService::submit);
        topic.publish("Exploding walkie-talkies kill nine and injure at least 300 in new attacks across Lebanon");
        latch.await();
        Assertions.assertEquals(2, consumedMessages.size());
    }

    @Test
    void releaseConsumerSlot() throws InterruptedException {
        Stream.generate(() -> new Consumer(topic, new Random().nextInt(), latch, consumedMessages))
                .limit(2)
                .forEach(executorService::submit);
        topic.publish("More explosions, more deaths, and more questions");
        latch.await();
        Assertions.assertEquals(2, consumedMessages.size());
    }
}
