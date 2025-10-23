package com.audio_corverter.demo.dto;

public record TranscricaoDTO(
        Long id,
        String nomeArquivo,
        String texto,
        String dataTranscricao
) {}