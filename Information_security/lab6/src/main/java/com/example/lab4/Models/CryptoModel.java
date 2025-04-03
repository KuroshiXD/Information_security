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

    private int mode;
    private int key;
    private String vigenereKey;
    private boolean performAnalysis;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getVigenereKey() {
        return vigenereKey;
    }

    public void setVigenereKey(String vigenereKey) {
        this.vigenereKey = vigenereKey;
    }

    public boolean isPerformAnalysis() {
        return performAnalysis;
    }

    public void setPerformAnalysis(boolean performAnalysis) {
        this.performAnalysis = performAnalysis;
    }
}