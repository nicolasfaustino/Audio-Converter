package com.audio_corverter.demo.controller;

import com.audio_corverter.demo.model.Transcricao;
import com.audio_corverter.demo.model.Usuario;
import com.audio_corverter.demo.repository.TranscricaoRepository;
import com.audio_corverter.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/audio")
public class audioConverter {

    // 1. Injetar os repositórios para ter acesso ao banco de dados
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TranscricaoRepository transcricaoRepository;

    // 2. O método agora recebe também o 'username' de quem está fazendo o upload
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {

        // Busca o usuário no banco para associar a transcrição a ele
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Erro: Usuário não encontrado para salvar o histórico.");
        }
        Usuario usuario = usuarioOptional.get();

        try (Model model = new Model("src/main/resources/model/vosk-model-small-pt-0.3")) {
            // ... (seu código de conversão existente)
            // Apenas certifique-se de que o resultado final do texto esteja em uma variável `textoFinal`

            // Simulação do resultado da sua lógica de transcrição
            String textoFinal = "Este é o texto extraído do áudio."; // Substitua pela sua lógica real de extração de texto

            // 3. Salva a nova transcrição no banco de dados
            salvarTranscricao(file.getOriginalFilename(), textoFinal, usuario);

            return ResponseEntity.ok(textoFinal);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }

    // 4. Método auxiliar para criar e salvar o objeto de transcrição
    private void salvarTranscricao(String nomeArquivo, String texto, Usuario usuario) {
        Transcricao novaTranscricao = new Transcricao();
        novaTranscricao.setNomeArquivo(nomeArquivo);
        novaTranscricao.setTextoTransito(texto);
        novaTranscricao.setUsuario(usuario);

        // Define a data e hora atual no formato brasileiro
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        novaTranscricao.setDataTranscricao(dtf.format(LocalDateTime.now()));

        transcricaoRepository.save(novaTranscricao);
        System.out.println("Transcrição salva para o usuário: " + usuario.getUsername());
    }
}