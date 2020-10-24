package com.example.agua_planeta_rica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.databinding.ItemProductLayoutBinding;
import com.example.agua_planeta_rica.model.Product;
import com.example.agua_planeta_rica.util.Common;

import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public AdapterProduct(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}

class ProductViewHolder extends RecyclerView.ViewHolder {
    private ItemProductLayoutBinding binding;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemProductLayoutBinding.bind(itemView);
    }

    public void bind(Product product) {
        binding.code.setText(String.format("Codigo: %s", product.getCode()));
        binding.name.setText(String.format("Producto: %s", product.getName()));
        binding.price.setText(String.format("Precio: $%s", Common.DF.format(product.getPrice())));
    }
}
