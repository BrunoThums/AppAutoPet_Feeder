package com.example.apf.model;

public class Dieta {

    int iddieta;
    String nome;
    int idpet;

    public Dieta() {
    }

    public Dieta(int iddieta, String nome, int idpet) {
        this.iddieta = iddieta;
        this.nome = nome;
        this.idpet = idpet;
    }

    public int getIddieta() {
        return iddieta;
    }

    public void setIddieta(int iddieta) {
        this.iddieta = iddieta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdpet() {
        return idpet;
    }

    public void setIdpet(int idpet) {
        this.idpet = idpet;
    }

    @Override
    public String toString() {
        return nome;
    }
}
