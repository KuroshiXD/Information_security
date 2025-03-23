package com.test_task.springproject.controllers;

import com.test_task.springproject.models.Topic;
import com.test_task.springproject.models.User;
import com.test_task.springproject.services.TopicService;
import com.test_task.springproject.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;
    private final UserService userService;

    public TopicController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping
    public String allTopics(Model model) {
        Iterable<Topic> allTopics = topicService.findAllTopics();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userService.findUserByUsername(userDetails.getUsername());

        if (user.isPresent()) { model.addAttribute("user", user.get()); } else { return "error"; }
        model.addAttribute("topics", allTopics);
        return "topics";
    }

    @GetMapping("/create-topic")
    public String showTopicForm() { return "create_topic"; }

    @PostMapping("/create-topic")
    public String createTopic(@RequestParam String title,
                              @RequestParam String content) {
        Topic topic = new Topic(title, content);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        topicService.createTopic(topic, userDetails.getUsername());
        return "redirect:/topics";
    }

    @GetMapping("/{id}")
    public String topicContent(@PathVariable Long id, Model model) {
        Optional<Topic> topic = topicService.findTopicById(id);
        if (topic.isPresent()) {
            model.addAttribute("topic", topic.get());
        } else { return "error"; }
        return "topic_content";
    }
}
