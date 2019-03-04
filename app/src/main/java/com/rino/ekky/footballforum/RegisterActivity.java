package com.rino.ekky.footballforum;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    EditText edtEmail, edtUsername, edtPassword, edtKonfirmasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.edt_email);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        edtKonfirmasi = findViewById(R.id.edt_konfirmasi_password);

        auth = FirebaseAuth.getInstance();
    }

    public void registerClick(View view) {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String konf_password = edtKonfirmasi.getText().toString().trim();
        boolean isEmpty = false;

        if (TextUtils.isEmpty(email)) {
            isEmpty = true;
            edtEmail.setError("Field cannot be blank");
        }
        if (TextUtils.isEmpty(password)) {
            isEmpty = true;
            edtPassword.setError("Field cannot be blank");
        }
        if (TextUtils.isEmpty(username)) {
            isEmpty = true;
            edtEmail.setError("Field cannot be blank");
        }
        if (TextUtils.isEmpty(konf_password)) {
            isEmpty = true;
            edtPassword.setError("Field cannot be blank");
        }

        if (!isEmpty) {
            if (edtPassword.getText().toString().equals(edtKonfirmasi.getText().toString())) {
                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    User user = new User(firebaseUser.getUid(), edtEmail.getText().toString(), edtUsername.getText().toString());

                                    Toast.makeText(RegisterActivity.this, "Authentication success.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, ChooseClubActivity.class);
                                    intent.putExtra("user", user);
                                    intent.putExtra("change", false);
                                    startActivity(intent);
                                    finish();
                                    //updateUI(firebaseUser);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, task.getException().toString(),
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
            else {
                Toast.makeText(this, "Password didn't match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void daftarClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
