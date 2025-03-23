package com.test_task.springproject.controllers;

import com.test_task.springproject.models.User;
import com.test_task.springproject.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/custom-redirect")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.loginUser(username, password);
        if (user.isPresent()) {
            if (user.get().isBlocked()) {
                redirectAttributes.addFlashAttribute("error", "Ваш аккаунт заблокирован.");
                return "redirect:/login";
            }
            return "redirect:/welcome/" + username;
        } else {
            redirectAttributes.addFlashAttribute("error", "Неверное имя пользователя или пароль.");
            return "redirect:/login";
        }
    }
}