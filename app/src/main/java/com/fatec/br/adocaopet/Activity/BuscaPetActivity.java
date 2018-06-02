package com.fatec.br.adocaopet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Common.ListRaca;
import com.fatec.br.adocaopet.Common.PetAdapter;
import com.fatec.br.adocaopet.Common.PetAdapterBusca;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

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
        editPesquisa = (EditText) findViewById(R.id.editPesquisa);
        cbEspecie = (Spinner) findViewById(R.id.cbEspecieBusca);
        fotoPet = (CircleImageView) findViewById(R.id.fotoCircleViewPet);

        alimentaCombos();

        listaPets = (RecyclerView) findViewById(R.id.listaTodosPets);
        listaPets.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        result = new ArrayList<>();

        listaPets.setLayoutManager(llm);

        petAdapter = new PetAdapterBusca(result);

        listaPets.setAdapter(petAdapter);

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

        result.clear();

        Query query;

        auth = FirebaseAuth.getInstance().getCurrentUser();

        String pesquisa = editPesquisa.getText().toString();

        if (TextUtils.isEmpty(pesquisa)) {
            editPesquisa.setError(getString(R.string.error_field_required));
            editPesquisa.requestFocus();

        } else {

            if (cbEspecie.getSelectedItem().toString().equals("Nome")) {

                query = firebaseDatabase.getReference("pets").orderByChild("nome").startAt(pesquisa).endAt(pesquisa + "\\uf8ff");

            } else if (cbEspecie.getSelectedItem().toString().equals("Raça")) {

                query = firebaseDatabase.getReference("pets").orderByChild("raca").startAt(pesquisa).endAt(pesquisa + "\\uf8ff");

            } else {

                query = firebaseDatabase.getReference("pets").orderByChild("especie").startAt(pesquisa).endAt(pesquisa + "\\uf8ff");
            }

            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    result.add(dataSnapshot.getValue(Pet.class));
                    petAdapter.notifyDataSetChanged();
                    CheckListaVazia();

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    int index = getItemIndex(pet);
                    result.set(index, pet);
                    petAdapter.notifyItemChanged(index);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Pet pet = dataSnapshot.getValue(Pet.class);

                    int index = getItemIndex(pet);

                    result.remove(index);
                    petAdapter.notifyItemRemoved(index);
                    CheckListaVazia();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
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

    private void alimentaCombos() {
        listRaca = new ListRaca();

        ArrayAdapter listaEspecie = new ArrayAdapter(this, android.R.layout.select_dialog_item, listRaca.ListaOpcoes());
        cbEspecie.setAdapter(listaEspecie);

    }

}
