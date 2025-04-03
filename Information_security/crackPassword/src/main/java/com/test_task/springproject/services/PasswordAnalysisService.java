package com.test_task.springproject.services;

import org.springframework.stereotype.Service;

@Service
public class PasswordAnalysisService {
    public int calculateAlphabetSize(String password) {
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        int alphabetSize = 0;
        if (hasLowercase) alphabetSize += 26;
        if (hasUppercase) alphabetSize += 26;
        if (hasDigit) alphabetSize += 10;
        if (hasSpecial) alphabetSize += 33;

        return alphabetSize;
    }

    public long calculateCombinations(int passwordLength, int alphabetSize) {
        if (alphabetSize == 0 || passwordLength == 0) {
            return 0;
        }
        return (long) Math.pow(alphabetSize, passwordLength);
    }

    public double estimateBruteForceTime(long combinations, int attemptsPerSecond, int pauseTime, int attemptsBeforePause) {
        if (attemptsPerSecond <= 0) {
            attemptsPerSecond = 1_000_000; // значение по умолчанию
        }
        if (attemptsBeforePause <= 0) {
            return combinations / (double) attemptsPerSecond;
        }

        long totalPauses = (combinations - 1) / attemptsBeforePause;
        return (double) combinations / attemptsPerSecond + (totalPauses * pauseTime);
    }

    public String formatTime(double seconds) {
        if (seconds <= 0) {
            return "мгновенно";
        }

        long sec = (long) seconds;
        if (sec < 60) {
            return "менее минуты";
        }

        long minutes = sec / 60;
        sec %= 60;
        long hours = minutes / 60;
        minutes %= 60;
        long days = hours / 24;
        hours %= 24;
        long months = days / 30;
        days %= 30;
        long years = months / 12;
        months %= 12;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append(" лет ");
        if (months > 0) sb.append(months).append(" месяцев ");
        if (days > 0) sb.append(days).append(" дней ");
        if (hours > 0) sb.append(hours).append(" часов ");
        if (minutes > 0) sb.append(minutes).append(" минут ");
        if (sec > 0) sb.append(sec).append(" секунд");

        return sb.toString().trim();
    }
}