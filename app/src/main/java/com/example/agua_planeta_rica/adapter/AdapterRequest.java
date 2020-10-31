package com.example.agua_planeta_rica.adapter;

import android.content.Context;
import android.view.ContextMenu;
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

public class AdapterRequest extends RecyclerView.Adapter<AdapterRequest.RequestViewHolder> {

    private Context context;
    private List<Request> requestList;
    private int code;

    public AdapterRequest(Context context, List<Request> requestList, int code) {
        this.context = context;
        this.requestList = requestList;
        this.code = code;
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
        holder.binding.quantity.setText(String.format(Locale.US, "Cantidad: %d", requestList.get(position).getQuantity()));
        holder.binding.type.setText(requestList.get(position).getType());
        if (code == 0)
            holder.binding.circleImageView.setImageResource(R.drawable.ic_arrow_circle_down);
        else {
            holder.binding.circleImageView.setImageResource(R.drawable.ic_arrow_circle_up);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        RequestItemLayoutBinding binding;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RequestItemLayoutBinding.bind(itemView);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Realizar accion");
            if (code == 0) {
                menu.add(0, 0, getAdapterPosition(), "Eliminar");
            } else {
                menu.add(0, 0, getAdapterPosition(), "Modificar");
            }
        }
    }
}
