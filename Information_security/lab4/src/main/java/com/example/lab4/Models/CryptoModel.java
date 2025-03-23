package com.example.lab4.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class CryptoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName; // Имя исходного файла
    private int mode; // Режим шифрования (1 - Цезарь, 2 - Виженер)
    private int key; // Ключ для шифра Цезаря
    private String vigenereKey; // Ключ для шифра Виженера
    private int alphabetChoice; // Выбор алфавита для Виженера

    private String originalFilePath; // Путь к исходному файлу
    private String encryptedFilePath; // Путь к зашифрованному файлу
    private String decryptedFilePath; // Путь к расшифрованному файлу

    public String getDecryptedFilePath() {
        return decryptedFilePath;
    }

    public void setDecryptedFilePath(String decryptedFilePath) {
        this.decryptedFilePath = decryptedFilePath;
    }

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    public void setEncryptedFilePath(String encryptedFilePath) {
        this.encryptedFilePath = encryptedFilePath;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public int getAlphabetChoice() {
        return alphabetChoice;
    }

    public void setAlphabetChoice(int alphabetChoice) {
        this.alphabetChoice = alphabetChoice;
    }

    public String getVigenereKey() {
        return vigenereKey;
    }

    public void setVigenereKey(String vigenereKey) {
        this.vigenereKey = vigenereKey;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}