package com.fatec.br.adocaopet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Common.ListRaca;
import com.fatec.br.adocaopet.Common.PetAdapter;
import com.fatec.br.adocaopet.Common.PetAdapterBusca;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.R;
import com.fatec.br.adocaopet.Utils.SimpleDividerItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuscaPetActivity extends AppCompatActivity {

    private RecyclerView listaPets;
    private List<Pet> result;
    private PetAdapterBusca petAdapter;
    String identificacaoUsuario;
    FirebaseUser auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button btn_voltar;
    private ImageButton btn_pesquisa;
    private EditText editPesquisa;
    private Spinner cbEspecie;
    private ListRaca listRaca;
    private RadioButton rbPequeno, rbMedio, rbGrande, rbMacho, rbFemea, rbCachorro, rbGato;
    private RadioGroup rgPorte, rgEspecie, rgSexo;
    private CircleImageView fotoPet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscapet);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("pets");

        btn_voltar = (Button) findViewById(R.id.btnVoltarBuscaPet);
        btn_pesquisa = (ImageButton) findViewById(R.id.search_btn);
//        editPesquisa = (EditText) findViewById(R.id.editPesquisa);
        cbEspecie = (Spinner) findViewById(R.id.cbEspecieBusca);
        fotoPet = (CircleImageView) findViewById(R.id.fotoCircleViewPet);

        rbCachorro = (RadioButton) findViewById(R.id.rbCachorro);
        rbGato = (RadioButton) findViewById(R.id.rbGato);
        rbFemea = (RadioButton) findViewById(R.id.rbFemea);
        rbMacho = (RadioButton) findViewById(R.id.rbMacho);
        rbPequeno = (RadioButton) findViewById(R.id.rbPequeno);
        rbMedio = (RadioButton) findViewById(R.id.rbMedio);
        rbGrande = (RadioButton) findViewById(R.id.rbGrande);

        rgEspecie = (RadioGroup) findViewById(R.id.rgEspecie);
        rgPorte = (RadioGroup) findViewById(R.id.rgPorte);
        rgSexo = (RadioGroup) findViewById(R.id.rgSexo);

        result = new ArrayList<>();

        alimentaCombos();

        listaPets = (RecyclerView) findViewById(R.id.listaTodosPets);


        btn_voltar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(BuscaPetActivity.this, PerfilActivity.class);
                startActivity(i);
                finish();
            }
        });

        btn_pesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    atualizarLista();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void atualizarLista() throws InterruptedException {

        if (result.size() != 0) {
            result.clear();
        }

        String raca = cbEspecie.getSelectedItem().toString();

        String sexo = verificaSexo();
        String porte = verificaPorte();
        String especie = verificaEspecie();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count ", "Número de Pets" + dataSnapshot.getChildrenCount());
                for (DataSnapshot pets : dataSnapshot.getChildren()) {
                    Pet pet = pets.getValue(Pet.class);
                    if (pet.getPorte().equals(porte) && pet.getSexo().equals(sexo) && pet.getEspecie().equals(especie) && pet.getRaca().equals(raca)) {
                        result.add(pet);
                    }
                }
                listaPets.setHasFixedSize(true);

                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);

                listaPets.setLayoutManager(llm);

                petAdapter = new PetAdapterBusca(result);

                listaPets.setAdapter(petAdapter);

                listaPets.addItemDecoration(new SimpleDividerItemDecoration(
                        getApplicationContext()
                ));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Log.e("Pets", result.toString());
    }

    private int getItemIndex(Pet pet) {
        int index = -1;

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getIdPet().equals(pet.getIdPet())) {
                index = i;
                break;
            }

        }
        return index;
    }

    private void CheckListaVazia() {
        if (result.size() == 0) {
            Toast.makeText(BuscaPetActivity.this, "Pets não encontrados", Toast.LENGTH_SHORT).show();
        }
    }

    public void alimentaCombos() {
        rgEspecie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                listRaca = new ListRaca();

                if (rbCachorro.isChecked()) {
                    ArrayAdapter listaCachorros = new ArrayAdapter(BuscaPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaCachorro());
                    cbEspecie.setAdapter(listaCachorros);

                } else {
                    ArrayAdapter listaGatos = new ArrayAdapter(BuscaPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaGatos());
                    cbEspecie.setAdapter(listaGatos);
                }
            }
        });

    }

    public String verificaPorte() {
        if (rbPequeno.isChecked()) {
            return "Pequeno";
        } else if (rbMedio.isChecked()) {
            return "Médio";
        } else {
            return "Grande";
        }
    }

    public String verificaSexo() {
        if (rbMacho.isChecked()) {
            return "macho";
        }
        return "femea";
    }

    public String verificaEspecie() {
        if (rbGato.isChecked()) {
            return "Gato";
        }
        return "Cachorro";
    }
}
