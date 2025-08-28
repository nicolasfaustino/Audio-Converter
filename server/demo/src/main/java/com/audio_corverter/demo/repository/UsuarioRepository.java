package com.audio_corverter.demo.repository;
import com.audio_corverter.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método para buscar um usuário pelo nome de usuário
    Optional<Usuario> findByUsername(String username);
}
