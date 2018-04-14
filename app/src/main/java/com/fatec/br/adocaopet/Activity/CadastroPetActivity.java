package com.fatec.br.adocaopet.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.fatec.br.adocaopet.Common.ListRaca;
import com.fatec.br.adocaopet.R;

/**
 * Created by Leo on 14/04/2018.
 */

public class CadastroPetActivity extends AppCompatActivity {

    private ImageView fotoPet;
    private EditText nomePet;
    private EditText idadePet;
    private EditText pesoPet;
    private Spinner cbEspecie;
    private Spinner cbPorte;
    private Spinner cbRaca;
    private RadioButton rbMacho;
    private RadioButton rbFemea;

    private ListRaca listRaca;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_cadastropet);

        inicializaComponentes();

        alimentaCombos();
        
    }

    private void inicializaComponentes() {

        fotoPet = (ImageView) findViewById(R.id.fotoPet);
        nomePet = (EditText) findViewById(R.id.editNomePet);
        idadePet = (EditText) findViewById(R.id.editIdadePet);
        pesoPet = (EditText) findViewById(R.id.editPesoPet);
        cbEspecie = (Spinner) findViewById(R.id.cbEspecie);
        cbPorte = (Spinner) findViewById(R.id.cbPorte);
        cbRaca = (Spinner) findViewById(R.id.cbRaca);
        rbMacho = (RadioButton) findViewById(R.id.rbMacho);
        rbFemea = (RadioButton) findViewById(R.id.rbFemea);

    }

    private void alimentaCombos(){
        listRaca = new ListRaca();

        //Alimentando Combos

        ArrayAdapter listaEspecie = new ArrayAdapter(this, android.R.layout.select_dialog_item, listRaca.ListaEspecie());
        cbEspecie.setAdapter(listaEspecie);

        ArrayAdapter listaPorte = new ArrayAdapter(this, android.R.layout.select_dialog_item, listRaca.ListaPorte());
        cbPorte.setAdapter(listaPorte);

        cbEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){

                    case 0:
                        ArrayAdapter listaCachorros = new ArrayAdapter(CadastroPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaCachorro());
                        cbRaca.setAdapter(listaCachorros);

                        Toast.makeText(CadastroPetActivity.this,"Cachorro",Toast.LENGTH_SHORT);
                        break;

                    case 1:
                        ArrayAdapter listaGatos = new ArrayAdapter(CadastroPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaGatos());
                        cbRaca.setAdapter(listaGatos);

                        Toast.makeText(CadastroPetActivity.this,"Gato",Toast.LENGTH_SHORT);
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
