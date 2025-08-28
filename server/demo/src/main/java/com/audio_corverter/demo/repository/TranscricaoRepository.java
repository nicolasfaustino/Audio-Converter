package com.audio_corverter.demo.repository;
import com.audio_corverter.demo.model.Transcricao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscricaoRepository extends JpaRepository<Transcricao, Long> {
    // Aqui você pode adicionar métodos customizados se precisar,
    // por exemplo, para buscar transcrições de um usuário específico.
}