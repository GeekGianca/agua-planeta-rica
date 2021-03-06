package com.example.agua_planeta_rica.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.adapter.AdapterRequest;
import com.example.agua_planeta_rica.databinding.FragmentMainBinding;
import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.util.Common;
import com.example.agua_planeta_rica.view.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.agua_planeta_rica.util.Common.CURRENT_USER;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final String TAG = "PlaceholderFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private FragmentMainBinding binding;
    private PageViewModel pageViewModel;
    private List<Request> requestList = new ArrayList<>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceRequest;
    private AdapterRequest adapterRequest;
    private int index;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        if (index == 1) {
            pageViewModel.setIndex("Sin contenido de compras.");
        } else {
            pageViewModel.setIndex("Sin contenido de pedidos.");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater);
        View root = binding.getRoot();
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Common.INSTANCE = this;
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceRequest = mDatabase.getReference("requests");
        mReferenceRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Request req = postSnapshot.getValue(Request.class);
                    if (req != null && req.getUsername().equals(CURRENT_USER.getUid())) {
                        if (req.getState().equals("Solicitado") || req.getState().equals("Cancelado")) {
                            requestList.add(req);
                        }
                    }
                }
                placeToAdapter(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void placeToAdapter(int pos) {
        index = pos;
        if (!requestList.isEmpty()) {
            adapterRequest = new AdapterRequest(getContext(), requestList, 1);
            binding.contentList.setHasFixedSize(true);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            binding.contentList.setLayoutManager(manager);
            binding.contentList.setAdapter(adapterRequest);
            adapterRequest.notifyDataSetChanged();
            binding.sectionLabel.setVisibility(View.GONE);
        } else {
            binding.sectionLabel.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Modificar")) {
            modify(requestList.get(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void modify(Request request) {
        ((MainActivity) getContext()).displayRequestModify(request);
    }
}