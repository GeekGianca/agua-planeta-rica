package com.example.agua_planeta_rica.view.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.adapter.AdapterRequest;
import com.example.agua_planeta_rica.databinding.FragmentRequestBinding;
import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.util.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.agua_planeta_rica.util.Common.CURRENT_USER;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";
    private FragmentRequestBinding binding;
    private PageViewModel pageViewModel;
    private List<Request> requestList = new ArrayList<>();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceRequest;
    private AdapterRequest adapterRequest;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRequestBinding.inflate(inflater);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceRequest = mDatabase.getReference("requests");
        mReferenceRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Request req = postSnapshot.getValue(Request.class);
                    if (req != null && req.getUsername().equals(CURRENT_USER.getUid())) {
                        if (req.getState().equals("Entregado") && !req.isDeleted()) {
                            requestList.add(req);
                        }
                    }
                }
                placeToAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Eliminar")) {
            modify(requestList.get(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void modify(Request request) {
        request.setDeleted(true);
        mReferenceRequest.child(request.getCode())
                .updateChildren(request.toMap());
        Toast.makeText(getContext(), "Compra eliminada.", Toast.LENGTH_SHORT).show();
    }

    public void placeToAdapter() {
        if (!requestList.isEmpty()) {
            adapterRequest = new AdapterRequest(getContext(), requestList, 0);
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
}