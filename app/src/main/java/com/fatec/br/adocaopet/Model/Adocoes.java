package com.fatec.br.adocaopet.Model;

public class Adocoes {

    private String id_adocao;
    private String id_pet;
    private String id_dono;
    private String id_adotante;
    private String data_adocao;
    private String status;


    public Adocoes() {
    }

    public Adocoes(String id_adocao, String id_pet, String id_dono, String id_adotante, String data_adocao, String status) {
        this.id_adocao = id_adocao;
        this.id_pet = id_pet;
        this.id_dono = id_dono;
        this.id_adotante = id_adotante;
        this.data_adocao = data_adocao;
        this.status = status;
    }

    public String getId_adocao() {
        return id_adocao;
    }

    public void setId_adocao(String id_adocao) {
        this.id_adocao = id_adocao;
    }

    public String getId_pet() {
        return id_pet;
    }

    public void setId_pet(String id_pet) {
        this.id_pet = id_pet;
    }

    public String getId_dono() {
        return id_dono;
    }

    public void setId_dono(String id_dono) {
        this.id_dono = id_dono;
    }

    public String getId_adotante() {
        return id_adotante;
    }

    public void setId_adotante(String id_adotante) {
        this.id_adotante = id_adotante;
    }

    public String getData_adocao() {
        return data_adocao;
    }

    public void setData_adocao(String data_adocao) {
        this.data_adocao = data_adocao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
