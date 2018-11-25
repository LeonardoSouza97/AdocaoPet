package com.fatec.br.adocaopet.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.Common.PetAdapter;
import com.fatec.br.adocaopet.Common.PetAdapterBusca;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuscaPetActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView listaPets;
    private List<Pet> result;
    private PetAdapterBusca petAdapter;
    String identificacaoUsuario;
    private FirebaseAuth auth;
    private Dialog dialog;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FloatingActionButton btnFloatPesquisa;
    private CircleImageView fotoUsuario;
    private EditText editPesquisa;
    private TextView editNomeMenu, editEmailMenu;
    private Spinner cbEspecie;
    private ListRaca listRaca;
    private RadioButton rbPequeno, rbMedio, rbGrande, rbMacho, rbFemea, rbCachorro, rbGato;
    private RadioGroup rgPorte, rgEspecie, rgSexo;
    private CircleImageView fotoPet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_busca_pet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_busca_pet);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_buscapet);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        fotoUsuario = (CircleImageView) header.findViewById(R.id.imageFotoPerfil);

        editNomeMenu = (TextView) header.findViewById(R.id.txtNomeUsuario);
        editEmailMenu = (TextView) header.findViewById(R.id.txtEmail);

        editNomeMenu.setText(PerfilActivity.editNome.getText());
        editEmailMenu.setText(PerfilActivity.editEmail.getText());

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.view_filtro);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("pets");

        btnFloatPesquisa = (FloatingActionButton) findViewById(R.id.btnFloatSearch);
        fotoPet = (CircleImageView) findViewById(R.id.fotoCircleViewPet);

        cbEspecie = (Spinner) dialog.findViewById(R.id.cbEspecieBusca);
        rbCachorro = (RadioButton) dialog.findViewById(R.id.rbCachorro);
        rbGato = (RadioButton) dialog.findViewById(R.id.rbGato);
        rbFemea = (RadioButton) dialog.findViewById(R.id.rbFemea);
        rbMacho = (RadioButton) dialog.findViewById(R.id.rbMacho);
        rbPequeno = (RadioButton) dialog.findViewById(R.id.rbPequeno);
        rbMedio = (RadioButton) dialog.findViewById(R.id.rbMedio);
        rbGrande = (RadioButton) dialog.findViewById(R.id.rbGrande);

        rgEspecie = (RadioGroup) dialog.findViewById(R.id.rgEspecie);
        rgPorte = (RadioGroup) dialog.findViewById(R.id.rgPorte);
        rgSexo = (RadioGroup) dialog.findViewById(R.id.rgSexo);

        result = new ArrayList<>();

        pegarFotoUsuario();

        alimentaCombos();

        listaPets = (RecyclerView) findViewById(R.id.listaTodosPets);

        btnFloatPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    atualizarLista();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void atualizarLista() throws InterruptedException {
        String raca = "";

        if (cbEspecie.getSelectedItem() != null && !cbEspecie.getSelectedItem().equals("")) {
            raca = cbEspecie.getSelectedItem().toString();
        }

        if (result.size() != 0) {
            result.clear();
        }

        String sexo = verificaSexo();
        String porte = verificaPorte();
        String especie = verificaEspecie();
        String racaFinal = raca;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isPorte = true;
                boolean isEspecie = true;
                boolean isSexo = true;
                boolean isRaca = true;

                for (DataSnapshot pets : dataSnapshot.getChildren()) {
                    Pet pet = pets.getValue(Pet.class);

                    if (!especie.isEmpty()) {
                        isEspecie = pet.getEspecie().equals(especie);
                    }

                    if (!sexo.isEmpty()) {
                        isSexo = pet.getSexo().equals(sexo);
                    }

                    if (!porte.isEmpty()) {
                        isPorte = pet.getPorte().equals(porte);
                    }

                    if (!racaFinal.isEmpty()) {
                        isRaca = pet.getRaca().equals(racaFinal);
                    }

                    if (isPorte && isEspecie && isSexo && isRaca) {
                        result.add(pet);
                    }
                }

                CheckListaVazia();

                listaPets.setHasFixedSize(true);

                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);

                listaPets.setLayoutManager(llm);

                petAdapter = new PetAdapterBusca(result);

                listaPets.setAdapter(petAdapter);

                listaPets.addItemDecoration(new SimpleDividerItemDecoration(
                        getApplicationContext()
                ));

                rgEspecie.clearCheck();
                rgPorte.clearCheck();
                rgSexo.clearCheck();
                cbEspecie.setSelection(-1);

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
                    ArrayAdapter listaCachorros = new ArrayAdapter(BuscaPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaCachorroFiltro());
                    cbEspecie.setAdapter(listaCachorros);
                    cbEspecie.setSelection(-1);

                } else {
                    ArrayAdapter listaGatos = new ArrayAdapter(BuscaPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaGatosFiltro());
                    cbEspecie.setAdapter(listaGatos);
                    cbEspecie.setSelection(-1);
                }
            }
        });

    }

    public String verificaPorte() {
        if (rbPequeno.isChecked()) {
            return "Pequeno";
        } else if (rbMedio.isChecked()) {
            return "Médio";
        } else if (rbGrande.isChecked()) {
            return "Grande";
        } else {
            return "";
        }
    }

    public String verificaSexo() {
        if (rbMacho.isChecked()) {
            return "macho";
        } else if (rbFemea.isChecked()) {
            return "femea";
        } else {
            return "";
        }
    }

    public String verificaEspecie() {
        if (rbGato.isChecked()) {
            return "Gato";
        } else if (rbCachorro.isChecked()) {
            return "Cachorro";
        } else {
            return "";
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cadastrar_pet) {
            Intent i = new Intent(BuscaPetActivity.this, CadastroPetActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_principal) {
            Intent i = new Intent(BuscaPetActivity.this, PerfilActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_meus_pets) {
            Intent i = new Intent(BuscaPetActivity.this, MeusPetsActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_buscar_pet) {

        } else if (id == R.id.nav_editar_perfil) {
            Intent i = new Intent(BuscaPetActivity.this, AlterarUsuarioActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_buscapet);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pegarFotoUsuario() {

        auth = FirebaseAuth.getInstance();

        try {
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
            final long ONE_MEGABYTE = 1024 * 1024;
            firebaseStorage.child("user/" + auth.getCurrentUser().getUid() + ".png").getBytes(ONE_MEGABYTE)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Picasso.get().load(auth.getCurrentUser().getPhotoUrl()).into(fotoUsuario);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_buscapet);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
