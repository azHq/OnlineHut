package com.ecommerce.onlinehut.Admin.all_users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAnimalListAdapter extends RecyclerView.Adapter<UserAnimalListAdapter.ViewHolder> {

    private final User user;
    private List<Animal> animals;

    public UserAnimalListAdapter(List<Animal> animals, User user) {
        this.animals = animals;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user_cow, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animal a = animals.get(position);
        holder.id.setText(String.format("পশু আইডিঃ %s", a.getId()));
        holder.name.setText(String.format("নামঃ %s", a.getName()));
        holder.price.setText(String.format("%s টাকা", a.getPrice()));
        holder.sold.setText(String.format("স্ট্যাটাসঃ %s", a.sold_status));

        if (a.getBuyer_id() == null)
            holder.buyer.setVisibility(View.GONE);
        else
            holder.buyer.setText(String.format("ক্রেতা আইডিঃ %s", a.buyer_id));

        holder.seller.setText(String.format("বিক্রেতা আইডিঃ %s", a.getUser_id()));

        if (a.getImage_path().length() > 5)
            Picasso.get().load(a.getImage_path()).into(holder.pp);
        else
            holder.pp.setImageResource(R.drawable.logo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*view.getContext().startActivity(new Intent(view.getContext(), UserDetails.class)
                        .putExtra("user_id", a.getUser_id())
                        .putExtra("user_name", a.getUser_name())
                        .putExtra("user_type", a.getUser_type())
                        .putExtra("phone_number", a.getPhone_number())
                        .putExtra("image_path", a.getImage_path())
                        .putExtra("device_id", a.getDevice_id())
                        .putExtra("disabled", a.isDisabled())
                        .putExtra("location", a.getLocation())
                        .putExtra("is_admin", a.isAdmin())
                );*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return animals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView pp, optionsBtn;
        public TextView id, name, seller, buyer, price, sold;

        public ViewHolder(@NonNull View view) {
            super(view);
            pp = view.findViewById(R.id.animalpp);
            name = view.findViewById(R.id.animalname);
            id = view.findViewById(R.id.animalid);
            seller = view.findViewById(R.id.seller);
            buyer = view.findViewById(R.id.buyer);
            price = view.findViewById(R.id.price);
            sold = view.findViewById(R.id.sold);
        }
    }

}
