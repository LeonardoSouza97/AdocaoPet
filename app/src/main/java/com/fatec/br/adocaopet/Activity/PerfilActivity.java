package com.fatec.br.adocaopet.Activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.support.design.widget.TabLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.fatec.br.adocaopet.Common.ViewPageAdapter;
import com.fatec.br.adocaopet.Fragments.FragmentAdocaoConcluida;
import com.fatec.br.adocaopet.Fragments.FragmentMyRequest;
import com.fatec.br.adocaopet.Fragments.FragmentReceiveRequest;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.DAO.FirebaseAuthUtils;
import com.fatec.br.adocaopet.Model.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView editNome, editEmail, editWelcome;
    CircleImageView fotoUsuario;
    CircleImageView fotoTelaUsuario;
    String identificacaoUsuario;
    FirebaseAuth auth;
    TabLayout tableLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        identificacaoUsuario = FirebaseAuthUtils.getUUID();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        editNome = header.findViewById(R.id.txtNomeUsuario);
        editEmail = header.findViewById(R.id.txtEmail);
        fotoUsuario = (CircleImageView) header.findViewById(R.id.imageFotoPerfil);
        fotoTelaUsuario =  (CircleImageView) findViewById(R.id.imageFotoPerfil);
        editWelcome = findViewById(R.id.txtNomeUsuario);

        //IMPLEMENTANDO MENU NOVO

        tableLayout = (TabLayout) findViewById(R.id.table_layout_id);
        viewPager = (ViewPager) findViewById(R.id.view_pager_id);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        adapter.AdicionaFragmentos(new FragmentAdocaoConcluida(), "Adoções Concluídas");
        adapter.AdicionaFragmentos(new FragmentMyRequest(), "");
        adapter.AdicionaFragmentos(new FragmentReceiveRequest(), "");
        viewPager.setAdapter(adapter);
        tableLayout.setupWithViewPager(viewPager);

        tableLayout.getTabAt(1).setIcon(R.drawable.ic_send_black_24dp);
        tableLayout.getTabAt(2).setIcon(R.drawable.ic_notifications_active_black_24dp);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(auth.getCurrentUser().getUid());

        pegarFotoUsuario();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    Usuario usuarioCarregado = dataSnapshot.getValue(Usuario.class);

                    if (usuarioCarregado == null) {
                        editNome.setText(auth.getCurrentUser().getDisplayName());
                        editWelcome.setText(auth.getCurrentUser().getDisplayName());
                        editEmail.setText(auth.getCurrentUser().getEmail());

                        Picasso.get().load(auth.getCurrentUser().getPhotoUrl()).into(fotoUsuario);
                        Picasso.get().load(auth.getCurrentUser().getPhotoUrl()).into(fotoTelaUsuario);

                    } else {
                        editNome.setText(usuarioCarregado.getNome());
                        editWelcome.setText(usuarioCarregado.getNome());
                        editEmail.setText(usuarioCarregado.getEmail());

                        pegarFotoUsuario();
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Erro: " + databaseError.getDetails());
            }

        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_logout) {
            Intent i = new Intent(PerfilActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            LoginManager.getInstance().logOut();
        }

        if (id == R.id.btn_about) {
            new AlertDialog.Builder(this).setTitle("Sobre").
                    setMessage("Aplicativo criado por alunos da Fatec").show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_cadastrar_pet) {
            Intent i = new Intent(PerfilActivity.this, CadastroPetActivity.class);
            startActivity(i);
            onStop();

        } else if (id == R.id.nav_principal) {
            pegarFotoUsuario();
        } else if (id == R.id.nav_meus_pets) {
            Intent i = new Intent(PerfilActivity.this, MeusPetsActivity.class);
            startActivity(i);
            onStop();

        } else if (id == R.id.nav_buscar_pet) {
            Intent i = new Intent(PerfilActivity.this, BuscaPetActivity.class);
            startActivity(i);
            onStop();

        } else if (id == R.id.nav_editar_perfil) {
            Intent i = new Intent(PerfilActivity.this, AlterarUsuarioActivity.class);
            startActivity(i);
            onStop();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void pegarFotoUsuario() {

        try {
            StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();
            final long ONE_MEGABYTE = 1024 * 1024;
            firebaseStorage.child("user/" + auth.getCurrentUser().getUid() + ".png").getBytes(ONE_MEGABYTE)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Picasso.get().load(auth.getCurrentUser().getPhotoUrl()).into(fotoTelaUsuario);
                            Picasso.get().load(auth.getCurrentUser().getPhotoUrl()).into(fotoUsuario);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            fotoUsuario.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(bytes)));
                            fotoTelaUsuario.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(bytes)));
                        }

                    });

        } catch (Exception e) {
            Notify.showNotify(this, e.getMessage());
            System.out.println(e.toString());
        }
    }

}
