package com.example.agua_planeta_rica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agua_planeta_rica.R;
import com.example.agua_planeta_rica.databinding.ItemUsersLayoutBinding;
import com.example.agua_planeta_rica.model.User;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<UserViewHolder> {
    private List<User> userList;
    private Context context;

    public AdapterUser(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.item_users_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

class UserViewHolder extends RecyclerView.ViewHolder {
    private ItemUsersLayoutBinding binding;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemUsersLayoutBinding.bind(itemView);
    }

    public void bind(User user) {
        binding.name.setText(String.format("Nombre: %s", user.getName()));
        binding.email.setText(String.format("Correo: %s", user.getEmail()));
        binding.direction.setText(String.format("Dirección: %s", user.getDirection()));
        binding.descriptionHouse.setText(String.format("Descripción casa: %s", user.getDescription()));
    }
}