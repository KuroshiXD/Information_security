package com.example.lab4.Controllers;

import com.example.lab4.Models.CryptoModel;
import com.example.lab4.Services.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/crypto")
public class CryptoController {

    private final CryptoService cryptoService;

    @Autowired
    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/index")
    public String showForm(Model model) {
        if (!model.containsAttribute("cryptoModel")) {
            model.addAttribute("cryptoModel", new CryptoModel());
        }
        return "index";
    }

    @PostMapping("/process")
    public String processText(@ModelAttribute CryptoModel cryptoModel,
                              @RequestParam("text") String text,
                              Model model) {
        try {
            // Валидация входных данных
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Текст для обработки не может быть пустым");
            }

            // Обработка в зависимости от режима
            ProcessingResult result = processBasedOnMode(cryptoModel, text);

            // Частотный анализ оригинального текста
            result.letterFrequencies = cryptoService.analyzeLetterFrequency(text);
            result.topBigrams = cryptoService.getTopBigrams(text, 10);

            // Передача данных в модель
            model.addAttribute("originalText", text);
            model.addAttribute("encryptedText", result.encryptedText);
            model.addAttribute("decryptedText", result.decryptedText);
            model.addAttribute("analysisResult", result.analysisResult);
            model.addAttribute("letterFrequencies", result.letterFrequencies);
            model.addAttribute("topBigrams", result.topBigrams);

            if (result.topLetters != null) {
                model.addAttribute("topLetters", result.topLetters);
                model.addAttribute("keyExplanation",
                        "Буква '" + result.topLetters.get(0) + "' сопоставлена с эталонной 'о'");
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cryptoModel", cryptoModel);
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Произошла ошибка: " + e.getMessage());
            model.addAttribute("cryptoModel", cryptoModel);
            return "index";
        }

        return "result";
    }

    private ProcessingResult processBasedOnMode(CryptoModel cryptoModel, String text) throws IOException {
        ProcessingResult result = new ProcessingResult();

        switch (cryptoModel.getMode()) {
            case 1: // Режим Цезаря
                processCaesarMode(cryptoModel, text, result);
                break;
            case 2: // Режим Виженера
                processVigenereMode(cryptoModel, text, result);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный режим работы");
        }

        return result;
    }

    private void processCaesarMode(CryptoModel cryptoModel, String text, ProcessingResult result) throws IOException {
        if (cryptoModel.isPerformAnalysis()) {
            cryptoService.writeFile(CryptoService.DECRYPTED_FILE, text);
            result.encryptedText = cryptoService.caesarEncrypt(text, cryptoModel.getKey());
            result.foundKey = cryptoService.caesarCryptanalysis();
            result.decryptedText = cryptoService.readFile(CryptoService.DECRYPTED_FILE);
            result.analysisResult = "Найденный ключ: " + result.foundKey;

            Map<Character, Integer> freq = cryptoService.analyzeLetterFrequency(
                    cryptoService.readFile(CryptoService.ENCRYPTED_FILE)
            );
            result.topLetters = getTopLetters(freq, 3);
        } else {
            Integer key = cryptoModel.getKey();
            if (key == null) {
                throw new IllegalArgumentException("Ключ для шифра Цезаря не может быть null");
            }

            result.encryptedText = cryptoService.caesarEncrypt(text, key);
            result.decryptedText = cryptoService.caesarDecrypt(result.encryptedText, key);
        }
    }

    private void processVigenereMode(CryptoModel cryptoModel, String text, ProcessingResult result) throws IOException {
        if (cryptoModel.isPerformAnalysis()) {
            cryptoService.writeFile(CryptoService.DECRYPTED_FILE, text);
            result.encryptedText = cryptoService.vigenereEncrypt(text, cryptoModel.getVigenereKey());
            result.foundVigenereKey = cryptoService.vigenereCryptanalysis();
            result.decryptedText = cryptoService.vigenereDecrypt(result.encryptedText, result.foundVigenereKey);
            result.analysisResult = "Найденный ключ: " + result.foundVigenereKey +
                    " (длина: " + result.foundVigenereKey.length() + ")";
        } else {
            String vigenereKey = cryptoModel.getVigenereKey();
            if (vigenereKey == null || vigenereKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Ключ для шифра Виженера не может быть пустым");
            }

            result.encryptedText = cryptoService.vigenereEncrypt(text, vigenereKey);
            result.decryptedText = cryptoService.vigenereDecrypt(result.encryptedText, vigenereKey);
        }
    }

    private List<Character> getTopLetters(Map<Character, Integer> freq, int count) {
        return freq.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static class ProcessingResult {
        String encryptedText = "";
        String decryptedText = "";
        String analysisResult = "";
        int foundKey = 0;
        String foundVigenereKey = "";
        Map<Character, Integer> letterFrequencies;
        Map<String, Integer> topBigrams;
        List<Character> topLetters;
    }
}