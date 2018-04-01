package com.fatec.br.adocaopet.Activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fatec.br.adocaopet.Common.Internet;
import com.fatec.br.adocaopet.Common.Notify;
import com.fatec.br.adocaopet.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText emailView;
    private EditText passwordView;
    private View mProgressView;
    private View mLoginFormView;

    public FirebaseAuth mAuth;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.emailView = findViewById(R.id.email);
        this.passwordView = findViewById(R.id.password);

        Button signInButton = findViewById(R.id.email_sign_in_button);
        Button registerButton = findViewById(R.id.regiter_button);
        Button esqueceuButton = findViewById(R.id.esqueceu_senha_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    tryLogin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
                startActivity(intent);
                finish();
            }
        });

        esqueceuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RedefinicaoSenhaActivity.class);
                startActivity(intent);
                finish();
            }
        });


        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void tryLogin() throws InterruptedException {
        if (mAuthTask != null) {
            return;
        }

        emailView.setError(null);
        passwordView.setError(null);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            emailView.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            passwordView.requestFocus();
        } else {
            if (!Internet.isNetworkAvailable(this)) {
                Notify.showNotify(this, getString(R.string.error_not_connected));
            } else {
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String email;
        private final String password;

        UserLoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                FirebaseAuth authentication = FirebaseAuth.getInstance();

                Task<AuthResult> authenticationResult = authentication.signInWithEmailAndPassword(this.email, this.password);

                while (!authenticationResult.isComplete()) {
                    Thread.sleep(1000);
                }

                return authenticationResult.isSuccessful();
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Notify.showNotify(LoginActivity.this, getString(R.string.message_login_successful));

                Intent intentMap = new Intent(LoginActivity.this, PerfilActivity.class);
                startActivity(intentMap);
                finish();
            } else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}