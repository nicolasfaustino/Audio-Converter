package com.audio_corverter.demo.controller;

import com.audio_corverter.demo.dto.TranscricaoDTO;
import com.audio_corverter.demo.model.Transcricao;
import com.audio_corverter.demo.model.Usuario;
import com.audio_corverter.demo.repository.TranscricaoRepository;
import com.audio_corverter.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/accounts")
public class Accounts {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TranscricaoRepository transcricaoRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);

        if (usuarioOptional.isPresent() && usuarioOptional.get().getPassword().equals(password)) {
            return ResponseEntity.ok("Login bem-sucedido!");
        } else {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos."); // 401 Unauthorized
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastro(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            if (usuarioRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.status(409).body("Erro: Nome de usuário já existe."); // 409 Conflict
            }

            Usuario novoUsuario = new Usuario();
            novoUsuario.setUsername(username);

            novoUsuario.setPassword(password);

            usuarioRepository.save(novoUsuario);

            return ResponseEntity.ok("Cadastro realizado com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno ao processar o cadastro: " + e.getMessage());
        }
    }

    @GetMapping("/historico/{username}")
    public ResponseEntity<?> historico(@RequestParam("username") String username) {
        try {
            var usuarioOpt = usuarioRepository.findByUsername(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Usuário não encontrado");
            }
            Usuario usuario = usuarioOpt.get();

            List<Transcricao> lista = transcricaoRepository.findByUsuario(usuario);

            DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            Comparator<Transcricao> byDateDesc = Comparator.comparing(
                    (Transcricao t) -> LocalDateTime.parse(t.getDataTranscricao(), BR)
            ).reversed();

            lista.sort(byDateDesc);

            lista.sort(byDateDesc);
            List<TranscricaoDTO> dtos = lista.stream()
                    .map((Transcricao t) -> new TranscricaoDTO(
                            t.getId(),
                            t.getNomeArquivo(),
                            t.getTextoTransito(),
                            t.getDataTranscricao()
                    ))
                    .toList();

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar histórico: " + e.getMessage());
        }
    }
}