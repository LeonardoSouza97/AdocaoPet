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

import de.hdodenhof.circleimageview.CircleImageView;

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
        holder.idadePet.setText(pets.getIdade() + " anos");
        holder.racaPet.setText(pets.getRaca());

        if(pets.getSexo().equals("femea")){
            holder.sexoPet.setBackgroundResource(R.mipmap.ic_feminino);
        }else{
            holder.sexoPet.setBackgroundResource(R.mipmap.ic_masculino);
        }

        if(pets.getEspecie().equals("Gato")){
            holder.especiePet.setBackgroundResource(R.mipmap.ic_gato);
        }else{
            holder.especiePet.setBackgroundResource(R.mipmap.ic_cachorro);
        }

        StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();


        try {
            StorageReference url_pet = firebaseStorage.child("pet/" + pets.getIdPet() + ".png");

            url_pet.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUrl) {
                    Picasso.get().load(downloadUrl.toString()).into(holder.fotoCirclePet);
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

        TextView nomePet, racaPet, idadePet;
        CircleImageView fotoPet, sexoPet, especiePet;
        CircleImageView fotoCirclePet;

        public PetsViewHolder(View itemView) {
            super(itemView);

            nomePet = (TextView) itemView.findViewById(R.id.editNomePetLista);
            racaPet = (TextView) itemView.findViewById(R.id.editRacaPetLista);
            idadePet = (TextView) itemView.findViewById(R.id.editIdadePetLista);
            sexoPet = (CircleImageView) itemView.findViewById(R.id.imageSexo);
            fotoCirclePet = (CircleImageView) itemView.findViewById(R.id.fotoCircleViewPet);
            especiePet = (CircleImageView) itemView.findViewById(R.id.imageEspecie);

        }
    }
}
