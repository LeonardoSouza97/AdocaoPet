package com.fatec.br.adocaopet.Common;

import android.app.Dialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdocoesAdapter extends RecyclerView.Adapter<AdocoesAdapter.AdocoesViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    Dialog dialog;
    private List<Adocoes> listaAdocoes;
    private TextView nomeUsuarioVisualiza, enderecoUsuarioVisualiza, telefoneUsuarioVisualiza, emailUsuarioVisualiza, dataUsuarioVisualiza;
    private TextView nomePetVisualiza, racaPetVisualiza, idadePetVisualiza, sexoPetVisualiza, pesoPetVisualiza, portePetVisualiza;
    private CircleImageView fotoUsuarioVisualiza, fotoPetVisualiza;
    private Adocoes adocoes;

    public AdocoesAdapter(List<Adocoes> listaAdocoes) {
        this.listaAdocoes = listaAdocoes;
    }

    @Override
    public AdocoesAdapter.AdocoesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        dialog = new Dialog(parent.getContext());
        dialog.setContentView(R.layout.view_detalhe_adocao);

        nomeUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_name_usuario_adocao);
        enderecoUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_endereco_usuario_adocao);
        telefoneUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_telefone_usuario_adocao);
        emailUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_email_usuario_adocao);
        dataUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_data_usuario_adocao);

        nomePetVisualiza = (TextView) dialog.findViewById(R.id.dialog_name_pet_adocao);
        racaPetVisualiza = (TextView) dialog.findViewById(R.id.dialog_raca_pet_adocao);
        idadePetVisualiza = (TextView) dialog.findViewById(R.id.dialog_idade_pet_adocao);
        sexoPetVisualiza = (TextView) dialog.findViewById(R.id.dialog_sexo_pet_adocao);
        pesoPetVisualiza = (TextView) dialog.findViewById(R.id.dialog_peso_pet_adocao);
        portePetVisualiza = (TextView) dialog.findViewById(R.id.dialog_porte_pet_adocao);

        fotoUsuarioVisualiza = (CircleImageView) dialog.findViewById(R.id.dialog_usuario_foto_adocao);
        fotoPetVisualiza = (CircleImageView) dialog.findViewById(R.id.dialog_pet_foto_adocao);

        return new AdocoesAdapter.AdocoesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adocao, parent, false));
    }

    @Override
    public void onBindViewHolder(final AdocoesAdapter.AdocoesViewHolder holder, int position) {

        adocoes = listaAdocoes.get(position);

        holder.nomeAdotante.setText(adocoes.getSolicitante().getNome());
        holder.nomePet.setText(adocoes.getPet().getNome());
        holder.dataAdocao.setText(adocoes.getDataAdocao());

        ref = database.getReference("adoções").child(adocoes.getIdAdocao());

        final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

        try {
            StorageReference url_pet = firebaseStorage.child("pet/" + adocoes.getPet().getIdPet() + ".png");

            url_pet.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUrl) {
                    Picasso.get().load(downloadUrl.toString()).into(holder.fotoCirclePet);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btnAceitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child("status").setValue(adocoes.getIdDono() + "Confirmado");
            }
        });

        holder.btnRecusar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child("status").setValue(adocoes.getIdDono() + "Recusado");
            }
        });

        holder.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nomeUsuarioVisualiza.setText(listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getNome());
                dataUsuarioVisualiza.setText("Data de Nascimento: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getDataNasc());
                telefoneUsuarioVisualiza.setText("Telefone: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getTelefone());
                emailUsuarioVisualiza.setText("Email: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getEmail());
                enderecoUsuarioVisualiza.setText("Endereço: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getEndereco());

                nomePetVisualiza.setText(listaAdocoes.get(holder.getAdapterPosition()).getPet().getNome());
                racaPetVisualiza.setText("Raça: " + listaAdocoes.get(holder.getAdapterPosition()).getPet().getRaca());
                idadePetVisualiza.setText("Idade: " + listaAdocoes.get(holder.getAdapterPosition()).getPet().getIdade());
                sexoPetVisualiza.setText("Sexo: " + listaAdocoes.get(holder.getAdapterPosition()).getPet().getSexo());
                pesoPetVisualiza.setText("Peso: " + listaAdocoes.get(holder.getAdapterPosition()).getPet().getPeso());
                portePetVisualiza.setText("Porte: " + listaAdocoes.get(holder.getAdapterPosition()).getPet().getPorte());

                final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                try {
                    StorageReference url_solicitante = firebaseStorage.child("user/" + (listaAdocoes.get(holder.getAdapterPosition()).getIdAdotante() + ".png"));

                    url_solicitante.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            Picasso.get().load(downloadUrl.toString()).into(fotoUsuarioVisualiza);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    StorageReference url_pet = firebaseStorage.child("pet/" + (listaAdocoes.get(holder.getAdapterPosition()).getIdPet() + ".png"));

                    url_pet.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            Picasso.get().load(downloadUrl.toString()).into(fotoPetVisualiza);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaAdocoes.size();
    }

    class AdocoesViewHolder extends RecyclerView.ViewHolder {

        TextView nomePet, nomeAdotante, dataAdocao;
        CircleImageView fotoCirclePet;
        ImageButton btnAceitar, btnRecusar, btnInfo;

        public AdocoesViewHolder(View itemView) {
            super(itemView);

            nomePet = (TextView) itemView.findViewById(R.id.editNomePetAdocao);
            nomeAdotante = (TextView) itemView.findViewById(R.id.editNomeAdotante);
            dataAdocao = (TextView) itemView.findViewById(R.id.editDataAdocao);
            fotoCirclePet = (CircleImageView) itemView.findViewById(R.id.fotoCircleViewPetAdocaoTela);
            btnAceitar = (ImageButton) itemView.findViewById(R.id.btnAceitar);
            btnRecusar = (ImageButton) itemView.findViewById(R.id.btnRecusar);
            btnInfo = (ImageButton) itemView.findViewById(R.id.btnInfo);


        }
    }
}
