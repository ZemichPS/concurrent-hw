package service.impl;

import model.Topic;
import service.api.TopicService;

import java.util.HashMap;
import java.util.Map;

public class TopicServiceImpl implements TopicService {

    private final Map<String, Topic> topics = new HashMap<>();

    @Override
    public void create(String name, int maxConsumers) {
        Topic topic = new Topic(name, maxConsumers);
        topics.put(name, topic);
    }

    @Override
    public Topic getByName(String name) {
        return topics.computeIfAbsent(name, key -> {
            throw new RuntimeException("Topic with name %s not found".formatted(key));
        });
    }
}
