package com.fatec.br.adocaopet.Model;

/**
 * Created by Leo on 14/04/2018.
 */

public class Pet {
    private String id_pet;
    private String nome;
    private String idade;
    private String peso;
    private String porte;
    private String raca;
    private String especie;
    private String sexo;
    private String id_usuario;

    public Pet(){};

    public Pet(String id_pet, String nome, String idade, String peso, String porte, String raca, String especie, String sexo, String id_usuario) {
        this.id_pet = id_pet;
        this.nome = nome;
        this.idade = idade;
        this.peso = peso;
        this.porte = porte;
        this.raca = raca;
        this.especie = especie;
        this.sexo = sexo;
        this.id_usuario = id_usuario;
    }

    public String getId_pet() {
        return id_pet;
    }

    public void setId_pet(String id_pet) {
        this.id_pet = id_pet;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }
}
