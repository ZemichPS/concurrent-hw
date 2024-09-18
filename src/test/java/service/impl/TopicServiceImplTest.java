package service.impl;

import model.Topic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.api.TopicService;

import static org.junit.jupiter.api.Assertions.*;

class TopicServiceImplTest {

    private final TopicService topicService = new TopicServiceImpl();

    @Test
    @DisplayName("Service must create and save new Topic")
    void create() {
        topicService.create("breaking news", 2);
        Assertions.assertEquals("breaking news", topicService.getByName("breaking news").getName());
    }

    @Test
    @DisplayName("Service must return exists topic by name")
    void getByName() {
        topicService.create("orderCreatedEvent", 2);
        Assertions.assertNotNull(topicService.getByName("orderCreatedEvent"));
    }
}