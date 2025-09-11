package com.audio_corverter.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.*;

import static com.audio_corverter.demo.controller.to16kMonoPcm.ConvertFiles;

@RestController
@RequestMapping("/api/audio")
public class audioConverter {

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println(file);
            // 1) Salva upload em arquivo temporário
            String ext = getExt(file.getOriginalFilename()); // ".mp3", ".wav", etc.
            System.out.println(ext);
            File uploaded = File.createTempFile("upload_", ext);
            file.transferTo(uploaded);

            // 2) Converte para WAV PCM 16k mono
            File wav16k = ConvertFiles(uploaded);

            // 3) Obtém diretório do modelo do classpath (funciona no IDE; em JAR prefira path externo)
            File modelDir = ResourceUtils.getFile("classpath:model/vosk-model-small-pt-0.3");

            // 4) Reconhecimento
            try (InputStream in = new FileInputStream(wav16k);
                 Model model = new Model(modelDir.getAbsolutePath());
                 Recognizer recognizer = new Recognizer(model, 16000)) {

                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) >= 0) {
                    recognizer.acceptWaveForm(buffer, n);
                }
                return ResponseEntity.ok(recognizer.getFinalResult());
            }

        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body("Formato de áudio não suportado: " + iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }

    private static String getExt(String name) {
        if (name == null) return ".dat";
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i) : ".dat";
    }
}