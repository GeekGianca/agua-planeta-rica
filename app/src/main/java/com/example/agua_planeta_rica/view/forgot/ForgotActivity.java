package com.example.agua_planeta_rica.view.forgot;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agua_planeta_rica.databinding.ActivityForgotBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {
    private ActivityForgotBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        binding.login.setOnClickListener(v -> {
            if (binding.username.getText().toString().isEmpty()) {
                binding.username.setError("Campo vacio.");
                return;
            } else {
                binding.username.setError(null);
            }
            mAuth.sendPasswordResetEmail(binding.username.getText().toString())
                    .addOnSuccessListener(aVoid -> new MaterialAlertDialogBuilder(ForgotActivity.this)
                            .setTitle("Restaurar contraseña")
                            .setMessage("La restauracion de contraseña ha sido enviada a su correo.")
                            .setPositiveButton("Aceptar", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }))
                    .addOnFailureListener(e -> new MaterialAlertDialogBuilder(ForgotActivity.this)
                            .setTitle("Error al restaurar")
                            .setMessage("Error al restaurar contraseña: " + e.getMessage())
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }));
        });
    }
}