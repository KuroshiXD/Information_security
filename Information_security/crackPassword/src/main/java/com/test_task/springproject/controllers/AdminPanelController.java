package com.test_task.springproject.controllers;

import com.test_task.springproject.models.Topic;
import com.test_task.springproject.models.User;
import com.test_task.springproject.services.AdminService;
import com.test_task.springproject.services.TopicService;
import com.test_task.springproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminPanelController {

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userService.findUserByUsername(userDetails.getUsername());
        currentUser.ifPresent(current_user -> model.addAttribute("current_user", current_user));
        Iterable<Topic> topics = topicService.findAllTopics();
        model.addAttribute("topics", topics);
        Iterable<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin_panel";
    }

    @GetMapping("/users/add_user")
    public String addUser() {
        return "admin_user_adding";
    }

    @GetMapping("/users/{id}/block")
    public String blockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.blockUser(id);
        redirectAttributes.addFlashAttribute("message", "Пользователь заблокирован");
        return "redirect:/admin/panel";
    }

    @GetMapping("/users/{id}/add_password_settings")
    public String showPasswordSettingsPage(@PathVariable Long id, Model model) {
        Optional<User> userOptional = userService.findUserById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);

            model.addAttribute("currentPatterns", user.getPatternsForPassword());
        } else {
            model.addAttribute("error", "Пользователь не найден");
        }

        return "admin_password_settings";
    }

    @GetMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.unblockUser(id);
        redirectAttributes.addFlashAttribute("message", "Пользователь разблокирован");
        return "redirect:/admin/panel";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "Пользователь удален");
        return "redirect:/admin/panel";
    }

    @GetMapping("/topics/{id}/delete")
    public String deleteTopic(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteTopic(id);
        redirectAttributes.addFlashAttribute("message", "Статья удалена");
        return "redirect:/admin/panel";
    }

    @GetMapping("/users/{id}/assign_admin")
    public String assignAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.findUserById(id);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.isPresent()) {
            if (user.get().isBlocked()) {
                redirectAttributes.addFlashAttribute("error",
                        "Нельзя сделать админом заблокированного пользователя!");
                return "redirect:/admin/panel";
            } else {
                adminService.assignAdminToUser(id, userDetails);
                redirectAttributes.addFlashAttribute("message",
                        "Пользователь " + user.get().getUsername() + " теперь админ!");
                return "redirect:/admin/panel";
            }
        }
        return "redirect:/admin/panel";
    }

    @GetMapping("/users/{id}/unassign_admin")
    public String unassignAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            adminService.unassignAdminToUser(id);
            redirectAttributes.addFlashAttribute("message", "Пользователь " + user.get().getUsername() + " больше не админ!");
            return "redirect:/admin/panel";
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/users/add_user")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          @RequestParam String role,
                          RedirectAttributes redirectAttributes, Model model) {
        Optional<User> userByUsername = userService.findUserByUsername(username);
        Optional<User> userByEmail = userService.findUserByEmail(email);
        if (userByUsername.isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь с таким ником уже существует!");
            return "redirect:/admin/panel";
        }
        if (userByEmail.isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь с такой почтой уже существует!");
            return "redirect:/admin/panel";
        }
        userService.registerUser(username, password, email, role);
        redirectAttributes.addFlashAttribute("message",
                "Пользователь создан!");
        return "redirect:/admin/panel";
    }

    @PostMapping("/users/{id}/add_password_settings")
    public String addPasswordSettings(
            @PathVariable Long id,
            @RequestParam(name = "patterns", required = false) String[] patterns,
            RedirectAttributes redirectAttributes) {

        try {
            List<String> newPatterns = (patterns != null)
                    ? new ArrayList<>(Arrays.asList(patterns))
                    : new ArrayList<>();

            adminService.updatePasswordPatterns(id, newPatterns);
            redirectAttributes.addFlashAttribute("message", "Паттерны успешно обновлены для пользователя!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/panel";
    }
}
