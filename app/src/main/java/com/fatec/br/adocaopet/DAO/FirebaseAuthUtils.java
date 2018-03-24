package com.fatec.br.adocaopet.DAO;

import com.fatec.br.adocaopet.Utils.Base64Custom;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Leo on 19/03/2018.
 */

public class FirebaseAuthUtils {

        public static com.google.firebase.auth.FirebaseAuth authentication = com.google.firebase.auth.FirebaseAuth.getInstance();

        public static String getUUID() {
            FirebaseUser user = getCurrentFirebaseUser();

            return user != null ? Base64Custom.codificarBase64(user.getEmail()) : null;
        }

        private static FirebaseUser getCurrentFirebaseUser() {
            return authentication.getCurrentUser();
        }

        public static com.google.firebase.auth.FirebaseAuth getInstance() {
            return authentication;
        }

    }


