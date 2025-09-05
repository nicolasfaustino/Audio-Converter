package com.audio_corverter.demo.controller;

import com.audio_corverter.demo.model.Usuario;
import com.audio_corverter.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/accounts")
public class Accounts {

    // 1. Injetando o repositório que acabamos de criar.
    // O Spring vai cuidar de instanciar e nos entregar este objeto.
    @Autowired
    private UsuarioRepository usuarioRepository;

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

    // TODO: A lógica do histórico virá aqui depois
    @PostMapping("/historico")
    public ResponseEntity<String> historico(@RequestParam("username") String username) {
        try {
            String result = "Aprovado";
            // TODO: LOGICA DE HISTORICO COM A DB
            // 1. Buscar o usuário pelo 'username'
            // 2. Usar o ID do usuário para buscar todas as suas transcrições no TranscricaoRepository
            // 3. Retornar a lista de transcrições como JSON

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar áudio: " + e.getMessage());
        }
    }
}