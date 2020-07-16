package com.ecommerce.onlinehut.Admin.all_users;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    private List<User> users;

    public UsersListAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User u = users.get(position);
        holder.statusTV.setText(u.isDisabled() ? "Disabled" : "Enabled");
        holder.nameTV.setText(u.user_name);
        holder.phoneTV.setText(u.phone_number);
        if (u.getImage_path().length() > 5)
            Picasso.get().load(u.getImage_path()).into(holder.profilePicture);
        else
            Picasso.get().load("https://www.aalforum.eu/wp-content/uploads/2016/04/profile-placeholder.png").into(holder.profilePicture);

        holder.optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.optionsBtn);
                //inflating menu from xml resource
                popup.inflate(R.menu.admin_user_control_menu);
                popup.getMenu().findItem(R.id.disable).setVisible(!u.isDisabled());
                popup.getMenu().findItem(R.id.enable).setVisible(u.isDisabled());
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.viewProfile:
                                //handle menu1 click
                                Menu.viewProfile(u, view.getContext());
                                return true;
                            case R.id.disable:
                                show_disable_dialog(view.getContext(), u, position, UsersListAdapter.this);
                                //handle menu2 click
                                return true;
                            case R.id.enable:
                                show_enable_dialog(view.getContext(), u, position, UsersListAdapter.this);
                                //handle menu2 click
                                return true;
                            case R.id.delete:
                                //handle menu3 click
                                Menu.deleteProfile(u, view.getContext());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), UserDetails.class)
                        .putExtra("user_id", u.getUser_id())
                        .putExtra("user_name", u.getUser_name())
                        .putExtra("user_type", u.getUser_type())
                        .putExtra("phone_number", u.getPhone_number())
                        .putExtra("image_path", u.getImage_path())
                        .putExtra("device_id", u.getDevice_id())
                        .putExtra("disabled", u.isDisabled())
                        .putExtra("location", u.getLocation())
                        .putExtra("is_admin", u.isAdmin())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profilePicture, optionsBtn;
        public TextView nameTV, phoneTV, balanceTV, statusTV;

        public ViewHolder(@NonNull View view) {
            super(view);
            profilePicture = view.findViewById(R.id.profilePicture);
            optionsBtn = view.findViewById(R.id.optionsBtn);
            nameTV = view.findViewById(R.id.nameTV);
            phoneTV = view.findViewById(R.id.phoneTV);
            balanceTV = view.findViewById(R.id.balanceTV);
            statusTV = view.findViewById(R.id.statusTV);
        }
    }

    public void show_disable_dialog(Context context, User u, int position, UsersListAdapter usersListAdapter) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.exit_panel, null);
        alert.setView(view);
        AlertDialog alertDialog = alert.show();
        ;
        Button yes = view.findViewById(R.id.yes);
        Button no = view.findViewById(R.id.no);
        TextView title_tv = view.findViewById(R.id.title);
        title_tv.setText(R.string.app_name);
        TextView body_tv = view.findViewById(R.id.body);
        body_tv.setText(R.string.disable_confirmation);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu.disableProfile(u, position, usersListAdapter, alertDialog);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void show_enable_dialog(Context context, User u, int position, UsersListAdapter usersListAdapter) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.exit_panel, null);
        alert.setView(view);
        AlertDialog alertDialog = alert.show();
        ;
        Button yes = view.findViewById(R.id.yes);
        Button no = view.findViewById(R.id.no);
        TextView title_tv = view.findViewById(R.id.title);
        title_tv.setText(R.string.app_name);
        TextView body_tv = view.findViewById(R.id.body);
        body_tv.setText(R.string.enable_confirmation);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu.enableProfile(u, position, usersListAdapter, alertDialog);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
