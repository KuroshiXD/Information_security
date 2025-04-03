package com.example.lab4.Services;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CryptoService {
    public static final String ALPHABET = "абвгдежзийклмнопрстуфхцчшщъыьэюяё";
    public static final String ENCRYPTED_FILE = "encrypted_text.txt";
    public static final String DECRYPTED_FILE = "decrypted_text.txt";

    // Частоты букв русского языка
    private static final Map<Character, Integer> REF_LETTER_FREQ = Map.ofEntries(
            Map.entry('о', 11), Map.entry('е', 9), Map.entry('а', 8),
            Map.entry('и', 7), Map.entry('н', 6), Map.entry('т', 6),
            Map.entry('с', 5), Map.entry('р', 5), Map.entry('в', 4),
            Map.entry('л', 4), Map.entry('к', 3), Map.entry('м', 3),
            Map.entry('д', 3), Map.entry('п', 2), Map.entry('у', 2),
            Map.entry('я', 2), Map.entry('ы', 2), Map.entry('ь', 2),
            Map.entry('г', 2), Map.entry('з', 1), Map.entry('б', 1),
            Map.entry('ч', 1), Map.entry('й', 1), Map.entry('х', 1),
            Map.entry('ж', 1), Map.entry('ш', 1), Map.entry('ю', 1),
            Map.entry('ц', 1), Map.entry('щ', 1), Map.entry('э', 1),
            Map.entry('ф', 1), Map.entry('ъ', 1), Map.entry('ё', 1)
    );

    // Частоты биграмм русского языка
    private static final Map<String, Integer> REF_BIGRAM_FREQ = Map.of(
            "ст", 30, "но", 25, "то", 25, "на", 25, "ен", 20,
            "ов", 20, "ни", 20, "ра", 20, "во", 18, "ко", 18
    );

    // ========== Файловые операции ==========
    public void writeFile(String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }

    public String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    // ========== Шифр Цезаря ==========
    public String caesarEncrypt(String text, int key) throws IOException {
        String encrypted = caesarCipher(text, key);
        writeFile(ENCRYPTED_FILE, encrypted);
        return encrypted;
    }

    public String caesarDecrypt(String text, int key) throws IOException {
        String decrypted = caesarCipher(text, -key);
        writeFile(DECRYPTED_FILE, decrypted);
        return decrypted;
    }

    public int caesarCryptanalysis() throws IOException {
        String cipherText = readFile(ENCRYPTED_FILE);
        String filtered = filterText(cipherText);

        if (filtered.isEmpty()) {
            throw new IllegalArgumentException("Текст не содержит букв из алфавита");
        }

        Map<Character, Integer> freq = analyzeLetterFrequency(filtered);
        if (freq.isEmpty()) {
            return 0;
        }

        List<Character> topLetters = freq.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        char[] refLetters = {'о', 'е', 'а', 'и', 'н', 'т', 'с', 'р', 'в', 'л'};

        for (char cipherChar : topLetters) {
            for (char refChar : refLetters) {
                int idxCipher = ALPHABET.indexOf(cipherChar);
                int idxRef = ALPHABET.indexOf(refChar);
                int possibleKey = (idxCipher - idxRef + ALPHABET.length()) % ALPHABET.length();

                String decrypted = caesarCipher(cipherText, -possibleKey);
                if (isMeaningfulText(decrypted)) {
                    writeFile(DECRYPTED_FILE, decrypted);
                    return possibleKey;
                }
            }
        }

        return 0;
    }

    private String caesarCipher(String text, int shift) {
        StringBuilder result = new StringBuilder();
        int alphLen = ALPHABET.length();

        for (char ch : text.toCharArray()) {
            char lowerCh = Character.toLowerCase(ch);
            int idx = ALPHABET.indexOf(lowerCh);

            if (idx == -1) {
                result.append(ch);
                continue;
            }

            int newIndex = (idx + shift) % alphLen;
            if (newIndex < 0) {
                newIndex += alphLen;
            }

            char newChar = ALPHABET.charAt(newIndex);
            result.append(Character.isUpperCase(ch) ? Character.toUpperCase(newChar) : newChar);
        }

        return result.toString();
    }

    // ========== Шифр Виженера ==========
    public String vigenereEncrypt(String text, String key) throws IOException {
        String encrypted = vigenereCipher(text, key, false);
        writeFile(ENCRYPTED_FILE, encrypted);
        return encrypted;
    }

    public String vigenereDecrypt(String text, String key) throws IOException {
        String decrypted = vigenereCipher(text, key, true);
        writeFile(DECRYPTED_FILE, decrypted);
        return decrypted;
    }

    public String vigenereCryptanalysis() throws IOException {
        String cipherText = readFile(ENCRYPTED_FILE);
        String filtered = filterText(cipherText);

        int keyLength = determineKeyLength(filtered);
        String foundKey = determineKey(filtered, keyLength);

        String decrypted = vigenereDecrypt(cipherText, foundKey);
        writeFile(DECRYPTED_FILE, decrypted);

        return foundKey;
    }

    private String vigenereCipher(String text, String key, boolean decrypt) {
        StringBuilder result = new StringBuilder();
        int alphLen = ALPHABET.length();
        int keyIndex = 0;

        for (char ch : text.toCharArray()) {
            char lowerCh = Character.toLowerCase(ch);
            int textIndex = ALPHABET.indexOf(lowerCh);

            if (textIndex == -1) {
                result.append(ch);
                continue;
            }

            char lowerKeyChar = Character.toLowerCase(key.charAt(keyIndex % key.length()));
            int keyShift = ALPHABET.indexOf(lowerKeyChar);

            if (decrypt) {
                keyShift = -keyShift;
            }

            int newIndex = (textIndex + keyShift) % alphLen;
            if (newIndex < 0) {
                newIndex += alphLen;
            }

            char newChar = ALPHABET.charAt(newIndex);
            result.append(Character.isUpperCase(ch) ? Character.toUpperCase(newChar) : newChar);
            keyIndex++;
        }

        return result.toString();
    }

    private int determineKeyLength(String cipherText) {
        int maxKeyLength = 33;
        double bestIc = 0;
        int bestKeyLength = 1;

        for (int keyLen = 1; keyLen <= maxKeyLength; keyLen++) {
            double icSum = 0;
            int validColumns = 0;

            for (int i = 0; i < keyLen; i++) {
                StringBuilder subtext = new StringBuilder();
                for (int j = i; j < cipherText.length(); j += keyLen) {
                    subtext.append(cipherText.charAt(j));
                }

                if (subtext.length() > 1) {
                    icSum += calculateIc(subtext.toString());
                    validColumns++;
                }
            }

            if (validColumns > 0) {
                double avgIc = icSum / validColumns;
                if (avgIc > bestIc) {
                    bestIc = avgIc;
                    bestKeyLength = keyLen;
                }
            }
        }

        return bestKeyLength;
    }

    private String determineKey(String cipherText, int keyLength) {
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < keyLength; i++) {
            StringBuilder subtext = new StringBuilder();
            for (int j = i; j < cipherText.length(); j += keyLength) {
                subtext.append(cipherText.charAt(j));
            }

            int shift = determineShiftForSubtext(subtext.toString());
            key.append(ALPHABET.charAt(shift));
        }

        return key.toString();
    }

    private int determineShiftForSubtext(String subtext) {
        if (subtext.isEmpty()) {
            return 0;
        }

        int alphLength = ALPHABET.length();
        double bestScore = Double.POSITIVE_INFINITY;
        int bestShift = 0;

        for (int shift = 0; shift < alphLength; shift++) {
            String shifted = applyShift(subtext, shift);
            Map<Character, Integer> subLetterFreq = analyzeLetterFrequency(shifted);
            Map<String, Integer> subBigramFreq = analyzeBigramFrequency(shifted);

            double letterError = 0;
            for (Map.Entry<Character, Integer> entry : REF_LETTER_FREQ.entrySet()) {
                int subCount = subLetterFreq.getOrDefault(entry.getKey(), 0);
                letterError += Math.abs(entry.getValue() - subCount);
            }

            double bigramError = 0;
            for (Map.Entry<String, Integer> entry : REF_BIGRAM_FREQ.entrySet()) {
                int subCount = subBigramFreq.getOrDefault(entry.getKey(), 0);
                bigramError += Math.abs(entry.getValue() - subCount);
            }

            double totalError = letterError + bigramError;
            if (totalError < bestScore) {
                bestScore = totalError;
                bestShift = shift;
            }
        }

        return bestShift;
    }

    // ========== Вспомогательные методы ==========
    private String applyShift(String text, int shift) {
        StringBuilder result = new StringBuilder();
        int alphLen = ALPHABET.length();

        for (char ch : text.toCharArray()) {
            int idx = ALPHABET.indexOf(ch);
            int newIndex = (idx - shift + alphLen) % alphLen;
            result.append(ALPHABET.charAt(newIndex));
        }

        return result.toString();
    }

    public Map<Character, Integer> analyzeLetterFrequency(String text) {
        Map<Character, Integer> freq = new HashMap<>();

        for (char ch : text.toLowerCase().toCharArray()) {
            if (ALPHABET.indexOf(ch) != -1) {
                freq.put(ch, freq.getOrDefault(ch, 0) + 1);
            }
        }

        return freq;
    }

    public Map<String, Integer> analyzeBigramFrequency(String text) {
        Map<String, Integer> freq = new HashMap<>();
        String filtered = filterText(text);

        for (int i = 0; i < filtered.length() - 1; i++) {
            String bigram = filtered.substring(i, i + 2);
            freq.put(bigram, freq.getOrDefault(bigram, 0) + 1);
        }

        return freq;
    }

    public Map<String, Integer> getTopBigrams(String text, int topN) {
        Map<String, Integer> bigrams = analyzeBigramFrequency(text);
        return bigrams.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private String filterText(String text) {
        return text.toLowerCase().chars()
                .mapToObj(c -> (char) c)
                .filter(c -> ALPHABET.indexOf(c) != -1)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private boolean isMeaningfulText(String text) {
        String[] commonWords = {"и", "в", "не", "на", "я", "что", "он", "это"};
        String lowerText = text.toLowerCase();
        return Arrays.stream(commonWords).anyMatch(lowerText::contains);
    }

    private double calculateIc(String text) {
        Map<Character, Integer> freq = analyzeLetterFrequency(text);
        int n = text.length();
        if (n <= 1) {
            return 0;
        }

        double ic = freq.values().stream()
                .mapToDouble(count -> count * (count - 1))
                .sum();

        return ic / (n * (n - 1));
    }
}