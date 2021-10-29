package com.example.apf.model;

public class Comando {
    int idcomando;
    int idalimentador;
    String data;
    String comando;
    boolean comandoExecutado;

    public Comando() {
    }

    public Comando(int idcomando, int idalimentador, String data, String comando, boolean comandoExecutado) {
        this.idcomando = idcomando;
        this.idalimentador = idalimentador;
        this.data = data;
        this.comando = comando;
        this.comandoExecutado = comandoExecutado;
    }

    public int getIdcomando() {
        return idcomando;
    }

    public void setIdcomando(int idcomando) {
        this.idcomando = idcomando;
    }

    public int getIdalimentador() {
        return idalimentador;
    }

    public void setIdalimentador(int idalimentador) {
        this.idalimentador = idalimentador;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public boolean isComandoExecutado() {
        return comandoExecutado;
    }

    public void setComandoExecutado(boolean comandoExecutado) {
        this.comandoExecutado = comandoExecutado;
    }
}
