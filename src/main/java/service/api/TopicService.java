package service.api;

import model.Topic;

public interface TopicService {
    void create(String name, int maxConsumers);
    Topic getByName(String name);
}
