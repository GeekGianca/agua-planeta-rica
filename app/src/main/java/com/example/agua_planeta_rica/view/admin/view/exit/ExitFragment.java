package com.example.agua_planeta_rica.view.admin.view.exit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.view.admin.AdminActivity;
import com.example.agua_planeta_rica.view.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ExitFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        getContext().startActivity(new Intent(getContext(), LoginActivity.class));
        ((AdminActivity) getContext()).finishAffinity();
        return inflater.inflate(R.layout.fragment_exit, container, false);
    }
}