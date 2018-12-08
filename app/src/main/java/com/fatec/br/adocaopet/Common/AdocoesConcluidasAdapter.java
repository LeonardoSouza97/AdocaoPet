package com.fatec.br.adocaopet.Common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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

public class AdocoesConcluidasAdapter extends RecyclerView.Adapter<AdocoesConcluidasAdapter.AdocoesViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    private List<Adocoes> listaAdocoes;
    private Adocoes adocoes;
    private Dialog dialog;
    private TextView nomeUsuarioVisualiza, dataUsuarioVisualiza, enderecoUsuarioVisualiza, telefoneUsuarioVisualiza, emailUsuarioVisualiza;
    private CircleImageView fotoUsuarioVisualiza;
    private Button btnLigar, btnEnviaEmail;
    private Context context;
    private DisparaEmail disparaEmail;

    public AdocoesConcluidasAdapter(List<Adocoes> listaAdocoes) {
        this.listaAdocoes = listaAdocoes;
    }

    @Override
    public AdocoesConcluidasAdapter.AdocoesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        dialog = new Dialog(parent.getContext());
        dialog.setContentView(R.layout.view_informacao);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        nomeUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_name_usuario);
        dataUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_data_usuario);
        enderecoUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_endereco_usuario);
        emailUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_email_usuario);
        telefoneUsuarioVisualiza = (TextView) dialog.findViewById(R.id.dialog_telefone_usuario);
        fotoUsuarioVisualiza = (CircleImageView) dialog.findViewById(R.id.dialog_usuario_foto);
        btnLigar = (Button) dialog.findViewById(R.id.btnLigar);
        btnEnviaEmail = (Button) dialog.findViewById(R.id.btnEnviarEmailDono);

        return new AdocoesConcluidasAdapter.AdocoesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adocoes_concluidas, parent, false));
    }

    @Override
    public void onBindViewHolder(final AdocoesConcluidasAdapter.AdocoesViewHolder holder, int position) {
        adocoes = listaAdocoes.get(position);

        holder.nomeAdotante.setText(adocoes.getSolicitante().getNome());
        holder.nomePet.setText(adocoes.getPet().getNome());
        holder.dataAdocao.setText(adocoes.getDataAdocao());

        if (adocoes.getStatus().equals(adocoes.getIdDono() + "Pendente")) {
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_pendente_novo);
        } else if (adocoes.getStatus().equals(adocoes.getIdDono() + "Recusado")) {
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_recusado_novo);
        } else {
            holder.imageStatus.setBackgroundResource(R.mipmap.ic_aprovado_novo);
            holder.imageCall.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
                menu.add(holder.getAdapterPosition(), 0, 0, "Finalizar Adoção");
            }
        });

        //REALIZANDO ADOÇÃO
        holder.imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nomeUsuarioVisualiza.setText("Nome: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getNome());
                dataUsuarioVisualiza.setText("Data de Nascimento: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getDataNasc());
                telefoneUsuarioVisualiza.setText("Telefone: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getTelefone());
                emailUsuarioVisualiza.setText("Email: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getEmail());
                enderecoUsuarioVisualiza.setText("Endereço: " + listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getEndereco());

                final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

                try {
                    StorageReference url_pet = firebaseStorage.child("user/" + (listaAdocoes.get(holder.getAdapterPosition()).getIdAdotante() + ".png"));

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
                        i.setData(Uri.parse("tel:" + pegaTelefone(listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getTelefone())));
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 123);
                        } else {
                            context.startActivity(i);
                        }
                    }
                });

                btnEnviaEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Enviando email..");
                        progressDialog.show();

                        Thread enviaEmail = new Thread() {
                            public void run() {

                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                String nomeDono = listaAdocoes.get(holder.getAdapterPosition()).getDono().getNome();
                                String nomePet = listaAdocoes.get(holder.getAdapterPosition()).getPet().getNome();
                                String emailSolicitante = listaAdocoes.get(holder.getAdapterPosition()).getSolicitante().getEmail();
                                String telefone = listaAdocoes.get(holder.getAdapterPosition()).getDono().getTelefone();

                                disparaEmail = new DisparaEmail();
                                disparaEmail.enviar("Parabéns pela adoção! ", "Olá sou(a) " + nomeDono + "! \n" +
                                        "\n" + "Vi que se interessou no(a)" + nomePet + ". Gostaria de marcar um lugar para se encontrar?\n" +
                                        "\n" +
                                        "Pode me chamar nesse número:\n" +
                                        telefone, emailSolicitante);
                                progressDialog.dismiss();
                            }
                        };

                        enviaEmail.start();

                        Toast.makeText(context, "Email enviado com sucesso!", Toast.LENGTH_SHORT).show();

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
        CircleImageView imageCall;

        public AdocoesViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            nomePet = (TextView) itemView.findViewById(R.id.editNomePetAdocao);
            nomeAdotante = (TextView) itemView.findViewById(R.id.editNomeAdotante);
            dataAdocao = (TextView) itemView.findViewById(R.id.editDataAdocao);
            fotoCirclePet = (CircleImageView) itemView.findViewById(R.id.fotoCircleViewPetAdocaoTela);
            imageStatus = (ImageView) itemView.findViewById(R.id.imageStatus);
            imageCall = (CircleImageView) itemView.findViewById(R.id.imageCall);

        }
    }

    public String pegaTelefone(String telefone) {

        telefone = telefone.replaceAll("[^\\d.]", "");
        return telefone.substring(2);
    }

//    public void enviarEmail(String email, String pet, String dono, String telefone)
//    {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/html");
//        intent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { email });
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Parabéns pela adoção!");
//        intent.putExtra(Intent.EXTRA_TEXT, "Olá sou(a) "+ dono +"! \n" +
//                "\n" + "Vi que se interessou no(a)" + pet + ". Gostaria de marcar um lugar para se encontrar?\n" +
//                "\n" +
//                "Pode me chamar nesse número:\n" +
//                telefone);
//        context.startActivity(Intent.createChooser(intent, "Enviar email para o solicitante"));
//
//    }
}
