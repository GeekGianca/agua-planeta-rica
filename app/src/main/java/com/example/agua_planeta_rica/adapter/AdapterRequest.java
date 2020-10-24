package com.example.agua_planeta_rica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.databinding.RequestItemLayoutBinding;
import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.util.Common;

import java.util.List;
import java.util.Locale;

public class AdapterRequest extends RecyclerView.Adapter<RequestViewHolder> {

    private Context context;
    private List<Request> requestList;

    public AdapterRequest(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(LayoutInflater.from(context).inflate(R.layout.request_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.binding.textView3.setText(requestList.get(position).getCode());
        holder.binding.price.setText(Common.DF.format(requestList.get(position).getTotalPrice()));
        holder.binding.quantity.setText(String.format(Locale.US,"Cantidad: %d", requestList.get(position).getQuantity()));
        holder.binding.type.setText(requestList.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}

class RequestViewHolder extends RecyclerView.ViewHolder {
    RequestItemLayoutBinding binding;

    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = RequestItemLayoutBinding.bind(itemView);
    }
}
