package com.example.apf.model;

public class Refeicao {

    int idrefeicao;
    String nome;
    String horario;
    String quantidadegramas;
    String iddieta;

    public Refeicao() {
    }

    public Refeicao(int idrefeicao, String nome, String horario, String quantidadegramas, String iddieta) {
        this.idrefeicao = idrefeicao;
        this.nome = nome;
        this.horario = horario;
        this.quantidadegramas = quantidadegramas;
        this.iddieta = iddieta;
    }

    public int getIdrefeicao() {
        return idrefeicao;
    }

    public void setIdrefeicao(int idrefeicao) {
        this.idrefeicao = idrefeicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getQuantidadegramas() {
        return quantidadegramas;
    }

    public void setQuantidadegramas(String quantidadegramas) {
        this.quantidadegramas = quantidadegramas;
    }

    public String getIddieta() {
        return iddieta;
    }

    public void setIddieta(String iddieta) {
        this.iddieta = iddieta;
    }
}
