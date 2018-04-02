package com.fatec.br.adocaopet.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Common.Internet;
import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.DAO.Conexao;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.Model.Usuario;
import com.fatec.br.adocaopet.R;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static java.lang.Thread.sleep;

/**
 * Created by Leo on 25/03/2018.
 */

public class AlterarUsuarioActivity extends AppCompatActivity {

    private EditText editNome, editSenha, editEndereco, editConfirmarSenha, editTelefone, editCpf, editRg, editDataNasc;
    private FirebaseAuth auth;
    private Button btnAlterar, btnVoltar;
    private Usuario usuario;
    private ImageView fotoUsuario;
    private Bitmap foto;
    String identificacaoUsuario;

    private boolean hasPicture = false;
    private static final int REQUEST_CAMERA = 1000;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_alterarusuario);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();

        fotoUsuario = (ImageView) findViewById(R.id.imageCamera);
        editNome = (EditText) findViewById(R.id.editNome);
        editSenha = (EditText) findViewById(R.id.editPassword);
        editConfirmarSenha = (EditText) findViewById(R.id.editConfirmPassword);
        editTelefone = (EditText) findViewById(R.id.editTelefone);
        editCpf = (EditText) findViewById(R.id.editCpf);
        editRg = (EditText) findViewById(R.id.editRg);
        editEndereco = (EditText) findViewById(R.id.editEndereco);
        editDataNasc = (EditText) findViewById(R.id.editDataNasc);
        btnAlterar = findViewById(R.id.btnAlterar);

        editTelefone.addTextChangedListener(MaskEditUtil.mask(editTelefone, MaskEditUtil.FORMAT_FONE));
        editCpf.addTextChangedListener(MaskEditUtil.mask(editCpf, MaskEditUtil.FORMAT_CPF));
        editRg.addTextChangedListener(MaskEditUtil.mask(editRg, MaskEditUtil.FORMAT_RG));
        editDataNasc.addTextChangedListener(MaskEditUtil.mask(editDataNasc, MaskEditUtil.FORMAT_DATE));


        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(auth.getCurrentUser().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    Usuario usuarioCarregado = dataSnapshot.getValue(Usuario.class);

                    if (usuarioCarregado == null) {
                        Notify.showNotify(AlterarUsuarioActivity.this, "Nothing");
                    } else {
                        editNome.setText(usuarioCarregado.getNome());
                        editTelefone.setText(usuarioCarregado.getTelefone());
                        editEndereco.setText(usuarioCarregado.getEndereco());
                        editSenha.setText(usuarioCarregado.getSenha());
                        editConfirmarSenha.setText(usuarioCarregado.getSenha());
                        editRg.setText(usuarioCarregado.getRg());
                        editCpf.setText(usuarioCarregado.getCpf());
                        editDataNasc.setText(usuarioCarregado.getDataNasc());

                        pegarFotoUsuario();
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

        btnAlterar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                try {
                    ValidarCampos();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void pegarFotoUsuario() {
        try {
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
            final long ONE_MEGABYTE = 1024 * 1024;
            firebaseStorage.child("user/" + identificacaoUsuario + ".png").getBytes(ONE_MEGABYTE)
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

    private void atualizaPerfilUsuario() {

        auth = FirebaseAuth.getInstance();

        AlterarSenha();

        auth.signInWithEmailAndPassword(auth.getCurrentUser().getEmail(), usuario.getSenha())
                .addOnCompleteListener(AlterarUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("users").child(auth.getCurrentUser().getUid());

                            ref.child("uid").setValue(auth.getCurrentUser().getUid());
                            ref.child("nome").setValue(usuario.getNome());
                            ref.child("senha").setValue(usuario.getSenha());
                            ref.child("telefone").setValue(usuario.getTelefone());
                            ref.child("endereco").setValue(usuario.getEndereco());
                            ref.child("rg").setValue(usuario.getRg());
                            ref.child("cpf").setValue(usuario.getCpf());
                            ref.child("dataNasc").setValue(usuario.getDataNasc());


                            Toast.makeText(AlterarUsuarioActivity.this, getString(R.string.atualiza_usuario_sucesso), Toast.LENGTH_SHORT).show();


                            if (hasPicture) {
                                AlterarFotoUsuario();
                            }

                            Preferencias preferencias = new Preferencias(AlterarUsuarioActivity.this);
                            preferencias.salvarUsuarioPreferencias(usuario.getId(), usuario.getNome());

                            finishUsuario();

                        } else {

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                editSenha.setError("No mínimo 6 caracteres");
                                editSenha.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(AlterarUsuarioActivity.this, getString(R.string.generic_error), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }


                            Toast.makeText(AlterarUsuarioActivity.this, getString(R.string.cadastro_usuario_erro), Toast.LENGTH_SHORT).show();

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

    private void saveUserWithPicture() {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child("user/" + identificacaoUsuario + ".png");

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

        editTelefone.addTextChangedListener(MaskEditUtil.mask(editTelefone, MaskEditUtil.FORMAT_FONE));
        editCpf.addTextChangedListener(MaskEditUtil.mask(editCpf, MaskEditUtil.FORMAT_CPF));
        editRg.addTextChangedListener(MaskEditUtil.mask(editRg, MaskEditUtil.FORMAT_RG));
        editDataNasc.addTextChangedListener(MaskEditUtil.mask(editDataNasc, MaskEditUtil.FORMAT_DATE));

    }

    private void ValidarCampos() throws InterruptedException {

        editNome.setError(null);
        editSenha.setError(null);
        editConfirmarSenha.setError(null);
        editTelefone.setError(null);
        editRg.setError(null);
        editCpf.setError(null);
        editDataNasc.setError(null);
        editEndereco.setError(null);

        String nome = editNome.getText().toString();
        String senha = editSenha.getText().toString();
        String confirmaSenha = editConfirmarSenha.getText().toString();
        String telefone = editTelefone.getText().toString();
        String RG = editRg.getText().toString();
        String dataNasc = editDataNasc.getText().toString();
        String endereco = editEndereco.getText().toString();
        String cpf = editCpf.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(nome)) {
            editNome.setError(getString(R.string.error_field_required));
            editNome.requestFocus();
        } else if (TextUtils.isEmpty(senha)) {
            editSenha.setError(getString(R.string.error_field_required));
            editSenha.requestFocus();
        } else if (TextUtils.isEmpty(confirmaSenha)) {
            editConfirmarSenha.setError(getString(R.string.error_field_required));
            editConfirmarSenha.requestFocus();
        } else if (TextUtils.isEmpty(telefone)) {
            editTelefone.setError(getString(R.string.error_field_required));
            editTelefone.requestFocus();
        } else if (TextUtils.isEmpty(cpf)) {
            editCpf.setError(getString(R.string.error_field_required));
            editCpf.requestFocus();
        } else if (TextUtils.isEmpty(RG)) {
            editRg.setError(getString(R.string.error_field_required));
            editRg.requestFocus();
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
                    usuario.setSenha(editSenha.getText().toString());
                    usuario.setTelefone(editTelefone.getText().toString());
                    usuario.setEndereco(editEndereco.getText().toString());
                    usuario.setCpf(editCpf.getText().toString());
                    usuario.setRg(editRg.getText().toString());
                    usuario.setDataNasc(editDataNasc.getText().toString());

                    atualizaPerfilUsuario();

                } else {
                    Toast.makeText(AlterarUsuarioActivity.this, getString(R.string.not_same_password), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private void AlterarSenha() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(editSenha.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Senha alterada com sucesso!", Toast.LENGTH_SHORT);
                        } else {
                            Toast.makeText(getApplicationContext(), "Erro ao alterar a senha", Toast.LENGTH_SHORT);
                        }
                    }
                });

    }

    private void AlterarFotoUsuario() {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = storageRef.child("user/" + identificacaoUsuario + ".png");

        riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                saveUserWithPicture();
            }
        });

    }


    private void finishUsuario() {

        Intent intentMap = new Intent(AlterarUsuarioActivity.this, PerfilActivity.class);
        startActivity(intentMap);
        finish();

    }
}
