package com.fatec.br.adocaopet.Common;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Model.Adocoes;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MinhasAdocoesAdapter extends RecyclerView.Adapter<MinhasAdocoesAdapter.AdocoesViewHolder> {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    private List<Adocoes> listaAdocoes;
    private Adocoes adocoes;
    private Dialog dialog;
    private TextView nomeUsuarioVisualiza, dataUsuarioVisualiza, enderecoUsuarioVisualiza, telefoneUsuarioVisualiza, emailUsuarioVisualiza;
    private CircleImageView fotoUsuarioVisualiza;
    private Button btnLigar, btnEnviaEmail;
    private Context context;

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
        btnLigar = (Button) dialog.findViewById(R.id.btnLigar);
        btnEnviaEmail = (Button) dialog.findViewById(R.id.btnEnviarEmailDono);

        return new MinhasAdocoesAdapter.AdocoesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_minhas_adocoes, parent, false));
    }

    @Override
    public void onBindViewHolder(final MinhasAdocoesAdapter.AdocoesViewHolder holder, int position) {
        adocoes = listaAdocoes.get(position);

        holder.nomeAdotante.setText(adocoes.getSolicitante().getNome());
        holder.nomePet.setText(adocoes.getPet().getNome());
        holder.dataAdocao.setText(adocoes.getDataAdocao());

        if (adocoes.getStatus().equals(adocoes.getIdDono() + "Pendente")) {
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_pendente);
        } else if (adocoes.getStatus().equals(adocoes.getIdDono() + "Recusado")) {
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_recusado);
        } else {
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_aprovado);
            holder.imageCall.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
                menu.add(holder.getAdapterPosition(), 0, 0, "Remover Adoção");
            }
        });

        //REALIZANDO ADOÇÃO
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
                    StorageReference url_pet = firebaseStorage.child("user/" + (listaAdocoes.get(holder.getAdapterPosition()).getIdDono() + ".png"));

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

                btnLigar.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("Range")
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_CALL);
                        i.setData(Uri.parse("tel:" + pegaTelefone(listaAdocoes.get(holder.getAdapterPosition()).getDono().getTelefone())));
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},123);
                        }else{
                            context.startActivity(i);
                        }
                    }
                });

                btnEnviaEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enviarEmail(listaAdocoes.get(holder.getAdapterPosition()).getDono().getEmail());
                    }
                });

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

            context = itemView.getContext();
            nomePet = (TextView) itemView.findViewById(R.id.editNomePetAdocao);
            nomeAdotante = (TextView) itemView.findViewById(R.id.editNomeAdotante);
            dataAdocao = (TextView) itemView.findViewById(R.id.editDataAdocao);
            fotoCirclePet = (CircleImageView) itemView.findViewById(R.id.fotoCircleViewPetAdocaoTela);
            imageStatus = (ImageView) itemView.findViewById(R.id.imageStatus);
            imageCall = (ImageButton) itemView.findViewById(R.id.imageCall);

        }
    }

    public String pegaTelefone(String telefone){

        telefone = telefone.replaceAll("[^\\d.]", "");
        return telefone.substring(2);
    }

    public void enviarEmail(String email)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { email });
        intent.putExtra(Intent.EXTRA_SUBJECT, "Tenho interesse em seu pet! ");
        context.startActivity(Intent.createChooser(intent, "Enviar email para o dono"));

    }

}
