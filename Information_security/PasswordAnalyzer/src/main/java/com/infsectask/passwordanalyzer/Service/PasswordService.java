package com.infsectask.passwordanalyzer.Service;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {

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
        if (hasLowercase) {
            alphabetSize += 26;
        }
        if (hasUppercase) {
            alphabetSize += 26;
        }
        if (hasDigit) {
            alphabetSize += 10;
        }
        if (hasSpecial) {
            alphabetSize += 33;
        }

        return alphabetSize;
    }

    public long calculateCombinations(int passwordLength, int alphabetSize) {
        return (long) Math.pow(alphabetSize, passwordLength);
    }

    public double estimateBruteForceTime(long combinations, int attemptsPerSecond, int pauseTime, int attemptsBeforePause) {
        long totalPauses = (combinations - 1) / attemptsBeforePause;
        double totalTime = (double) combinations / attemptsPerSecond + (totalPauses * pauseTime);
        return totalTime;
    }

    public String formatTime(double seconds) {
        long sec = (long) seconds;
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

        return String.format("%d лет %d месяцев %d дней %d часов %d минут %d секунд",
                years, months, days, hours, minutes, sec);
    }
}
