package com.example.agua_planeta_rica.view.delivery.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.adapter.AdapterAdminRequest;
import com.example.agua_planeta_rica.databinding.FragmentFinanceBinding;
import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.util.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FinanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinanceFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private List<Request> requestList;
    private DatabaseReference mReference;
    private FragmentFinanceBinding binding;
    private int totalRequest = 0;
    private double total = 0;

    public static FinanceFragment newInstance(int index) {
        FinanceFragment fragment = new FinanceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFinanceBinding.inflate(inflater);
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
                            if (request.getState().equals("Entregado")) {
                                requestList.add(request);
                                total += request.getTotalPrice();
                                totalRequest += request.getQuantity();
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
        binding.listRequest.setHasFixedSize(true);
        binding.listRequest.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.listRequest.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        binding.total.setText(String.format("Total ventas: $%s", Common.DF.format(total)));
        binding.totalRequest.setText(String.format("Total productos vendidos: %s", totalRequest));
    }
}