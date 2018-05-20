package com.fatec.br.adocaopet.Common;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fatec.br.adocaopet.Model.Adocoes;
import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Leo on 20/05/2018.
 */

public class AdocoesAdapter extends RecyclerView.Adapter<AdocoesAdapter.AdocoesViewHolder> {

    private List<Adocoes> listaAdocoes;

    public AdocoesAdapter(List<Adocoes> listaAdocoes) {
        this.listaAdocoes = listaAdocoes;
    }

    @Override
    public AdocoesAdapter.AdocoesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdocoesAdapter.AdocoesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adocao, parent, false));
    }

    @Override
    public void onBindViewHolder(final AdocoesAdapter.AdocoesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return listaAdocoes.size();
    }

    class AdocoesViewHolder extends RecyclerView.ViewHolder {

        TextView nomePet, nomeAdotante, dataAdocao;
        CircleImageView fotoCirclePet;
        ImageButton btnAceitar, btnRecusar;

        public AdocoesViewHolder(View itemView) {
            super(itemView);

            nomePet = (TextView) itemView.findViewById(R.id.editNomePetAdocao);
            nomeAdotante = (TextView) itemView.findViewById(R.id.editNomePetAdocao);
            dataAdocao = (TextView) itemView.findViewById(R.id.editDataAdocao);
            fotoCirclePet = (CircleImageView) itemView.findViewById(R.id.fotoCircleViewPetAdocaoTela);
            btnAceitar = (ImageButton) itemView.findViewById(R.id.btnAceitar);
            btnRecusar = (ImageButton) itemView.findViewById(R.id.btnRecusar);


        }
    }
}
