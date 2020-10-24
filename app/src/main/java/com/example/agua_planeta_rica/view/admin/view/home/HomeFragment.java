package com.example.agua_planeta_rica.view.admin.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.adapter.AdapterProduct;
import com.example.agua_planeta_rica.databinding.FragmentHomeBinding;
import com.example.agua_planeta_rica.databinding.ItemAddProductLayoutBinding;
import com.example.agua_planeta_rica.model.Product;
import com.example.agua_planeta_rica.util.Common;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private DatabaseReference mReference;
    private List<Product> productList;
    private AlertDialog dialog;
    private ItemAddProductLayoutBinding productBind;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        binding.addProduct.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_add_product_layout, null, false);
            productBind = ItemAddProductLayoutBinding.bind(view);
            builder.setView(productBind.getRoot());
            productBind.add.setOnClickListener(v1 -> {
                if (!productBind.name.getText().toString().isEmpty() && !productBind.price.getText().toString().isEmpty()) {
                    Product product = new Product();
                    product.setCode(new Random().nextInt());
                    product.setName(productBind.name.getText().toString());
                    product.setPrice(Double.parseDouble(productBind.price.getText().toString()));
                    mReference.child("products").child(product.getCode() + "").setValue(product);
                    dialog.dismiss();
                    dialog.cancel();
                    Toast.makeText(getContext(), "Producto agregado a la lista.", Toast.LENGTH_SHORT).show();
                } else {
                    if (productBind.name.getText().toString().isEmpty()) {
                        productBind.layoutProductName.setError("Este campo es necesario.");
                    }
                    if (productBind.price.getText().toString().isEmpty()) {
                        productBind.laytoutPrice.setError("Este campo es necesario.");
                    }
                }
            });
            productBind.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    dialog.cancel();
                }
            });
            dialog = builder.create();
            dialog.show();
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Product product = postSnapshot.getValue(Product.class);
                            productList.add(product);
                        }
                        configureAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void configureAdapter() {
        AdapterProduct adapter = new AdapterProduct(productList, getContext());
        binding.listProduct.setHasFixedSize(true);
        binding.listProduct.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.listProduct.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}