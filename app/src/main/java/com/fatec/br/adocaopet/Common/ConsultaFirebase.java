//package com.fatec.br.adocaopet.Common;
//
//import android.os.AsyncTask;
//
//import com.fatec.br.adocaopet.Model.Usuario;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class ConsultaFirebase extends AsyncTask {
//
//    private Usuario usuarioCarregado;
//
//    @Override
//    protected Usuario doInBackground(Object[] objects) {
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference dataDono = database.getReference("users").child(idDono);
//
//        dataDono.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                usuarioCarregado = dataSnapshot.getValue(Usuario.class);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("ERRO" + databaseError.toString());
//            }
//
//        });
//
//        return usuarioCarregado;
//    }
//
//
//    @Override
//    protected void onPostExecute(Object o) {
//        super.onPostExecute(o);
//    }
//}
