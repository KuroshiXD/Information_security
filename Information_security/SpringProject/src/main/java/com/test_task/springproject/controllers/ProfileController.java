package com.test_task.springproject.controllers;

import com.test_task.springproject.models.Topic;
import com.test_task.springproject.models.User;
import com.test_task.springproject.services.TopicService;
import com.test_task.springproject.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final TopicService topicService;

    public ProfileController(UserService userService, TopicService topicService) {
        this.userService = userService;
        this.topicService = topicService;
    }

    @GetMapping("/{id}")
    public String userProfile(@PathVariable Long id, Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails != null && userDetails.getUsername() != null) {
            Optional<User> user = userService.findUserByUsername(userDetails.getUsername());
            if (user.isPresent() && user.get().getId().equals(id)) {
                Iterable<Topic> topics = topicService.findTopicByAuthorId(id);
                model.addAttribute("topics", topics);
                model.addAttribute("user", user.get());
                return "profile";
            }
        }
        return "profile";
    }
}
