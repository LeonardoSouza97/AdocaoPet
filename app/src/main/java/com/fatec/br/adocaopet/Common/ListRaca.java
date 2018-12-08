package com.fatec.br.adocaopet.Common;

public class ListRaca {

    public String[] ListaCachorro() {

        String[] caes = {"Beagle", "Boxer", "Buldogue", "Cocker", "Golden","Husky Siberiano", "Pinscher", "Pug","Rottweiler", "Vira-Lata", "Yorkshire"};

        return caes;
    }

    public String[] ListaCachorroFiltro() {

        String[] caes = {"", "Beagle", "Boxer", "Buldogue", "Cocker", "Golden","Husky Siberiano", "Pinscher", "Pug","Rottweiler", "Vira-Lata", "Yorkshire"};

        return caes;
    }

    public String[] ListaGatos() {

        String[] gatos = {"Azul russo", "Balinês", "Bombaim", "Chartreux", "Curl Americano", "Himalaio", "Korat", "Maine Coon", "Nebelung", "Persa", "Scottish Fold", "Siberiano"};

        return gatos;
    }

    public String[] ListaGatosFiltro() {

        String[] gatos = {"", "Azul russo", "Balinês", "Bombaim", "Chartreux", "Curl Americano", "Himalaio", "Korat", "Maine Coon", "Nebelung", "Persa", "Scottish Fold", "Siberiano"};

        return gatos;
    }


    public String[] ListaPorte() {

        String[] porte = {"Pequeno", "Médio", "Grande"};

        return porte;
    }

    public String[] ListaEspecie() {

        String[] especie = {"Cachorro", "Gato"};

        return especie;
    }

    public String[] ListaOpcoes() {

        String[] opcoes = {"Nome", "Raça", "Espécie"};

        return opcoes;
    }
}

