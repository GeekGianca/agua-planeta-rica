package com.example.agua_planeta_rica.view.delivery;

import android.content.Intent;
import android.os.Bundle;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.databinding.ActivityDeliverBinding;
import com.example.agua_planeta_rica.view.admin.AdminActivity;
import com.example.agua_planeta_rica.view.delivery.view.SectionsPagerAdapter;
import com.example.agua_planeta_rica.view.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;

public class DeliverActivity extends AppCompatActivity {
    private ActivityDeliverBinding binding;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeliverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.navigation_exit){
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(DeliverActivity.this, LoginActivity.class));
                finishAffinity();
                return true;
            }
            return false;
        });
        /*FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}