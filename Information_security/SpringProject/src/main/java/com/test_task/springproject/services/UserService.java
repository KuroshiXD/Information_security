package com.test_task.springproject.services;

import com.test_task.springproject.models.User;
import com.test_task.springproject.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getUsername().equals("ADMIN")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setBlocked(false);
        user.setPasswordRestrictionsEnabled(false);
        user.setFailedLoginAttempts(0);
        return saveUser(user);
    }

    public User registerUser(String username, String password, String email, String role) {
        User user = new User(username, passwordEncoder.encode(password), email, role);
        return saveUser(user);
    }

    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = findUserByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            user.get().setFailedLoginAttempts(0);
            saveUser(user.get());
            return user;
        } else if (user.isPresent()) {
            handleFailedLogin(user.get());
        }
        return Optional.empty();
    }

    public void handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= 3) {
            user.setBlocked(true);
        }
        saveUser(user);
    }

    public String changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> user = findUserByUsername(username);

        if (user.isEmpty()) {
            return "Пользователь не найден";
        }

        if (!passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            return "Неверный старый пароль";
        }

        if (!isPasswordValid(newPassword, user.get())) {
            return "Новый пароль не соответствует требованиям";
        }

        user.get().setPassword(passwordEncoder.encode(newPassword));
        user.get().setUpdatedAt(LocalDateTime.now());
        saveUser(user.get());

        return null;
    }

    public boolean isPasswordValid(String newPassword, User user) {

        List<String> patterns = user.getPatternsForPassword();

        if (patterns == null || patterns.isEmpty()) {
            return true;
        }

        for (String pattern : patterns) {
            switch (pattern) {
                case "lowercase":
                    if (!newPassword.matches(".*[a-z].*")) {
                        return false;
                    }
                    break;
                case "uppercase":
                    if (!newPassword.matches(".*[A-Z].*")) {
                        return false;
                    }
                    break;
                case "numbers":
                    if (!newPassword.matches(".*\\d.*")) {
                        return false;
                    }
                    break;
                case "specialChars":
                    if (!newPassword.matches(".*[!@#$%^&*].*")) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}