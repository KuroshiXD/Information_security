package com.example.lab4.Services;

import com.example.lab4.Models.CryptoModel;
import com.example.lab4.Repositories.CryptoFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class CryptoService {

    @Autowired
    private CryptoFormRepository cryptoFormRepository;

    public void saveCryptoForm(CryptoModel cryptoModel) {
        cryptoFormRepository.save(cryptoModel);
    }

    public void writeFile(String fileName, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        }
    }

    public String caesarEncrypt(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                character = (char) ((character - base + key) % 26 + base);
            }
            result.append(character);
        }
        return result.toString();
    }

    public String caesarDecrypt(String text, int key) {
        return caesarEncrypt(text, 26 - key);
    }

    public String vigenereEncrypt(String text, String key, int alphabetChoice) {
        StringBuilder result = new StringBuilder();
        key = key.toUpperCase();
        int keyIndex = 0;

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                int shift = key.charAt(keyIndex % key.length()) - 'A';
                character = (char) ((character - base + shift) % 26 + base);
                keyIndex++;
            }
            result.append(character);
        }
        return result.toString();
    }

    public String vigenereDecrypt(String text, String key, int alphabetChoice) {
        StringBuilder result = new StringBuilder();
        key = key.toUpperCase();
        int keyIndex = 0;

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                int shift = key.charAt(keyIndex % key.length()) - 'A';
                character = (char) ((character - base - shift + 26) % 26 + base);
                keyIndex++;
            }
            result.append(character);
        }
        return result.toString();
    }
}