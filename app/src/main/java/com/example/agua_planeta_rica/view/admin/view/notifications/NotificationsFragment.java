package com.example.agua_planeta_rica.view.admin.view.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.adapter.AdapterUser;
import com.example.agua_planeta_rica.databinding.FragmentNotificationsBinding;
import com.example.agua_planeta_rica.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private List<User> userList;
    private DatabaseReference mReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            User user = postSnapshot.getValue(User.class);
                            userList.add(user);
                        }
                        configureAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void configureAdapter() {
        AdapterUser adapter = new AdapterUser(userList, getContext());
        binding.listUsers.setHasFixedSize(true);
        binding.listUsers.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.listUsers.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}