package com.fatec.br.adocaopet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fatec.br.adocaopet.Common.PetAdapter;
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

/**
 * Created by Leo on 15/04/2018.
 */

public class MeusPetsActivity extends AppCompatActivity {

    private RecyclerView listaPets;
    private List<Pet> result;
    private PetAdapter petAdapter;
    private Button btnVoltarPerfil;
    private TextView SemPets;
    String identificacaoUsuario;
    FirebaseUser auth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meuspets);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("pets");


        btnVoltarPerfil = (Button) findViewById(R.id.btnVoltarPerfil);
        SemPets = (TextView) findViewById(R.id.txtSemPets);

        listaPets = (RecyclerView) findViewById(R.id.recyclerViewPets);
        listaPets.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        result = new ArrayList<>();

        listaPets.setLayoutManager(llm);

        petAdapter = new PetAdapter(result);

        listaPets.setAdapter(petAdapter);

        atualizarLista();

        CheckListaVazia();

        btnVoltarPerfil.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(MeusPetsActivity.this, PerfilActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                removePet(item.getGroupId());
                break;
            case 1:
                Intent i = new Intent(MeusPetsActivity.this, AlterarPetActivity.class);
                i.putExtra("idPet", pegaPet(item.getGroupId()));
                startActivity(i);
                finish();
                break;
        }

        return super.onContextItemSelected(item);
    }


    private void atualizarLista() {

        auth = FirebaseAuth.getInstance().getCurrentUser();

        Query query = firebaseDatabase.getReference("pets").orderByChild("idUsuario").equalTo(auth.getUid());

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

    private void removePet(int position){
        databaseReference.child(result.get(position).getIdPet()).removeValue();

    }

    private String pegaPet(int position){
        String idPet = databaseReference.child(result.get(position).getIdPet()).toString();

        return idPet;
    }

    private void CheckListaVazia(){
        if(result.size() == 0 ){
            listaPets.setVisibility(View.INVISIBLE);
            SemPets.setVisibility(View.VISIBLE);
        }else{
            listaPets.setVisibility(View.VISIBLE);
            SemPets.setVisibility(View.INVISIBLE);
        }
    }
}
