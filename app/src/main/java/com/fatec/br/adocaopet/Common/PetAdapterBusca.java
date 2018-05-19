package com.fatec.br.adocaopet.Common;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class PetAdapterBusca extends RecyclerView.Adapter<PetAdapterBusca.PetsViewHolder> {

    private List<Pet> listaPets;
    Dialog dialog;

    public PetAdapterBusca(List<Pet> listaPets) {
        this.listaPets = listaPets;
    }


    @Override
    public PetsViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pets, parent, false);
        final PetsViewHolder viewHolder = new PetsViewHolder(view);

        //Dados exibidos quando o usuário clica em algum pet na tela de Busca.
        dialog = new Dialog(parent.getContext());
        dialog.setContentView(R.layout.activity_visualizapet);

        viewHolder.petPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView nomePetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_name_pet);
                TextView racaPetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_raca_pet);
                TextView idadePetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_idade_pet);
                final CircleImageView fotoPetTelaVisualiza = (CircleImageView) dialog.findViewById(R.id.dialog_pet_foto);

                nomePetTelaVisualiza.setText(listaPets.get(viewHolder.getAdapterPosition()).getNome());
                racaPetTelaVisualiza.setText(listaPets.get(viewHolder.getAdapterPosition()).getRaca());
                idadePetTelaVisualiza.setText(listaPets.get(viewHolder.getAdapterPosition()).getIdade());

                StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                try {
                    StorageReference url_pet = firebaseStorage.child("pet/" + (listaPets.get(viewHolder.getAdapterPosition()).getIdPet().toString() + ".png"));

                    url_pet.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            Picasso.get().load(downloadUrl.toString()).into(fotoPetTelaVisualiza);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PetsViewHolder holder, int position) {

        Pet pets = listaPets.get(position);

        holder.nomePet.setText(pets.getNome());
        holder.idadePet.setText(pets.getIdade() + " anos");
        holder.racaPet.setText(pets.getRaca());

        if (pets.getSexo().equals("femea")) {
            holder.sexoPet.setBackgroundResource(R.mipmap.ic_feminino);
        } else {
            holder.sexoPet.setBackgroundResource(R.mipmap.ic_masculino);
        }

        if (pets.getEspecie().equals("Gato")) {
            holder.especiePet.setBackgroundResource(R.mipmap.ic_gato);
        } else {
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

    }

    @Override
    public int getItemCount() {
        return listaPets.size();
    }

    class PetsViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout petPerfil;
        TextView nomePet, racaPet, idadePet;
        CircleImageView fotoPet, sexoPet, especiePet;
        CircleImageView fotoCirclePet;

        public PetsViewHolder(View itemView) {
            super(itemView);

            petPerfil = (RelativeLayout) itemView.findViewById(R.id.petPerfil);
            nomePet = (TextView) itemView.findViewById(R.id.editNomePetLista);
            racaPet = (TextView) itemView.findViewById(R.id.editRacaPetLista);
            idadePet = (TextView) itemView.findViewById(R.id.editIdadePetLista);
            sexoPet = (CircleImageView) itemView.findViewById(R.id.imageSexo);
            fotoCirclePet = (CircleImageView) itemView.findViewById(R.id.fotoCircleViewPet);
            especiePet = (CircleImageView) itemView.findViewById(R.id.imageEspecie);

        }
    }
}
