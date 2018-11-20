package com.fatec.br.adocaopet.Common;

public class ListRaca {

    public String[] ListaCachorro() {

        String[] caes = {"Beagle", "Pinscher", "Boxer", "Vira-Lata"};

        return caes;
    }

    public String[] ListaCachorroFiltro() {

        String[] caes = {"", "Beagle", "Pinscher", "Boxer", "Vira-Lata"};

        return caes;
    }

    public String[] ListaGatos() {

        String[] gatos = {"Persa", "Azul russo", "Maine Coon"};

        return gatos;
    }

    public String[] ListaGatosFiltro() {

        String[] gatos = {"", "Persa", "Azul russo", "Maine Coon"};

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

