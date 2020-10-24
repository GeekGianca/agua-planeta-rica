package com.example.agua_planeta_rica.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.databinding.ItemInfoRequestLayoutBinding;
import com.example.agua_planeta_rica.model.Request;
import com.example.agua_planeta_rica.util.Common;
import com.example.agua_planeta_rica.view.delivery.MapsActivity;
import com.example.agua_planeta_rica.view.delivery.view.PlaceholderFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AdapterAdminRequest extends RecyclerView.Adapter<RequestAdminViewHolder> {
    private List<Request> requestList;
    private Context context;
    private boolean isDelivery;

    public AdapterAdminRequest(List<Request> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        this.isDelivery = false;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    @NonNull
    @Override
    public RequestAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestAdminViewHolder(LayoutInflater.from(context).inflate(R.layout.item_info_request_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdminViewHolder holder, int position) {
        holder.bind(requestList.get(position), context);
        holder.itemView.setOnClickListener(v -> {
            if (isDelivery) {
                Request rl = requestList.get(position);
                if (rl.getLat() == 0 && rl.getLng() == 0) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Sin referencia")
                            .setMessage("No se tiene referencia del pedido, puede llamar al numero de detalle y hacer el envio de la solicitud directamente sin la app.")
                            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()).setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + rl.getUserPhone()));
                            context.startActivity(callIntent);
                            dialog.dismiss();
                        }
                    }).create().show();
                } else {
                    Common.DELIVERY_NOW = requestList.get(position);
                    context.startActivity(new Intent(context, MapsActivity.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}

class RequestAdminViewHolder extends RecyclerView.ViewHolder {
    private ItemInfoRequestLayoutBinding binding;

    public RequestAdminViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemInfoRequestLayoutBinding.bind(itemView);
    }

    public void bind(Request request, Context context) {
        binding.product.setText(String.format("Producto: %s", request.getType()));
        binding.total.setText(String.format("Total: $%s", Common.DF.format(request.getTotalPrice())));
        binding.quantity.setText(String.format("Cantidad: %s", request.getQuantity()));
        binding.detail.setText(String.format("Detalle: %s", request.getDetail()));
        binding.state.setText(String.format("Estado: %s", request.getState()));
        if (request.getState().equals("Solicitado")) {
            binding.stateBg.setBackgroundColor(context.getResources().getColor(R.color.colorStateWaiting));
        } else if (request.getState().equals("Entregado")) {
            binding.stateBg.setBackgroundColor(context.getResources().getColor(R.color.colorStateDone));
        } else {
            binding.stateBg.setBackgroundColor(context.getResources().getColor(R.color.colorStateDecline));
        }
    }
}
