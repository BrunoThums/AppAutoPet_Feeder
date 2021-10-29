package com.example.apf.model;

public class Pet {

    int idpet;
    String nome;
    String animal;
    String dtnascimento;
    String raca;
    String porte;
    int idusuario;

    public Pet() {
    }

    public Pet(int idpet, String nome, String animal, String dtnascimento, String raca, String porte, int idusuario) {
        this.idpet = idpet;
        this.nome = nome;
        this.animal = animal;
        this.dtnascimento = dtnascimento;
        this.raca = raca;
        this.porte = porte;
        this.idusuario = idusuario;
    }

    public int getIdpet() {
        return idpet;
    }

    public void setIdpet(int idpet) {
        this.idpet = idpet;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getDtnascimento() {
        return dtnascimento;
    }

    public void setDtnascimento(String dtnascimento) {
        this.dtnascimento = dtnascimento;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    @Override
    public String toString() {
        return nome;
    }
}
