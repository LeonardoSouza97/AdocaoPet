package com.fatec.br.adocaopet.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

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
    private AutoCompleteTextView autoEditRaca;
    private RadioButton rbMacho;
    private RadioButton rbFemea;

    private ListRaca listRaca;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_cadastropet);

        inicializaComponentes();

        listRaca = new ListRaca();

        //Alimentando AutoCompleteTextView
        ArrayAdapter listaCachorros = new ArrayAdapter(this, android.R.layout.select_dialog_item, listRaca.ListaCachorro());
        autoEditRaca.setThreshold(1);
        autoEditRaca.setAdapter(listaCachorros);


    }


    private void inicializaComponentes() {

        autoEditRaca = (AutoCompleteTextView) findViewById(R.id.autoEditRaca);
        fotoPet = (ImageView) findViewById(R.id.fotoPet);
        nomePet = (EditText) findViewById(R.id.editNomePet);
        idadePet = (EditText) findViewById(R.id.editIdadePet);
        pesoPet = (EditText) findViewById(R.id.editPesoPet);
        cbEspecie = (Spinner) findViewById(R.id.cbEspecie);
        cbPorte = (Spinner) findViewById(R.id.cbPorte);
        rbMacho = (RadioButton) findViewById(R.id.rbMacho);
        rbFemea = (RadioButton) findViewById(R.id.rbFemea);

    }
}
