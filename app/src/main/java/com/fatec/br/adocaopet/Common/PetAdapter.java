package com.fatec.br.adocaopet.Common;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Leo on 15/04/2018.
 */

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetsViewHolder> {

    private List<Pet> listaPets;

    public PetAdapter(List<Pet> listaPets) {
        this.listaPets = listaPets;
    }

    @Override
    public PetsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PetsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pets, parent, false));
    }

    @Override
    public void onBindViewHolder(final PetsViewHolder holder, int position) {

        Pet pets = listaPets.get(position);

        holder.nomePet.setText(pets.getNome());
        holder.idadePet.setText(pets.getIdade());
        holder.descricaoPet.setText(pets.getDescricao());

        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();


        try {
            StorageReference url_pet = firebaseStorage.child("pet/" + pets.getIdPet() + ".png");

            url_pet.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUrl) {
                    Picasso.get().load(downloadUrl.toString()).into(holder.fotoPet);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
                menu.add(holder.getAdapterPosition(), 0, 0, "Remover Pet");
                menu.add(holder.getAdapterPosition(), 1, 0, "Atualizar Pet");
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaPets.size();
    }

    class PetsViewHolder extends RecyclerView.ViewHolder {

        TextView nomePet, descricaoPet, idadePet;
        ImageView fotoPet;

        public PetsViewHolder(View itemView) {
            super(itemView);

            fotoPet = (ImageView) itemView.findViewById(R.id.fotoPetView);
            nomePet = (TextView) itemView.findViewById(R.id.editNomePetLista);
            descricaoPet = (TextView) itemView.findViewById(R.id.editIdadePetLista);
            idadePet = (TextView) itemView.findViewById(R.id.editDescricaoPetLista);

        }
    }
}
