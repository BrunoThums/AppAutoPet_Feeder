package com.example.apf.model;

public class Alimentador {

    int idalimentador;
    String serial;
    int idusuario;
    int idpet;
    int iddieta;

    public Alimentador() {
    }

    public Alimentador(int idalimentador, String serial, int idusuario, int idpet, int iddieta) {
        this.idalimentador = idalimentador;
        this.serial = serial;
        this.idusuario = idusuario;
        this.idpet = idpet;
        this.iddieta = iddieta;
    }

    public int getIdalimentador() {
        return idalimentador;
    }

    public void setIdalimentador(int idalimentador) {
        this.idalimentador = idalimentador;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public int getIdpet() {
        return idpet;
    }

    public void setIdpet(int idpet) {
        this.idpet = idpet;
    }

    public int getIddieta() {
        return iddieta;
    }

    public void setIddieta(int iddieta) {
        this.iddieta = iddieta;
    }
}
