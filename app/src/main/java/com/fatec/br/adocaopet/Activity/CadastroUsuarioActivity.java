package com.fatec.br.adocaopet.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fatec.br.adocaopet.R;
import com.fatec.br.adocaopet.DAO.Conexao;
import com.fatec.br.adocaopet.Model.Usuario;
import com.fatec.br.adocaopet.Utils.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha, editEndereco, editCpf, editRG, editDataNasc, editConfirmarSenha, editTelefone;
    private FirebaseAuth auth;
    private Button btnRegistrar, btnVoltar;
    private Usuario usuario;
    private ImageView fotoUsuario;
    private Bitmap foto;

    private boolean hasPicture = false;
    private static final int REQUEST_CAMERA = 1000;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_cadastrousuario);
        inicializaComponentes();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSenha.getText().toString().equals(editConfirmarSenha.getText().toString())) {
                    usuario = new Usuario();

                    usuario.setNome(editNome.getText().toString());
                    usuario.setEmail(editEmail.getText().toString());
                    usuario.setSenha(editSenha.getText().toString());
                    usuario.setTelefone(editTelefone.getText().toString());
                    usuario.setEndereco(editEndereco.getText().toString());
                    usuario.setCpf(editCpf.getText().toString());
                    usuario.setRg(editRG.getText().toString());
                    usuario.setDataNasc(editDataNasc.getText().toString());
                    usuario.setId(Base64Custom.codificarBase64(usuario.getEmail()));
                    criarUser();
                } else {
                    Toast.makeText(CadastroUsuarioActivity.this, getString(R.string.not_same_password), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }



    private void criarUser() {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("users").child(auth.getCurrentUser().getUid());

                            ref.child("uid").setValue(auth.getCurrentUser().getUid());
                            ref.child("nome").setValue(usuario.getNome());
                            ref.child("email").setValue(usuario.getEmail());
                            ref.child("senha").setValue(usuario.getSenha());
                            ref.child("telefone").setValue(usuario.getTelefone());
                            ref.child("endereco").setValue(usuario.getEndereco());
                            ref.child("rg").setValue(usuario.getRg());
                            ref.child("cpf").setValue(usuario.getCpf());
                            ref.child("data_nasc").setValue(usuario.getDataNasc());

                            Toast.makeText(CadastroUsuarioActivity.this, getString(R.string.cadastro_usuario_sucesso), Toast.LENGTH_SHORT).show();

                            if (hasPicture) {
                                saveUserWithPicture();
                            }

                        } else {
                            Toast.makeText(CadastroUsuarioActivity.this, getString(R.string.cadastro_usuario_erro), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
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
            fotoUsuario.setImageBitmap(foto);
            hasPicture = true;
        }
    }

    public void saveUserWithPicture() {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child("user/" + usuario.getId() + ".png");

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

    public void inicializaComponentes() {
        editNome = (EditText) findViewById(R.id.editNome);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editSenha = (EditText) findViewById(R.id.editPassword);
        editConfirmarSenha = (EditText) findViewById(R.id.editConfirmPassword);
        editTelefone = (EditText) findViewById(R.id.editTelefone);
        editEndereco = editEndereco = (EditText) findViewById(R.id.editEndereco);
        editCpf = (EditText) findViewById(R.id.editCpf);
        editRG = (EditText) findViewById(R.id.editRg);
        editDataNasc = (EditText) findViewById(R.id.editDataNasc);

        btnRegistrar = (Button) findViewById(R.id.btnCadastrarUsuario);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);
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
}
