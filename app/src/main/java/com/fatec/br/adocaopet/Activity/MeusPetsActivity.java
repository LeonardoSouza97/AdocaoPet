package com.fatec.br.adocaopet.Activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.Common.PetAdapter;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.R;
import com.fatec.br.adocaopet.Utils.SimpleDividerItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeusPetsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView listaPets;
    private List<Pet> result;
    private PetAdapter petAdapter;
    private TextView SemPets, editNome, editEmail;
    CircleImageView fotoUsuario;
    private FirebaseAuth firebaseAuth;
    String identificacaoUsuario;
    FirebaseUser auth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_meuspets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_meus_pets);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_meuspets);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        editNome = header.findViewById(R.id.txtNomeUsuario);
        editEmail = header.findViewById(R.id.txtEmail);

        editNome.setText(PerfilActivity.editNome.getText());
        editEmail.setText(PerfilActivity.editEmail.getText());

        fotoUsuario = (CircleImageView) header.findViewById(R.id.imageFotoPerfil);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("pets");

        SemPets = (TextView) findViewById(R.id.txtSemPets);

        listaPets = (RecyclerView) findViewById(R.id.recyclerViewPets);
        listaPets.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        result = new ArrayList<>();
        listaPets.setLayoutManager(llm);
        petAdapter = new PetAdapter(result);
        listaPets.setAdapter(petAdapter);

        firebaseAuth = FirebaseAuth.getInstance();

        atualizarLista();
        pegarFotoUsuario();
        CheckListaVazia();

        listaPets.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));

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

    private void removePet(int position) {
        databaseReference.child(result.get(position).getIdPet()).removeValue();

    }

    private String pegaPet(int position) {
        String idPet = databaseReference.child(result.get(position).getIdPet()).toString();

        return idPet;
    }

    private void CheckListaVazia() {
        if (result.size() == 0) {
            listaPets.setVisibility(View.INVISIBLE);
            SemPets.setVisibility(View.VISIBLE);
        } else {
            listaPets.setVisibility(View.VISIBLE);
            SemPets.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cadastrar_pet) {
            Intent i = new Intent(MeusPetsActivity.this, CadastroPetActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_principal) {
            Intent i = new Intent(MeusPetsActivity.this, PerfilActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_meus_pets) {

        } else if (id == R.id.nav_buscar_pet) {
            Intent i = new Intent(MeusPetsActivity.this, BuscaPetActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_editar_perfil) {
            Intent i = new Intent(MeusPetsActivity.this, AlterarUsuarioActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_meuspets);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pegarFotoUsuario() {

        try {
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
            final long ONE_MEGABYTE = 1024 * 1024;
            firebaseStorage.child("user/" + firebaseAuth.getCurrentUser().getUid() + ".png").getBytes(ONE_MEGABYTE)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Picasso.get().load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(fotoUsuario);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            fotoUsuario.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(bytes)));
                        }

                    });

        } catch (Exception e) {
            Notify.showNotify(this, e.getMessage());
            System.out.println(e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_meuspets);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
