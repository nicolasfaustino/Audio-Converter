package com.audio_corverter.demo.repository;

import com.audio_corverter.demo.model.Transcricao;
import com.audio_corverter.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscricaoRepository extends JpaRepository<Transcricao, Long> {

    List<Transcricao> findByUsuario(Usuario usuario);
}