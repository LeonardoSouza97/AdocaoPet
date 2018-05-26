package com.fatec.br.adocaopet.Model;

public class Adocoes {

    private String id_adocao;
    private String id_pet;
    private String id_dono;
    private String id_adotante;
    private String data_adocao;
    private String status;
    private Usuario solicitante;
    private Pet pet;

    public Adocoes(String id_adocao, String id_pet, String id_dono, String id_adotante, String data_adocao, String status, Usuario solicitante, Pet pet) {
        this.id_adocao = id_adocao;
        this.id_pet = id_pet;
        this.id_dono = id_dono;
        this.id_adotante = id_adotante;
        this.data_adocao = data_adocao;
        this.status = status;
        this.solicitante = solicitante;
        this.pet = pet;
    }

    public Adocoes() {
    }


    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public String getIdAdocao() {
        return id_adocao;
    }

    public void setIdAdocao(String id_adocao) {
        this.id_adocao = id_adocao;
    }

    public String getIdPet() {
        return id_pet;
    }

    public void setIdPet(String id_pet) {
        this.id_pet = id_pet;
    }

    public String getIdDono() {
        return id_dono;
    }

    public void setIdDono(String id_dono) {
        this.id_dono = id_dono;
    }

    public String getId_adotante() {
        return id_adotante;
    }

    public void setId_adotante(String id_adotante) {
        this.id_adotante = id_adotante;
    }

    public String getDataAdocao() {
        return data_adocao;
    }

    public void setDataAdocao(String data_adocao) {
        this.data_adocao = data_adocao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
