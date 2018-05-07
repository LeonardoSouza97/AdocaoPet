package com.fatec.br.adocaopet.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.fatec.br.adocaopet.Utils.Preferencias;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by Leo on 14/04/2018.
 */

public class CadastroPetActivity extends AppCompatActivity {

    private ImageView fotoPet;
    private EditText nomePet;
    private EditText idadePet;
    private EditText pesoPet;
    private EditText descricaoPet;
    private Spinner cbEspecie;
    private Spinner cbPorte;
    private Spinner cbRaca;
    private RadioButton rbMacho;
    private RadioButton rbFemea;
    private RadioGroup rbgSexo;
    private Button btnConfirmar;
    private Button btnVoltar;
    private Usuario usuario;
    private Pet pet;
    private Bitmap foto;

    private ListRaca listRaca;

    private FirebaseAuth auth;
    private boolean hasPicture = false;
    private static final int REQUEST_CAMERA = 1000;
    String identificacaoUsuario;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_cadastropet);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();
        inicializaComponentes();

        alimentaCombos();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CadastroPetActivity.this, PerfilActivity.class);
                startActivity(i);
                finish();
            }
        });


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
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

        fotoPet = (ImageView) findViewById(R.id.fotoPet);
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
        btnConfirmar = (Button) findViewById(R.id.btnCadastrarPet);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);

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
                        ArrayAdapter listaCachorros = new ArrayAdapter(CadastroPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaCachorro());
                        cbRaca.setAdapter(listaCachorros);

                        Toast.makeText(CadastroPetActivity.this, "Cachorro", Toast.LENGTH_SHORT);
                        break;

                    case 1:
                        ArrayAdapter listaGatos = new ArrayAdapter(CadastroPetActivity.this, android.R.layout.select_dialog_item, listRaca.ListaGatos());
                        cbRaca.setAdapter(listaGatos);

                        Toast.makeText(CadastroPetActivity.this, "Gato", Toast.LENGTH_SHORT);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void criarPet() {

        auth = FirebaseAuth.getInstance();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("pets").child(pet.getIdPet());

        ref.child("idPet").setValue(pet.getIdPet());
        ref.child("nome").setValue(pet.getNome());
        ref.child("idade").setValue(pet.getIdade());
        ref.child("peso").setValue(pet.getPeso());
        ref.child("porte").setValue(pet.getPorte());
        ref.child("raca").setValue(pet.getRaca());
        ref.child("especie").setValue(pet.getEspecie());
        ref.child("sexo").setValue(pet.getSexo());
        ref.child("descricao").setValue(pet.getDescricao());
        ref.child("idUsuario").setValue(auth.getCurrentUser().getUid());

        Toast.makeText(CadastroPetActivity.this, getString(R.string.cadastro_pet_sucesso), Toast.LENGTH_SHORT).show();


        if (hasPicture) {
            savePetWithPicture();
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

    public String converte(String text) {
        return text.replaceAll("[ãâàáä]", "a")
                .replaceAll("[êèéë]", "e")
                .replaceAll("[îìíï]", "i")
                .replaceAll("[õôòóö]", "o")
                .replaceAll("[ûúùü]", "u")
                .replaceAll("[ÃÂÀÁÄ]", "A")
                .replaceAll("[ÊÈÉË]", "E")
                .replaceAll("[ÎÌÍÏ]", "I")
                .replaceAll("[ÕÔÒÓÖ]", "O")
                .replaceAll("[ÛÙÚÜ]", "U")
                .replace('ç', 'c')
                .replace('Ç', 'C')
                .replace('ñ', 'n')
                .replace('Ñ', 'N')
                .replace("'", "")
                .replace("?", "")
                .replace("!", "")
                .replace("$", "")
                .replace("%", "")
                .replace("=", "")
                .replace("^", "")
                .replace("~", "")
                .replace(":", "")
                .replace(";", "")
                .replace("º", "")
                .replace("ª", "")
                .replace("+", "")
                .replace("_", "")
                .replace("#", "")
                .replace("´", "")
                .replace("`", "")
                .replace("@", "")
                .replace("¨", "")
                .replace("*", "")
                .replace("_", "")
                .replace("\\(\\)\\=\\{\\}\\[\\]\\~\\^\\]","")
                .replace("[\\.\\;\\-\\_\\+\\'\\ª\\º\\:\\;\\/]","")
                .replace(" ","");
    }

    public void savePetWithPicture() {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child("pet/" + pet.getIdPet() + ".png");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

        riversRef.putBytes(outputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
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
        Intent intentMap = new Intent(CadastroPetActivity.this, PerfilActivity.class);
        startActivity(intentMap);
        finish();
    }

    private void ValidarCampos() throws InterruptedException {


        FirebaseUser user = auth.getCurrentUser();

        nomePet.setError(null);
        idadePet.setError(null);
        pesoPet.setError(null);
        descricaoPet.setError(null);


        String nome = nomePet.getText().toString();
        String idade = idadePet.getText().toString();
        String peso = pesoPet.getText().toString();
        String descricao = descricaoPet.getText().toString();
        String raca = cbRaca.getSelectedItem().toString();

        boolean valid = true;

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

                String strCatId = nome + raca + user.getUid();
                String strCatId2 = converte(strCatId);

                pet.setIdPet(strCatId2.trim());
                pet.setNome(nomePet.getText().toString());
                pet.setIdade(idadePet.getText().toString());
                pet.setDescricao(descricaoPet.getText().toString());
                pet.setPeso(pesoPet.getText().toString());
                pet.setPorte(cbPorte.getSelectedItem().toString());
                pet.setEspecie(cbEspecie.getSelectedItem().toString());
                pet.setRaca(cbRaca.getSelectedItem().toString());
                pet.setDescricao(descricaoPet.getText().toString());
                pet.setIdUsuario(identificacaoUsuario);

                if (rbMacho.isChecked()) {
                    pet.setSexo("macho");
                } else {
                    pet.setSexo("femea");
                }

                criarPet();

            }
        }


    }

}

