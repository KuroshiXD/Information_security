package com.infsectask.passwordanalyzer.Controller;


import com.infsectask.passwordanalyzer.Service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyzePassword(
            @RequestParam("password") String password,
            @RequestParam("attemptsPerSecond") int attemptsPerSecond,
            @RequestParam("attemptsBeforePause") int attemptsBeforePause,
            @RequestParam("pauseTime") int pauseTime,
            Model model) {

        int alphabetSize = passwordService.calculateAlphabetSize(password);
        long combinations = passwordService.calculateCombinations(password.length(), alphabetSize);
        double bruteForceTime = passwordService.estimateBruteForceTime(combinations, attemptsPerSecond, pauseTime, attemptsBeforePause);
        String formattedTime = passwordService.formatTime(bruteForceTime);

        model.addAttribute("password", password);
        model.addAttribute("alphabetSize", alphabetSize);
        model.addAttribute("combinations", combinations);
        model.addAttribute("bruteForceTime", formattedTime);

        return "index";
    }
}
