package com.fatec.br.adocaopet.Common;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fatec.br.adocaopet.Model.Adocoes;
import com.fatec.br.adocaopet.Model.Pet;
import com.fatec.br.adocaopet.Model.Usuario;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class PetAdapterBusca extends RecyclerView.Adapter<PetAdapterBusca.PetsViewHolder> {

    private List<Pet> listaPets;
    private TextView nomePetTelaVisualiza, racaPetTelaVisualiza, idadePetTelaVisualiza, pesoPetTelaVisualiza, descricaoPetTelaVisualiza, portePetTelaVisualiza, vacinadoPetTelaVisualiza, vermifugadoPetTelaVisualiza;
    private Button btnAdotar;
    Dialog dialog;
    Usuario donoCarregado, adotanteCarregado;
    Pet petCarregado;
    private FirebaseAuth auth;
    private Adocoes adocoes;

    public PetAdapterBusca(List<Pet> listaPets) {
        this.listaPets = listaPets;
    }


    @Override
    public PetsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View view;
        auth = FirebaseAuth.getInstance();
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
                pesoPetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_peso_pet);
                portePetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_porte_pet);
                descricaoPetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_descricao_pet);
                vacinadoPetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_vermifugado_pet);
                vermifugadoPetTelaVisualiza = (TextView) dialog.findViewById(R.id.dialog_vacionado_pet);
                btnAdotar = (Button) dialog.findViewById(R.id.btnAdotar);
                final CircleImageView fotoPetTelaVisualiza = (CircleImageView) dialog.findViewById(R.id.dialog_pet_foto);

                nomePetTelaVisualiza.setText("Nome: " + listaPets.get(viewHolder.getAdapterPosition()).getNome());
                racaPetTelaVisualiza.setText("Raça: " + listaPets.get(viewHolder.getAdapterPosition()).getRaca());
                idadePetTelaVisualiza.setText("Idade: " + listaPets.get(viewHolder.getAdapterPosition()).getIdade() + "anos");
                pesoPetTelaVisualiza.setText("Peso: " + listaPets.get(viewHolder.getAdapterPosition()).getPeso() + "kg");
                vacinadoPetTelaVisualiza.setText("Vacinado: " + listaPets.get(viewHolder.getAdapterPosition()).getVacinado());
                vermifugadoPetTelaVisualiza.setText("Vermifugado: " + listaPets.get(viewHolder.getAdapterPosition()).getVermifugado());
                portePetTelaVisualiza.setText("Porte: " + listaPets.get(viewHolder.getAdapterPosition()).getPorte());
                descricaoPetTelaVisualiza.setText("Descrição: " + listaPets.get(viewHolder.getAdapterPosition()).getDescricao());

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

                        final ProgressDialog progressDialog = new ProgressDialog(parent.getContext());
                        progressDialog.setMessage("Realizando solicitação..");
                        progressDialog.show();

                        Thread Adocao = new Thread() {
                            public void run() {
                                adocoes = new Adocoes();

                                donoCarregado = ConsultaDono(listaPets.get(viewHolder.getAdapterPosition()).getIdUsuario());
                                adotanteCarregado = ConsultaSolicitante(auth.getCurrentUser().getUid());
                                petCarregado = ConsultaPet(listaPets.get(viewHolder.getAdapterPosition()).getIdPet());

                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                ((Activity) parent.getContext()).runOnUiThread(new Runnable() {
                                    public void run() {

                                        Date data = new Date(System.currentTimeMillis());

                                        Format formatter = new SimpleDateFormat("dd/MM/yy");
                                        String dataFormatada = formatter.format(data);

                                        adocoes.setIdDono(listaPets.get(viewHolder.getAdapterPosition()).getIdUsuario());
                                        adocoes.setIdPet(listaPets.get(viewHolder.getAdapterPosition()).getIdPet());
                                        adocoes.setIdAdotante(auth.getCurrentUser().getUid());
                                        adocoes.setDataAdocao(dataFormatada);
                                        adocoes.setIdAdocao(adocoes.getIdDono() + adocoes.getIdPet() + adocoes.getIdAdotante());
                                        adocoes.setSolicitante(adotanteCarregado);
                                        adocoes.setStatus(adocoes.getIdDono() + "Pendente");
                                        adocoes.setPet(petCarregado);
                                        adocoes.setDono(donoCarregado);

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference dataAdocao = database.getReference("adoções").child(adocoes.getIdAdocao());

                                        dataAdocao.child("idAdocao").setValue(adocoes.getIdAdocao());
                                        dataAdocao.child("idDono").setValue(adocoes.getIdDono());
                                        dataAdocao.child("idAdotante").setValue(adocoes.getIdAdotante());
                                        dataAdocao.child("dataAdocao").setValue(adocoes.getDataAdocao());
                                        dataAdocao.child("status").setValue(adocoes.getStatus());
                                        dataAdocao.child("idPet").setValue(adocoes.getIdPet());
                                        dataAdocao.child("solicitante").setValue(adocoes.getSolicitante());
                                        dataAdocao.child("pet").setValue(adocoes.getPet());
                                        dataAdocao.child("dono").setValue(adocoes.getDono());

                                        progressDialog.dismiss();

                                        Toast.makeText(parent.getContext(), "Adoção realizada com sucesso!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        };
                        Adocao.start();

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
        CircleImageView sexoPet, especiePet;
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

    private Usuario ConsultaSolicitante(String idDono) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataDono = database.getReference("users").child(idDono);

        dataDono.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                adotanteCarregado = dataSnapshot.getValue(Usuario.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("ERRO" + databaseError.toString());
            }

        });

        return adotanteCarregado;
    }

    private Pet ConsultaPet(String idPet) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataPet = database.getReference("pets").child(idPet);

        dataPet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                petCarregado = dataSnapshot.getValue(Pet.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("ERRO" + databaseError.toString());
            }

        });

        return petCarregado;
    }

    private Usuario ConsultaDono(String idDono) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataDono = database.getReference("users").child(idDono);

        dataDono.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                donoCarregado = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                System.out.println("ERRO" + databaseError.toString());
            }
        });
        return donoCarregado;
    }


}
