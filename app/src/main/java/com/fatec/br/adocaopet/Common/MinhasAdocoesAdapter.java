package com.fatec.br.adocaopet.Common;


import android.app.Dialog;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatec.br.adocaopet.Model.Adocoes;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MinhasAdocoesAdapter extends RecyclerView.Adapter<MinhasAdocoesAdapter.AdocoesViewHolder>{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    private List<Adocoes> listaAdocoes;
    private Adocoes adocoes;
    private Dialog dialog;
    private TextView nomeUsuarioVisualiza, dataUsuarioVisualiza, enderecoUsuarioVisualiza, telefoneUsuarioVisualiza, emailUsuarioVisualiza;
    private CircleImageView fotoUsuarioVisualiza;

    public MinhasAdocoesAdapter(List<Adocoes> listaAdocoes) {
        this.listaAdocoes = listaAdocoes;
    }

    @Override
    public MinhasAdocoesAdapter.AdocoesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        dialog = new Dialog(parent.getContext());
        dialog.setContentView(R.layout.view_informacao);

        nomeUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_name_usuario);
        dataUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_data_usuario);
        enderecoUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_endereco_usuario);
        emailUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_email_usuario);
        telefoneUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_telefone_usuario);
        fotoUsuarioVisualiza = (CircleImageView) dialog.findViewById(R.id.dialog_usuario_foto);

        return new MinhasAdocoesAdapter.AdocoesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_minhas_adocoes, parent, false));
    }

    @Override
    public void onBindViewHolder(final MinhasAdocoesAdapter.AdocoesViewHolder holder, int position) {
        adocoes = listaAdocoes.get(position);

        holder.nomeAdotante.setText(adocoes.getSolicitante().getNome());
        holder.nomePet.setText(adocoes.getPet().getNome());
        holder.dataAdocao.setText(adocoes.getDataAdocao());

        if(adocoes.getStatus().equals(adocoes.getIdDono() + "Pendente")){
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_pendente);
        }else if (adocoes.getStatus().equals(adocoes.getIdDono() + "Recusado")){
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_recusado);
        }else{
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_aprovado);
            holder.imageCall.setVisibility(View.VISIBLE);
        }

        holder.imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nomeUsuarioVisualiza.setText("Nome: " + listaAdocoes.get(holder.getAdapterPosition()).getDono().getNome());
                dataUsuarioVisualiza.setText("Data de Nascimento: " + listaAdocoes.get(holder.getAdapterPosition()).getDono().getDataNasc());
                telefoneUsuarioVisualiza.setText("Telefone: " + listaAdocoes.get(holder.getAdapterPosition()).getDono().getTelefone());
                emailUsuarioVisualiza.setText("Email: " + listaAdocoes.get(holder.getAdapterPosition()).getDono().getEmail());
                enderecoUsuarioVisualiza.setText("Endereço: " + listaAdocoes.get(holder.getAdapterPosition()).getDono().getEndereco());

                final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                try {
                    StorageReference url_pet = firebaseStorage.child("pet/" + (listaAdocoes.get(holder.getAdapterPosition()).getIdDono() + ".png"));

                    url_pet.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            Picasso.get().load(downloadUrl.toString()).into(fotoUsuarioVisualiza);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.show();
            }
        });


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

    }

    @Override
    public int getItemCount() {
        return listaAdocoes.size();
    }

    class AdocoesViewHolder extends RecyclerView.ViewHolder {

        TextView nomePet, nomeAdotante, dataAdocao;
        CircleImageView fotoCirclePet;
        ImageView imageStatus;
        ImageButton imageCall;

        public AdocoesViewHolder(View itemView) {
            super(itemView);

            nomePet = (TextView) itemView.findViewById(R.id.editNomePetAdocao);
            nomeAdotante = (TextView) itemView.findViewById(R.id.editNomeAdotante);
            dataAdocao = (TextView) itemView.findViewById(R.id.editDataAdocao);
            fotoCirclePet = (CircleImageView) itemView.findViewById(R.id.fotoCircleViewPetAdocaoTela);
            imageStatus = (ImageView) itemView.findViewById(R.id.imageStatus);
            imageCall = (ImageButton) itemView.findViewById(R.id.imageCall);

        }
    }
}
