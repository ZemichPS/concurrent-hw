import model.Consumer;
import model.Topic;
import service.api.TopicService;
import service.impl.TopicServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        TopicService topicService = new TopicServiceImpl();
        topicService.create("news", 2);
        Topic topic = topicService.getByName("news");

        CountDownLatch latch = new CountDownLatch(16);
        List<String> consumedMessages = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Stream.generate(() -> new Consumer(topic,
                        random.nextInt(1, 10_000),
                        latch,
                        consumedMessages))
                .limit(2)
                .forEach(executorService::submit);

        getNews().forEach(
                news -> {
                    try {
                        sleep(random.nextInt(800, 3_800));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    topic.publish(news);
                }
        );

        latch.await();
        executorService.shutdown();
        System.out.println("All news were read");
    }

    private static List<String> getNews() {
        return """
                Some more details about the Taiwanese firm Gold Apollo that we just mentioned, who claims the pagers used in yesterday's attack in Lebanon were made by a Hungarian firm called BAC Consulting.
                Gold Apollo's founder Hsu Ching-Kuang said his company had signed an agreement with BAC three years ago, adding that money transfers from them had been "very strange".
                There had been problems with the payments which had come through the Middle East he said, but didn't go into further detail.
                Gold Apollo also added that while BAC had licensed their name, it had "no involvement in the design or manufacturing of the product".
                Taiwan’s manufacturing system is a complex maze of small companies, many of which do not actually make the products they sell.
                They may own the brand name, the intellectual property and have research and design departments.
                But most of the actual manufacturing is farmed out to factories in China or Southeast Asia.
                Earlier today - prior to reports of exploding walkie-talkies in southern Lebanon - I spoke to Dr Nour El Osta, from the Hotel Dieu Hospital in Beirut, about what she saw following Tuesday's pager attacks.
                It was a normal day at the beginning of the day, until it wasn’t anymore," she told me. 
                It unfortunately reminded us of the 4 August 2020 explosion [when more than 200 people were killed in Beirut port] but it was also different."
                """
                .lines()
                .toList();
    }

}
