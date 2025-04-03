package com.test_task.springproject.controllers;

import com.test_task.springproject.models.User;
import com.test_task.springproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/password_changing")
public class ChangePasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping
    public String changePassword(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userService.findUserByUsername(userDetails.getUsername());
        user.ifPresent(value -> model.addAttribute("user", value));
        return "change_password";
    }

    @PostMapping
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails != null && userDetails.getUsername() != null) {
            Optional<User> user = userService.findUserByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Новый пароль и подтверждение не совпадают.");
                    return "redirect:/password_changing";
                }

                String errorMessage = userService.changePassword(userDetails.getUsername(), oldPassword, newPassword);

                if (errorMessage != null) {
                    redirectAttributes.addFlashAttribute("error", errorMessage);
                    return "redirect:/password_changing";
                } else {
                    redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен.");
                    return "redirect:/profile/" + user.get().getId();
                }
            }
        }

        redirectAttributes.addFlashAttribute("error", "Ошибка при смене пароля.");
        return "redirect:/password_changing";
    }
}
