package com.example.apf.model;

public class Usuario {

    int idUsuario;
    String nome;
    String dtnascimento;
    String email;
    String senha;
    String celular;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nome, String dtnascimento, String email, String senha, String celular) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.dtnascimento = dtnascimento;
        this.email = email;
        this.senha = senha;
        this.celular = celular;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDtnascimento() {
        return dtnascimento;
    }

    public void setDtnascimento(String dtnascimento) {
        this.dtnascimento = dtnascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }
}
