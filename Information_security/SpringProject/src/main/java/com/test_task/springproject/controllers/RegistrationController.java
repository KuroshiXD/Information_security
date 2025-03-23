package com.test_task.springproject.controllers;

import com.test_task.springproject.models.User;
import com.test_task.springproject.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Пароли не совпадают.");
            return "redirect:/register";
        }

        User user = new User(username, password, email);
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("message", "Регистрация прошла успешно. Войдите в систему.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            return "redirect:/register";
        }
    }
}