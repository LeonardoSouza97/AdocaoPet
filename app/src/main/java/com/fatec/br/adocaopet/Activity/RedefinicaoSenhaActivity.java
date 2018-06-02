package com.fatec.br.adocaopet.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RedefinicaoSenhaActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editEmail;
    private Button btnEnviar, btnVoltar;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_redefinicao_senha);

        inicializaComponentes();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redefinirSenha();

            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishEnvioEmail();
            }
        });
    }


    private void redefinirSenha() {

        auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(editEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RedefinicaoSenhaActivity.this, getString(R.string.email_redefinicao_enviado), Toast.LENGTH_SHORT).show();
                    finishEnvioEmail();
                }else{
                    Toast.makeText(RedefinicaoSenhaActivity.this, getString(R.string.email_redefinicao_enviado_erro), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void inicializaComponentes() {

        editEmail = (EditText) findViewById(R.id.editEmail);
        btnEnviar = (Button) findViewById(R.id.btnEnviarEmail);
        btnEnviar = (Button) findViewById(R.id.btnEnviarEmail);
        btnVoltar = (Button) findViewById(R.id.btnVoltar);

    }

    private void finishEnvioEmail(){
        Intent i = new Intent(RedefinicaoSenhaActivity.this, LoginActivity.class);
        startActivity(i);
        finish();

    }




}