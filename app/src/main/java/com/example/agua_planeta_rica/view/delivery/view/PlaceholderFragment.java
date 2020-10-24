package com.example.agua_planeta_rica.view.delivery.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.adapter.AdapterAdminRequest;
import com.example.agua_planeta_rica.databinding.FragmentDeliverBinding;
import com.example.agua_planeta_rica.model.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private FragmentDeliverBinding binding;
    private List<Request> requestList;
    private DatabaseReference mReference;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentDeliverBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Request request = postSnapshot.getValue(Request.class);
                            if (request.getState().equals("Solicitado")){
                                requestList.add(request);
                            }
                        }
                        configureAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void configureAdapter() {
        AdapterAdminRequest adapter = new AdapterAdminRequest(requestList, getContext());
        adapter.setDelivery(true);
        binding.requestList.setHasFixedSize(true);
        binding.requestList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.requestList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}