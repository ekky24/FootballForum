package com.rino.ekky.footballforum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    EditText edtEmail, edtPassword;
    SharedPreferences preferences;
    boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        preferences = getSharedPreferences("football_forum", Context.MODE_PRIVATE);

        if (!preferences.getBoolean("first_time", false)) {
            MatchReceiver receiver = new MatchReceiver();
            receiver.setReleaseTodayAlarm(this);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("first_time", true);
            editor.apply();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    public void loginClick(View view) {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        boolean isEmpty = false;

        if (TextUtils.isEmpty(email)) {
            isEmpty = true;
            edtEmail.setError("Field cannot be blank");
        }
        if (TextUtils.isEmpty(password)) {
            isEmpty = true;
            edtPassword.setError("Field cannot be blank");
        }

        if (!isEmpty) {
            auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = auth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    public void daftarClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Intent intent = new Intent(this, JadwalDrawerActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
