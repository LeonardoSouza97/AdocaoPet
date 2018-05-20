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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Activity.AdocaoActivity;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.Model.Adocoes;
import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Leo on 15/04/2018.
 */

public class PetAdapterBusca extends RecyclerView.Adapter<PetAdapterBusca.PetsViewHolder> {

    private List<Pet> listaPets;
    private String idPet, idAdocao, idAdotante, dataAdocao, idDono, status;
    private TextView nomePetTelaVisualiza, racaPetTelaVisualiza, idadePetTelaVisualiza;
    private Button btnAdotar;
    Dialog dialog;
    AdocaoActivity adocao;
    FirebaseAuth auth;


    public PetAdapterBusca(List<Pet> listaPets) {
        this.listaPets = listaPets;
    }


    @Override
    public PetsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pets, parent, false);
        final PetsViewHolder viewHolder = new PetsViewHolder(view);

        //Dados exibidos quando o usuário clica em algum pet na tela de Busca.
        dialog = new Dialog(parent.getContext());
        dialog.setContentView(R.layout.activity_visualizapet);

        viewHolder.petPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nomePetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_name_pet);
                racaPetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_raca_pet);
                idadePetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_idade_pet);
                btnAdotar = (Button) dialog.findViewById(R.id.btnAdotar);
                final CircleImageView fotoPetTelaVisualiza = (CircleImageView) dialog.findViewById(R.id.dialog_pet_foto);

                nomePetTelaVisualiza.setText(listaPets.get(viewHolder.getAdapterPosition()).getNome());
                racaPetTelaVisualiza.setText(listaPets.get(viewHolder.getAdapterPosition()).getRaca());
                idadePetTelaVisualiza.setText(listaPets.get(viewHolder.getAdapterPosition()).getIdade());

                final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

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

                btnAdotar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Adocoes adocoes = new Adocoes();

                        Date data = new Date(System.currentTimeMillis());

                        Format formatter = new SimpleDateFormat("dd/MM/yy");
                        String dataFormatada = formatter.format(data);

                        adocoes.setId_dono(listaPets.get(viewHolder.getAdapterPosition()).getIdUsuario());
                        adocoes.setId_pet(listaPets.get(viewHolder.getAdapterPosition()).getIdPet());
                        adocoes.setId_adotante(FirebaseAuthUtils.getUUID());
                        adocoes.setStatus("Pendente");
                        adocoes.setData_adocao(dataFormatada);
                        adocoes.setId_adocao(adocoes.getId_dono() + adocoes.getId_pet() + adocoes.getId_dono());

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dataAdocao = database.getReference("adoções").child(adocoes.getId_adocao());

                        dataAdocao.child("idAdocao").setValue(adocoes.getId_adocao());
                        dataAdocao.child("idDono").setValue(adocoes.getId_dono());
                        dataAdocao.child("idAdotante").setValue(adocoes.getId_adotante());
                        dataAdocao.child("dataAdocao").setValue(adocoes.getData_adocao());
                        dataAdocao.child("status").setValue(adocoes.getStatus());
                        dataAdocao.child("idPet").setValue(adocoes.getId_pet());

                        Toast.makeText(parent.getContext(), "Solicitação de Adoção Enviada!", Toast.LENGTH_SHORT).show();

//                        adocao.realizaAdocao(idAdocao, idDono, idAdotante, status, dataAdocao, idPet);
                    }
                });


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
