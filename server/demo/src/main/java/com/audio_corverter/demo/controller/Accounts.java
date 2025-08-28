package com.audio_corverter.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/accounts")
public class Accounts {
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            String result = "Aprovado";
            // TODO: LOGICA DE LOGIN COM A DB

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastro(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            String result = "Aprovado";
            // TODO: LOGICA DE CADASTRO COM A DB

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }

    @PostMapping("/historico")
    public ResponseEntity<String> cadastro(@RequestParam("username") String username) {
        try {
            String result = "Aprovado";
            // TODO: LOGICA DE HISTORICO COM A DB

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }
}
