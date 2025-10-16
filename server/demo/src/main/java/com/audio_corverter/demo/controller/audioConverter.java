package com.audio_corverter.demo.controller;

import com.audio_corverter.demo.model.Transcricao;
import com.audio_corverter.demo.model.Usuario;
import com.audio_corverter.demo.repository.TranscricaoRepository;
import com.audio_corverter.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.audio_corverter.demo.controller.to16kMonoPcm.ConvertFiles;

@RestController
@RequestMapping("/api/audio")
public class audioConverter {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TranscricaoRepository transcricaoRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Erro: Usuário não encontrado para salvar o histórico.");
        }

        Usuario usuario = usuarioOptional.get();

        try {
            System.out.println(file);
            String ext = getExt(file.getOriginalFilename()); // ".mp3", ".wav", etc.
            System.out.println(ext);
            File uploaded = File.createTempFile("upload_", ext);
            file.transferTo(uploaded);

            File wav16k = ConvertFiles(uploaded);

            File modelDir = ResourceUtils.getFile("classpath:model/vosk-model-small-pt-0.3");

            try (InputStream in = new FileInputStream(wav16k);
                 Model model = new Model(modelDir.getAbsolutePath());
                 Recognizer recognizer = new Recognizer(model, 16000)) {

                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) >= 0) {
                    recognizer.acceptWaveForm(buffer, n);
                }

                salvarTranscricao(file.getOriginalFilename(), recognizer.getFinalResult(), usuario);

                List<Transcricao> lista = transcricaoRepository.findByUsuario(usuario);

                DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                Comparator<Transcricao> byDateDesc = Comparator.comparing(
                        (Transcricao t) -> LocalDateTime.parse(t.getDataTranscricao(), BR)
                ).reversed();

                lista.sort(byDateDesc);

                System.out.println(lista.getFirst().getTextoTransito());
                System.out.println(lista.getLast().getId());

                return ResponseEntity.ok(lista.getFirst().getTextoTransito());
            }

        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body("Formato de áudio não suportado: " + iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }

    private void salvarTranscricao(String nomeArquivo, String texto, Usuario usuario) {
        Transcricao novaTranscricao = new Transcricao();
        novaTranscricao.setNomeArquivo(nomeArquivo);
        novaTranscricao.setTextoTransito(texto);
        novaTranscricao.setUsuario(usuario);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        novaTranscricao.setDataTranscricao(dtf.format(LocalDateTime.now()));

        transcricaoRepository.save(novaTranscricao);
        System.out.println("Transcrição salva para o usuário: " + usuario.getUsername());
    }

    private static String getExt(String name) {
        if (name == null) return ".dat";
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i) : ".dat";
    }
}