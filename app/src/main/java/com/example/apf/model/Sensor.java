package com.example.apf.model;

public class Sensor {
    int idsensor;
    int idalimentador;
    String data;
    String sensores;

    public Sensor() {
    }

    public Sensor(int idsensor, int idalimentador, String data, String sensores) {
        this.idsensor = idsensor;
        this.idalimentador = idalimentador;
        this.data = data;
        this.sensores = sensores;
    }

    public int getIdsensor() {
        return idsensor;
    }

    public void setIdsensor(int idsensor) {
        this.idsensor = idsensor;
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

    public String getSensores() {
        return sensores;
    }

    public void setSensores(String sensores) {
        this.sensores = sensores;
    }
}
