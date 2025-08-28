package com.audio_corverter.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Transicoes") // O nome da tabela deve ser "Transicoes"
public class Transcricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;
    private String dataTranscricao;
    private String textoTransito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getDataTranscricao() {
        return dataTranscricao;
    }

    public String getTextoTransito() {
        return textoTransito;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void setDataTranscricao(String dataTranscricao) {
        this.dataTranscricao = dataTranscricao;
    }

    public void setTextoTransito(String textoTransito) {
        this.textoTransito = textoTransito;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
