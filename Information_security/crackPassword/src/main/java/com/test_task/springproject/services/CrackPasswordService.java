package com.test_task.springproject.services;

import com.test_task.springproject.models.User;
import com.test_task.springproject.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CrackPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private UserService userService;

    private static final Map<Character, Character> RUS_TO_LAT;

    static {
        Map<Character, Character> map = new HashMap<>();
        map.put('а', 'f');
        map.put('б', ',');
        map.put('в', 'd');
        map.put('г', 'u');
        map.put('д', 'l');
        map.put('е', 't');
        map.put('ё', '`');
        map.put('ж', ';');
        map.put('з', 'p');
        map.put('и', 'b');
        map.put('й', 'q');
        map.put('к', 'r');
        map.put('л', 'k');
        map.put('м', 'v');
        map.put('н', 'y');
        map.put('о', 'j');
        map.put('п', 'g');
        map.put('р', 'h');
        map.put('с', 'c');
        map.put('т', 'n');
        map.put('у', 'e');
        map.put('ф', 'a');
        map.put('х', '[');
        map.put('ц', 'w');
        map.put('ч', 'x');
        map.put('ш', 'i');
        map.put('щ', 'o');
        map.put('ъ', ']');
        map.put('ы', 's');
        map.put('ь', 'm');
        map.put('э', '\'');
        map.put('ю', '.');
        map.put('я', 'z');
        RUS_TO_LAT = Collections.unmodifiableMap(map);
    }

    @PostConstruct
    public void init() {
        DICTIONARY = loadFromFile();
        DICTIONARY_RUS = loadFromFileRus();
    }

    private static List<String> DICTIONARY = new ArrayList<>();
    private static List<String> DICTIONARY_RUS = new ArrayList<>();

    private List<String> loadFromFile() {
        List<String> words = new ArrayList<>();
        try {
            Resource resource = resourceLoader.getResource("classpath:dictionary_for_brut.txt");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return words;
    }

    private List<String> loadFromFileRus() {
        List<String> wordsRus = new ArrayList<>();
        try {
            Resource resourceRus = resourceLoader.getResource("classpath:dictionary_rus.txt");
            InputStream inputStreamRus = resourceRus.getInputStream();
            BufferedReader readerRus = new BufferedReader(new InputStreamReader(inputStreamRus));

            String lineRus;
            while ((lineRus = readerRus.readLine()) != null) {
                if (!lineRus.trim().isEmpty()) {
                    wordsRus.add(lineRus.trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return wordsRus;
    }

    public Map<String, Object> analyzePassword(String userName, String method, int maxLength) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.nanoTime();
        long attempts = 0;
        boolean found = false;
        String foundPassword = null;
        double speed = 0;

        switch (method) {
            case "dictionary":
                attempts = 0;
                for (String word : DICTIONARY_RUS) {
                    attempts++;
                    if (userService.loginUser(userName, word).isPresent()) {
                        found = true;
                        foundPassword = word;
                        break;
                    }

                    String engLayout = convertToEnglishLayout(word);
                    attempts++;
                    if (userService.loginUser(userName, engLayout).isPresent()) {
                        found = true;
                        foundPassword = engLayout;
                        break;
                    }

                    for (int i = 0; i < 10; i++) {
                        String withNumber = word + i;
                        attempts++;
                        if (userService.loginUser(userName, word).isPresent()) {
                            found = true;
                            foundPassword = withNumber;
                            break;
                        }

                        String engWithNumber = engLayout + i;
                        attempts++;
                        if (userService.loginUser(userName, engWithNumber).isPresent()) {
                            found = true;
                            foundPassword = engWithNumber;
                            break;
                        }
                    }
                    if (found) break;
                }
                break;

            case "bruteforce":
                String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
                found = bruteForce(userName, "", chars, maxLength, result);
                attempts = (long) result.get("attempts");
                foundPassword = found ? (String) result.get("password") : null;
                break;
        }

        long duration = System.nanoTime() - startTime;
        speed = attempts / (duration / 1_000_000_000.0);

        result.put("status", found ? "Успех" : "Неудача");
        result.put("method", method);
        result.put("attempts", attempts);
        result.put("speed", String.format("%.2f", speed));
        result.put("time_ns", duration);
        if (found) {
            result.put("password", foundPassword);
        }
        return result;
    }

    private boolean bruteForce(String userName, String current,
                               String chars, int maxLength,
                               Map<String, Object> result) {
        if (!current.isEmpty()) {
            long attempts = (long) result.getOrDefault("attempts", 0L);
            result.put("attempts", attempts + 1);

            if (userService.loginUser(userName, current).isPresent()) {
                result.put("password", current);
                return true;
            }
        }

        if (current.length() >= maxLength) {
            return false;
        }

        for (int i = 0; i < chars.length(); i++) {
            if (bruteForce(userName, current + chars.charAt(i), chars, maxLength, result)) {
                return true;
            }
        }
        return false;
    }

    private String convertToEnglishLayout(String russianWord) {
        StringBuilder result = new StringBuilder();
        for (char c : russianWord.toLowerCase().toCharArray()) {
            result.append(RUS_TO_LAT.getOrDefault(c, c));
        }
        return result.toString();
    }

    public Map<String, Object> crackUserPassword(String username, String method, int maxLength) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return Map.of("error", "Пользователь не найден");
        }

        return analyzePassword(username, method, maxLength);
    }
}