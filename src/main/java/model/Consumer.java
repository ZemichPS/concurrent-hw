package model;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Consumer implements Runnable {
    private final Topic topic;
    private final int consumerId;
    private int lastReadIndex = 0;
    private final CountDownLatch latch;
    private final List<String> consumedMessages;

    public Consumer(Topic topic, int consumerId, CountDownLatch latch, List<String> consumedMessages) {
        this.topic = topic;
        this.consumerId = consumerId;
        this.latch = latch;
        this.consumedMessages = consumedMessages;
    }

    @Override
    public void run() {
        try {
            while (latch.getCount() > 0) {
                if (!topic.acquireConsumerSlot()) {
                    System.out.println("Consumer " + consumerId + " is waiting for free slot...");
                    Thread.sleep(100);
                    continue;
                }
                String message = topic.consume(lastReadIndex);
                consumedMessages.add(message);
                lastReadIndex++;
                System.out.println("Consumer " + consumerId + " read: " + message);
                latch.countDown();
                topic.releaseConsumerSlot();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
