package com.test_task.springproject.controllers;

import com.test_task.springproject.services.CrackPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/crack")
public class CrackPasswordController {

    private final CrackPasswordService crackPasswordService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CrackPasswordController(CrackPasswordService crackPasswordService) {
        this.crackPasswordService = crackPasswordService;
    }

    @GetMapping("/password")
    public String showCrackPage() {
        return "crack_password";
    }

    @PostMapping("/analyze")
    public String analyzePassword(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam String method,
            @RequestParam(defaultValue = "4") int maxLength,
            Model model) {

        Map<String, Object> result = null;
        if (username != null && !username.isEmpty()) {
            result = crackPasswordService.crackUserPassword(username, method, maxLength);
        }

        model.addAllAttributes(result);
        return "crack_result";
    }
}