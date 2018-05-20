package com.fatec.br.adocaopet.Activity;

import android.widget.Toast;

import com.fatec.br.adocaopet.Model.Adocoes;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.internal.ado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdocaoActivity {


    public void realizaAdocao(String idAdocao, String idDono, String idPet, String idAdotante, String data, String status) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataAdocao = database.getReference("adoções").child(idAdocao);

        dataAdocao.child("idAdocao").setValue(idAdocao);
        dataAdocao.child("idDono").setValue(idDono);
        dataAdocao.child("idAdotante").setValue(idAdotante);
        dataAdocao.child("dataAdocao").setValue(data);
        dataAdocao.child("status").setValue(status);
        dataAdocao.child("idPet").setValue(idPet);

    }

}

