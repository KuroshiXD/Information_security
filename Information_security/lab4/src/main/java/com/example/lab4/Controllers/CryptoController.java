package com.example.lab4.Controllers;

import com.example.lab4.Models.CryptoModel;
import com.example.lab4.Services.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("cryptoModel", new CryptoModel());
        return "index";
    }

    @PostMapping("/process")
    public String process(@ModelAttribute CryptoModel cryptoModel, @RequestParam("text") String text, Model model) {
        try {
            String fileName = "input_text.txt";
            cryptoService.writeFile(fileName, text);

            String encryptedText = "";
            String decryptedText = "";

            if (cryptoModel.getMode() == 1) {
                encryptedText = cryptoService.caesarEncrypt(text, cryptoModel.getKey());
                decryptedText = cryptoService.caesarDecrypt(encryptedText, cryptoModel.getKey());
            } else if (cryptoModel.getMode() == 2) {
                encryptedText = cryptoService.vigenereEncrypt(text, cryptoModel.getVigenereKey(), cryptoModel.getAlphabetChoice());
                decryptedText = cryptoService.vigenereDecrypt(encryptedText, cryptoModel.getVigenereKey(), cryptoModel.getAlphabetChoice());
            }

            cryptoService.writeFile("encrypted_text.txt", encryptedText);
            cryptoService.writeFile("decrypted_text.txt", decryptedText);

            model.addAttribute("originalText", text);
            model.addAttribute("encryptedText", encryptedText);
            model.addAttribute("decryptedText", decryptedText);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обработке текста");
        }

        return "result";
    }
}