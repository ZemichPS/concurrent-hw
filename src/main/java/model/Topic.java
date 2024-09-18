package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;

public class Topic {

    private final String name;
    private final List<String> messages = new ArrayList<>();

    private final Lock lock = new ReentrantLock();
    private final Condition newMessageCondition = lock.newCondition();

    private final Semaphore slots;

    public Topic(String name, int maxConsumers) {
        this.name = name;
        this.slots = new Semaphore(maxConsumers);
    }

    public String getName() {
        return name;
    }

    public void publish(String message) {
        lock.lock();
        try {
            messages.add(message);
            newMessageCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public String consume(int lastReadIndex) throws InterruptedException {
        slots.acquire();
        try {
            lock.lock();
            try {
                while (lastReadIndex >= messages.size()) {
                    while (lastReadIndex >= messages.size()) {
                        newMessageCondition.await();
                    }
                }
                return messages.get(lastReadIndex);
            } finally {
                lock.unlock();
            }
        } finally {
            slots.release();
        }
    }
}