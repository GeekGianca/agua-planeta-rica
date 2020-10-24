package com.example.agua_planeta_rica.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agua_planeta_rica.databinding.ActivityLoginBinding;
import com.example.agua_planeta_rica.model.User;
import com.example.agua_planeta_rica.view.admin.AdminActivity;
import com.example.agua_planeta_rica.view.delivery.DeliverActivity;
import com.example.agua_planeta_rica.view.forgot.ForgotActivity;
import com.example.agua_planeta_rica.view.main.MainActivity;
import com.example.agua_planeta_rica.view.register.RegisterActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.agua_planeta_rica.util.Common.CURRENT_USER;
import static com.example.agua_planeta_rica.util.Common.CURRENT_USER_MODEL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUp.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        binding.login.setOnClickListener(view -> {
            binding.loading.setVisibility(View.VISIBLE);
            login();
        });

        binding.forgot.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotActivity.class)));

    }

    private void login() {
        if (!binding.username.getText().toString().isEmpty() && !binding.password.getText().toString().isEmpty()) {
            mAuth.signInWithEmailAndPassword(binding.username.getText().toString(), binding.password.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        binding.loading.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            CURRENT_USER = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar.make(binding.getRoot(), "Falló al intentar autenticar al usuario.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        } else {
            binding.loading.setVisibility(View.GONE);
            Snackbar.make(binding.getRoot(), "Comprueba tu usuario y contraseña e intentalo nuevamente.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("users");
        CURRENT_USER = mAuth.getCurrentUser();
        binding.loading.setVisibility(View.VISIBLE);
        binding.login.setEnabled(false);
        binding.signUp.setEnabled(false);
        updateUI();
    }

    private void updateUI() {
        if (CURRENT_USER != null) {
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        if (user != null && user.getEmail().equals(CURRENT_USER.getEmail())) {
                            CURRENT_USER_MODEL = user;
                            break;
                        }
                    }
                    Log.d(TAG, CURRENT_USER_MODEL.toString());
                    if (CURRENT_USER_MODEL.getRole() == 0) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    } else {
                        if (CURRENT_USER_MODEL.getRole() == 1) {
                            startActivity(new Intent(LoginActivity.this, DeliverActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                    finishAffinity();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i(TAG, "onCancelled", error.toException());
                }
            });
        }
        binding.loading.setVisibility(View.GONE);
        binding.login.setEnabled(true);
        binding.signUp.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}