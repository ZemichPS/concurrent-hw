import model.Consumer;
import model.Topic;
import service.api.TopicService;
import service.impl.TopicServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        TopicService topicService = new TopicServiceImpl();
        topicService.create("news", 5);
        Topic topic = topicService.getByName("news");

        CountDownLatch latch = new CountDownLatch(5);
        List<String> consumedMessages = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Stream.generate(() -> new Consumer(topic, new Random().nextInt(), latch, consumedMessages))
                .limit(3)
                .forEach(executorService::submit);


        getNews().forEach(topic::publish);

        latch.await();
        System.out.println("All news were read");
    }

    private static List<String> getNews() {
        return """
                Some more details about the Taiwanese firm Gold Apollo that we just mentioned, who claims the pagers used in yesterday's attack in Lebanon were made by a Hungarian firm called BAC Consulting.
                Gold Apollo's founder Hsu Ching-Kuang said his company had signed an agreement with BAC three years ago, adding that money transfers from them had been "very strange".
                There had been problems with the payments which had come through the Middle East he said, but didn't go into further detail.
                Gold Apollo also added that while BAC had licensed their name, it had "no involvement in the design or manufacturing of the product".
                Taiwan’s manufacturing system is a complex maze of small companies, many of which do not actually make the products they sell.
                They may own the brand name, the intellectual property and have research and design departments. But most of the actual manufacturing is farmed out to factories in China or Southeast Asia.
                """
                .lines()
                .toList();
    }

}
