package com.audio_corverter.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/api/audio")
public class AudioConverter {
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("audio", ".wav");
            file.transferTo(tempFile);

            try (Model model = new Model("model")) {
                try (Recognizer recognizer = new Recognizer(model, 16000)) {
                    InputStream ais = new FileInputStream(tempFile);
                    byte[] buffer = new byte[4096];
                    int nbytes;

                    while ((nbytes = ais.read(buffer)) >= 0) {
                        if (recognizer.acceptWaveForm(buffer, nbytes)) {

                        }
                    }
                    String result = recognizer.getFinalResult();
                    return ResponseEntity.ok(result);
                }
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }
}
