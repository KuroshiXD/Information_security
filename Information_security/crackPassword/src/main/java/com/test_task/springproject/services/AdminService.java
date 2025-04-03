package com.test_task.springproject.services;

import com.test_task.springproject.models.Topic;
import com.test_task.springproject.models.User;
import com.test_task.springproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void blockUser(Long id) {
        Optional<User> user = userService.findUserById(id);
        user.ifPresent(value -> value.setBlocked(true));
        user.ifPresent(value -> userService.saveUser(value));
    }

    public void unblockUser(Long id) {
        Optional<User> user = userService.findUserById(id);
        user.ifPresent(value -> value.setBlocked(false));
        user.ifPresent(value -> userService.saveUser(value));
    }

    public void deleteUser(Long id) {
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
        }
    }

    public void deleteTopic(Long id) {
        Optional<Topic> topic = topicService.findTopicById(id);
        topic.ifPresent(value -> topicService.deleteTopic(id));
    }

    public void assignAdminToUser(Long id, UserDetails currentUser) {
        Optional<User> user = userService.findUserById(id);
        user.ifPresent(value -> {
            value.setRole("ROLE_ADMIN");
            Optional<User> currentUserOptional = userService.findUserByUsername(currentUser.getUsername());
            currentUserOptional.ifPresent(user1 -> value.setAssignedAdminBy(user1.getEmail()));
            userService.saveUser(value);
        });
    }

    public void unassignAdminToUser(Long id) {
        Optional<User> user = userService.findUserById(id);
        user.ifPresent(value -> {
            value.setRole("ROLE_USER");
            value.setAssignedAdminBy(null);
            userService.saveUser(value);
        });
    }

    public void updatePasswordPatterns(Long userId, List<String> newPatterns) {
        Optional<User> userOptional = userService.findUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            List<String> currentPatterns = user.getPatternsForPassword();
            if (currentPatterns != null) {
                currentPatterns.removeIf(pattern -> !newPatterns.contains(pattern));
            }

            user.setPatternsForPassword(newPatterns);

            userService.saveUser(user);
        } else {
            throw new IllegalArgumentException("Пользователь не найден");
        }
    }
}
