package com.fatec.br.adocaopet.Model;


/**
 * Created by Leo on 18/03/2018.
 */

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String endereco;
    private String cpf;
    private String rg;
    private String data_nasc;

    public Usuario() {
    }

    public Usuario(String id, String nome, String email, String senha, String telefone, String endereco, String cpf, String rg, String data_nasc) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.endereco = endereco;
        this.cpf = cpf;
        this.rg = rg;
        this.data_nasc = data_nasc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public void setDataNasc(String data_nasc) {
        this.data_nasc = data_nasc;
    }

    public String getDataNasc() {
        return data_nasc;
    }



    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", telefone='" + telefone + '\'' +
                ", endereco='" + endereco + '\'' +
                ", cpf='" + cpf + '\'' +
                ", data_nasc='" + data_nasc + '\'' +
                ", rg='" + rg + '\'' +
                '}';
    }
}
