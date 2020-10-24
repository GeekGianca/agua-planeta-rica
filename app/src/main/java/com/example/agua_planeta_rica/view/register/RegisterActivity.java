package com.example.agua_planeta_rica.view.register;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agua_planeta_rica.databinding.ActivityRegisterBinding;
import com.example.agua_planeta_rica.model.User;
import com.example.agua_planeta_rica.util.Common;
import com.example.agua_planeta_rica.view.login.LoginActivity;
import com.example.agua_planeta_rica.view.main.MainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private User mUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.register.setOnClickListener(view -> {
            mUser = new User();
            mUser.setIdentification(binding.identification.getText().toString());
            mUser.setRole(2);
            mUser.setName(binding.name.getText().toString());
            mUser.setDirection(binding.direction.getText().toString());
            mUser.setStreet(binding.street.getText().toString());
            mUser.setDescription(binding.description.getText().toString());
            mUser.setEmail(binding.email.getText().toString());
            mUser.setPassword(binding.password.getText().toString());
            checkEmpty();
        });
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("/users");
    }

    private void checkEmpty() {
        if (mUser.getIdentification().isEmpty()) {
            binding.identification.setError("La identificacion no puede estar vacia.");
            return;
        } else {
            binding.identification.setError(null);
        }
        if (mUser.getName().isEmpty()) {
            binding.name.setError("El nombre no puede estar vacio.");
            return;
        } else {
            binding.name.setError(null);
        }
        if (mUser.getDirection().isEmpty()) {
            binding.direction.setError("La dirección no puede estar vacia.");
            return;
        } else {
            binding.direction.setError(null);
        }
        if (mUser.getStreet().isEmpty()) {
            binding.street.setError("El barrio no puede estar vacio.");
            return;
        } else {
            binding.street.setError(null);
        }
        if (mUser.getEmail().isEmpty()) {
            binding.email.setError("El correo electronico no puede estar vacio.");
            return;
        } else {
            binding.email.setError(null);
        }
        if (mUser.getPassword().isEmpty()) {
            binding.password.setError("La contraseña no puede estar vacia.");
            return;
        } else {
            binding.password.setError(null);
        }
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Espere...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
        register();
    }

    private void register() {
        mAuth.createUserWithEmailAndPassword(mUser.getEmail(), mUser.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        Common.CURRENT_USER = mAuth.getCurrentUser();
                        completeRegistration();
                    } else {
                        mDialog.dismiss();
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Snackbar.make(binding.getRoot(), "Error al intentar registrar el usuario.", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void completeRegistration() {
        mReference.child(mUser.getIdentification()).setValue(mUser);
        mDialog.dismiss();
        new MaterialAlertDialogBuilder(this)
                .setTitle("Registro completo!")
                .setMessage("Usuario registrado exitosamente.\n¿Desea acceder inmediatamente?")
                .setPositiveButton("Aceptar", (dialogInterface, i) -> updateUI())
                .setNegativeButton("Cancelar", (dialogInterface, i) -> logout())
                .create()
                .show();
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void updateUI() {
        if (Common.CURRENT_USER != null) {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
    }

    public void backActivity(View view) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }
}