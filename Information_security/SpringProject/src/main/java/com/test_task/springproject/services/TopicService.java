package com.test_task.springproject.services;

import com.test_task.springproject.models.Topic;
import com.test_task.springproject.models.User;
import com.test_task.springproject.repositories.TopicRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final UserService userService;

    public TopicService(TopicRepository topicRepository, UserService userService) {
        this.topicRepository = topicRepository;
        this.userService = userService;
    }

    public Topic saveTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }

    public Optional<Topic> findTopicById(Long id) {
        return topicRepository.findById(id);
    }

    public List<Topic> findAllTopics() {
        return topicRepository.findAll();
    }

    public Topic createTopic(Topic topic, String username) {
        User currentUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        topic.setAuthor(currentUser);
        return saveTopic(topic);
    }

    public Iterable<Topic> findTopicByAuthorId(Long id) { return topicRepository.findByAuthorId(id); }
}
