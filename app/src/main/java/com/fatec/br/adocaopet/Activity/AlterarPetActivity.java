package com.fatec.br.adocaopet.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Common.Internet;
import com.fatec.br.adocaopet.Common.ListRaca;
import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.DAO.Conexao;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.Model.Usuario;
import com.fatec.br.adocaopet.R;
import com.fatec.br.adocaopet.Utils.Base64Custom;
import com.fatec.br.adocaopet.Utils.MaskEditUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class AlterarPetActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private CircleImageView fotoPet, fotoUsuario;
    private EditText nomePet;
    private EditText idadePet;
    private EditText pesoPet;
    private EditText descricaoPet;
    private Spinner cbEspecie;
    private Spinner cbPorte;
    private Spinner cbRaca;
    private RadioButton rbMacho, rbFemea, rbVermifugadoSim, rbVermifugadoNao, rbVacionadoSim, rbVacionadoNao;
    private RadioGroup rbgSexo, rbgVacinado, rbgVermifugado;
    private Button btnAlterarPet;
    private Button btnVoltar;
    private Pet pet;
    private Bitmap foto;
    private TextView editNome, editEmail;

    private ListRaca listRaca;

    private FirebaseAuth auth;
    private boolean hasPicture = false;
    private static final int REQUEST_CAMERA = 1000;
    String identificacaoUsuario;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_perfil_alterapet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_alterar_pet);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alterarpet);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        fotoUsuario = (CircleImageView) header.findViewById(R.id.imageFotoPerfil);
        editNome = (TextView) header.findViewById(R.id.txtNomeUsuario);
        editEmail = (TextView) header.findViewById(R.id.txtEmail);

        editNome.setText(PerfilActivity.editNome.getText());
        editEmail.setText(PerfilActivity.editEmail.getText());

        String identificadorPet = getIntent().getStringExtra("idPet");

        String[] identificadorPetSeparado = identificadorPet.split("/");

        identificacaoUsuario = FirebaseAuthUtils.getUUID();

        inicializaComponentes();

        alimentaCombos();

        auth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("pets").child(identificadorPetSeparado[4]);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    Pet petCarregado = dataSnapshot.getValue(Pet.class);

                    if (petCarregado == null) {
                        Notify.showNotify(AlterarPetActivity.this, "Nothing");
                    } else {
                        nomePet.setText(petCarregado.getNome());
                        idadePet.setText(petCarregado.getIdade());
                        descricaoPet.setText(petCarregado.getDescricao());
                        pesoPet.setText(petCarregado.getPeso());
                        cbEspecie.setSelection(getIndex(cbEspecie, petCarregado.getEspecie()));
                        cbPorte.setSelection(getIndex(cbPorte, petCarregado.getPorte()));
                        cbRaca.setSelection(getIndex(cbRaca, petCarregado.getRaca()));

                        if (petCarregado.getSexo().equals("macho")) {
                            rbMacho.setChecked(true);
                        } else {
                            rbFemea.setChecked(true);
                        }

                        if (petCarregado.getVermifugado().equals("sim")) {
                            rbVermifugadoSim.setChecked(true);
                        } else {
                            rbVermifugadoNao.setChecked(true);
                        }

                        if (petCarregado.getVacinado().equals("sim")) {
                            rbVacionadoSim.setChecked(true);
                        } else {
                            rbVacionadoNao.setChecked(true);
                        }

                        pegarfotoPet();
                        pegarFotoUsuarioMenu();
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Erro: " + databaseError.getDetails());
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlterarPetActivity.this, PerfilActivity.class);
                startActivity(i);
                finish();
            }
        });


        btnAlterarPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ValidarCampos();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void inicializaComponentes() {

        fotoPet = (CircleImageView) findViewById(R.id.fotoPet);
        nomePet = (EditText) findViewById(R.id.editNomePet);
        idadePet = (EditText) findViewById(R.id.editIdadePet);
        pesoPet = (EditText) findViewById(R.id.editPesoPet);
        cbEspecie = (Spinner) findViewById(R.id.cbEspecie);
        cbPorte = (Spinner) findViewById(R.id.cbPorte);
        cbRaca = (Spinner) findViewById(R.id.cbRaca);
        rbgSexo = (RadioGroup) findViewById(R.id.rbgSexo);
        rbMacho = (RadioButton) findViewById(R.id.rbMacho);
        rbFemea = (RadioButton) findViewById(R.id.rbFemea);
        descricaoPet = (EditText) findViewById(R.id.editDescricao);
        btnAlterarPet = (Button) findViewById(R.id.btnAlterarPet);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);
        rbVacionadoSim = (RadioButton) findViewById(R.id.rbVacinadoSim);
        rbVacionadoNao = (RadioButton) findViewById(R.id.rbVacinadoNao);
        rbVermifugadoNao = (RadioButton) findViewById(R.id.rbVermifugadoNao);
        rbVermifugadoSim = (RadioButton) findViewById(R.id.rbVermifugadoSim);
        rbgVacinado = (RadioGroup) findViewById(R.id.rbgVacinado);
        rbgVermifugado = (RadioGroup) findViewById(R.id.rbgVermifugado);

        pesoPet.addTextChangedListener(MaskEditUtil.mask(pesoPet, MaskEditUtil.FORMAT_PESO));
        idadePet.addTextChangedListener(MaskEditUtil.mask(idadePet, MaskEditUtil.FORMAT_IDADE));

    }

    private void alimentaCombos() {
        listRaca = new ListRaca();

        //Alimentando Combos

        ArrayAdapter listaEspecie = new ArrayAdapter(this, android.R.layout.select_dialog_item, listRaca.ListaEspecie());
        cbEspecie.setAdapter(listaEspecie);

        ArrayAdapter listaPorte = new ArrayAdapter(this, android.R.layout.select_dialog_item, listRaca.ListaPorte());
        cbPorte.setAdapter(listaPorte);

        cbEspecie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {

                    case 0:
                        ArrayAdapter listaCachorros = new ArrayAdapter(AlterarPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaCachorro());
                        cbRaca.setAdapter(listaCachorros);

                        Toast.makeText(AlterarPetActivity.this, "Cachorro", Toast.LENGTH_SHORT);
                        break;

                    case 1:
                        ArrayAdapter listaGatos = new ArrayAdapter(AlterarPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaGatos());
                        cbRaca.setAdapter(listaGatos);

                        Toast.makeText(AlterarPetActivity.this, "Gato", Toast.LENGTH_SHORT);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void atualizarPet() {

        String identificadorPet = getIntent().getStringExtra("idPet");

        String[] identificadorPetSeparado = identificadorPet.split("/");

        auth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("pets").child(identificadorPetSeparado[4]);

        ref.child("nome").setValue(pet.getNome());
        ref.child("email").setValue(auth.getCurrentUser().getEmail());
        ref.child("idade").setValue(pet.getIdade());
        ref.child("peso").setValue(pet.getPeso());
        ref.child("porte").setValue(pet.getPorte());
        ref.child("raca").setValue(pet.getRaca());
        ref.child("especie").setValue(pet.getEspecie());
        ref.child("sexo").setValue(pet.getSexo());
        ref.child("vermifugado").setValue(pet.getVermifugado());
        ref.child("vacinado").setValue(pet.getVacinado());
        ref.child("descricao").setValue(pet.getDescricao());

        Toast.makeText(AlterarPetActivity.this, getString(R.string.atualiza_pet_sucesso), Toast.LENGTH_SHORT).show();

        if (hasPicture) {
            AlterarFotoPet();
        }

        finishPet();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Conexao.getFirebaseAuth();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            foto = (Bitmap) data.getExtras().get("data");
            fotoPet.setImageBitmap(foto);
            hasPicture = true;
        }
    }

    public void savePetWithPicture() {

        String identificadorPet = getIntent().getStringExtra("idPet");

        String[] identificadorPetSeparado = identificadorPet.split("/");

        String identificadorPetGlobal = identificadorPetSeparado[4];

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child("pet/" + identificadorPetGlobal + ".png");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

        riversRef.putBytes(outputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                });
    }

    public void takeAPicture(View v) {
        useCamera();
    }

    public boolean useCamera() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            takeAPicture();
            return true;
        }
        return false;
    }

    public void takeAPicture() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, REQUEST_CAMERA);

    }


    public void finishPet() {
        Intent intentMap = new Intent(AlterarPetActivity.this, PerfilActivity.class);
        startActivity(intentMap);
        finish();
    }

    private void ValidarCampos() throws InterruptedException {
        nomePet.setError(null);
        idadePet.setError(null);
        pesoPet.setError(null);
        descricaoPet.setError(null);

        String nome = nomePet.getText().toString();
        String idade = idadePet.getText().toString();
        String peso = pesoPet.getText().toString();
        String descricao = descricaoPet.getText().toString();
        String raca = cbRaca.getSelectedItem().toString();

        if (TextUtils.isEmpty(nome)) {
            nomePet.setError(getString(R.string.error_field_required));
            nomePet.requestFocus();
        } else if (TextUtils.isEmpty(idade)) {
            idadePet.setError(getString(R.string.error_field_required));
            idadePet.requestFocus();
        } else if (TextUtils.isEmpty(peso)) {
            pesoPet.setError(getString(R.string.error_field_required));
            pesoPet.requestFocus();
        } else if (TextUtils.isEmpty(descricao)) {
            descricaoPet.setError(getString(R.string.error_field_required));
            descricaoPet.requestFocus();
        } else {
            if (!Internet.isNetworkAvailable(this)) {
                Notify.showNotify(this, getString(R.string.error_not_connected));
            } else {

                pet = new Pet();

                pet.setNome(nomePet.getText().toString());
                pet.setIdade(idadePet.getText().toString());
                pet.setDescricao(descricaoPet.getText().toString());
                pet.setPeso(pesoPet.getText().toString());
                pet.setPorte(cbPorte.getSelectedItem().toString());
                pet.setEspecie(cbEspecie.getSelectedItem().toString());
                pet.setRaca(cbRaca.getSelectedItem().toString());

                if (rbMacho.isChecked()) {
                    pet.setSexo("macho");
                } else {
                    pet.setSexo("femea");
                }

                if (rbVermifugadoSim.isChecked()) {
                    pet.setVermifugado("sim");
                } else {
                    pet.setVermifugado("não");
                }

                if (rbVacionadoSim.isChecked()) {
                    pet.setVacinado("sim");
                } else {
                    pet.setVacinado("não");
                }

                atualizarPet();
            }
        }

    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void pegarfotoPet() {

        String identificadorPet = getIntent().getStringExtra("idPet");

        String[] identificadorPetSeparado = identificadorPet.split("/");

        String identificadorPetGlobal = identificadorPetSeparado[4];

        try {
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
            final long ONE_MEGABYTE = 1024 * 1024;
            firebaseStorage.child("pet/" + identificadorPetGlobal + ".png").getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            fotoPet.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(bytes)));

                        }
                    });

        } catch (Exception e) {
            Notify.showNotify(this, e.getMessage());
            System.out.println(e.toString());
        }
    }

    private void AlterarFotoPet() {

        String identificadorPet = getIntent().getStringExtra("idPet");

        String[] identificadorPetSeparado = identificadorPet.split("/");

        String identificadorPetGlobal = identificadorPetSeparado[4];

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child("pet/" + identificadorPetGlobal + ".png");

        riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                savePetWithPicture();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_cadastrar_pet) {
            Intent i = new Intent(AlterarPetActivity.this, CadastroPetActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_principal) {
            Intent i = new Intent(AlterarPetActivity.this, PerfilActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_meus_pets) {
            Intent i = new Intent(AlterarPetActivity.this, MeusPetsActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_buscar_pet) {
            Intent i = new Intent(AlterarPetActivity.this, PerfilActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_editar_perfil) {
            Intent i = new Intent(AlterarPetActivity.this, AlterarUsuarioActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alterarpet);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_alterarpet);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent(AlterarPetActivity.this, MeusPetsActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void pegarFotoUsuarioMenu() {

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


}
