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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Common.Internet;
import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.R;
import com.fatec.br.adocaopet.DAO.Conexao;
import com.fatec.br.adocaopet.Model.Usuario;
import com.fatec.br.adocaopet.Utils.Base64Custom;
import com.fatec.br.adocaopet.Utils.MaskEditUtil;
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
    private boolean existeUsuario;
    String identificacaoUsuario;

    private boolean hasPicture = false;
    private static final int REQUEST_CAMERA = 1000;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_cadastrousuario);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();
        inicializaComponentes();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
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
                            ref.child("dataNasc").setValue(usuario.getDataNasc());

                            Toast.makeText(CadastroUsuarioActivity.this, getString(R.string.cadastro_usuario_sucesso), Toast.LENGTH_SHORT).show();


                            if (hasPicture) {
                                saveUserWithPicture();
                            }

                            Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                            preferencias.salvarUsuarioPreferencias(usuario.getId(), usuario.getNome());

                            finishUsuario();

                        } else {

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                editSenha.setError("No mínimo 6 caracteres");
                                editSenha.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editEmail.setError("E-mail inválido");
                                editEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                editEmail.setError("E-mail já cadastrado");
                                editEmail.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(CadastroUsuarioActivity.this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

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
        fotoUsuario = (ImageView) findViewById(R.id.imageCamera);
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

        editTelefone.addTextChangedListener(MaskEditUtil.mask(editTelefone, MaskEditUtil.FORMAT_FONE));
        editCpf.addTextChangedListener(MaskEditUtil.mask(editCpf, MaskEditUtil.FORMAT_CPF));
        editRG.addTextChangedListener(MaskEditUtil.mask(editRG, MaskEditUtil.FORMAT_RG));
        editDataNasc.addTextChangedListener(MaskEditUtil.mask(editDataNasc, MaskEditUtil.FORMAT_DATE));
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


    public void finishUsuario() {
        Intent intentMap = new Intent(CadastroUsuarioActivity.this, PerfilActivity.class);
        startActivity(intentMap);
        finish();
    }

    private void ValidarCampos() throws InterruptedException {

        editNome.setError(null);
        editEmail.setError(null);
        editSenha.setError(null);
        editConfirmarSenha.setError(null);
        editTelefone.setError(null);
        editRG.setError(null);
        editCpf.setError(null);
        editDataNasc.setError(null);
        editEndereco.setError(null);

        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        String confirmaSenha = editConfirmarSenha.getText().toString();
        String telefone = editTelefone.getText().toString();
        String RG = editRG.getText().toString();
        String dataNasc = editDataNasc.getText().toString();
        String endereco = editEndereco.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(nome)) {
            editNome.setError(getString(R.string.error_field_required));
            editNome.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            editEmail.setError(getString(R.string.error_field_required));
            editEmail.requestFocus();
        } else if (TextUtils.isEmpty(senha)) {
            editSenha.setError(getString(R.string.error_field_required));
            editSenha.requestFocus();
        } else if (TextUtils.isEmpty(confirmaSenha)) {
            editConfirmarSenha.setError(getString(R.string.error_field_required));
            editConfirmarSenha.requestFocus();
        } else if (TextUtils.isEmpty(telefone)) {
            editTelefone.setError(getString(R.string.error_field_required));
            editTelefone.requestFocus();
        } else if (TextUtils.isEmpty(RG)) {
            editRG.setError(getString(R.string.error_field_required));
            editRG.requestFocus();
        } else if (TextUtils.isEmpty(dataNasc)) {
            editDataNasc.setError(getString(R.string.error_field_required));
            editDataNasc.requestFocus();
        } else if (TextUtils.isEmpty(endereco)) {
            editEndereco.setError(getString(R.string.error_field_required));
            editEndereco.requestFocus();
        } else {
            if (!Internet.isNetworkAvailable(this)) {
                Notify.showNotify(this, getString(R.string.error_not_connected));
            } else {

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
        }
    }


}

