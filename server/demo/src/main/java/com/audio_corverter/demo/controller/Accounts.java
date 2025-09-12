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

    // 1. Injetando o repositório que acabamos de criar.
    // O Spring vai cuidar de instanciar e nos entregar este objeto.
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TranscricaoRepository transcricaoRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        // 2. Lógica de Login
        // Busca um usuário pelo nome de usuário no banco de dados.
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsername(username);

        // Verifica se o usuário foi encontrado e se a senha corresponde.
        if (usuarioOptional.isPresent() && usuarioOptional.get().getPassword().equals(password)) {
            // Em um caso real, aqui você geraria um token (JWT, por exemplo).
            return ResponseEntity.ok("Login bem-sucedido!");
        } else {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos."); // 401 Unauthorized
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastro(@RequestParam("username") String username, @RequestParam("password") String password) {
        try {
            // 3. Lógica de Cadastro
            // Primeiro, verifica se o nome de usuário já existe para evitar duplicatas.
            if (usuarioRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.status(409).body("Erro: Nome de usuário já existe."); // 409 Conflict
            }

            // Cria uma nova instância do nosso modelo de usuário.
            Usuario novoUsuario = new Usuario();
            novoUsuario.setUsername(username);

            // ATENÇÃO: Em um projeto real, NUNCA salve a senha em texto plano.
            // Você deveria usar uma biblioteca como BCrypt para criptografá-la.
            // Ex: novoUsuario.setPassword(new BCryptPasswordEncoder().encode(password));
            novoUsuario.setPassword(password);

            // Salva o novo usuário no banco de dados. O JPA faz toda a mágica do SQL.
            usuarioRepository.save(novoUsuario);

            return ResponseEntity.ok("Cadastro realizado com sucesso!");

        } catch (Exception e) {
            // Retorna uma mensagem de erro genérica caso algo dê errado.
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

            // Ordena por dataTranscricao (String no formato dd/MM/yyyy HH:mm:ss)
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