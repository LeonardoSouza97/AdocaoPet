package com.fatec.br.adocaopet.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fatec.br.adocaopet.Common.AdocoesConcluidasAdapter;
import com.fatec.br.adocaopet.Common.MinhasAdocoesAdapter;
import com.fatec.br.adocaopet.Model.Adocoes;
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

public class FragmentAdocaoConcluida extends android.support.v4.app.Fragment{

    View view;
    FirebaseUser auth;
    private AdocoesConcluidasAdapter adocoesAdapter;
    private RecyclerView listaAdocoes;
    private List<Adocoes> result;
    private Adocoes adocoes;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public FragmentAdocaoConcluida() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_adocoesconcluidas, container, false);

        listaAdocoes = (RecyclerView) view.findViewById(R.id.lista_adocoes_concluidas);
        listaAdocoes.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listaAdocoes.setLayoutManager(llm);
        listaAdocoes.setAdapter(adocoesAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("adoções");
        result = new ArrayList<>();
        adocoesAdapter = new AdocoesConcluidasAdapter(result);

        try {
            atualizarLista();

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                removeAdocao(item.getGroupId());
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void removeAdocao(int position){
        databaseReference.child(result.get(position).getIdAdocao()).removeValue();
    }

    private void atualizarLista() throws InterruptedException {
        Query query;

        auth = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        query = database.getReference("adoções").orderByChild("status").equalTo(auth.getUid() + "Confirmado");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                result.add(dataSnapshot.getValue(Adocoes.class));
                adocoesAdapter.notifyDataSetChanged();
                CheckListaVazia();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adocoes = dataSnapshot.getValue(Adocoes.class);
                int index = getItemIndex(adocoes);
                result.set(index, adocoes);
                adocoesAdapter.notifyItemChanged(index);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adocoes = dataSnapshot.getValue(Adocoes.class);
                int index = getItemIndex(adocoes);
                result.remove(index);
                adocoesAdapter.notifyItemRemoved(index);
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

    private int getItemIndex(Adocoes adocoes) {

        int index = -1;

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getIdAdocao().equals(adocoes.getIdAdocao())) {
                index = i;
                break;
            }

        }
        return index;
    }


    private void CheckListaVazia() {
        if (result.size() == 0) {

        }
    }

}
