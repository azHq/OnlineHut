package com.ecommerce.onlinehut.Admin.settings;

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

import com.ecommerce.onlinehut.Admin.all_users.Menu;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.ecommerce.onlinehut.models.Setting;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.ViewHolder> {

    private List<Setting> settings;

    public SettingsListAdapter(List<Setting> settings) {
        this.settings = settings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_appconfig, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Setting s = settings.get(position);
        holder.label.setText(s.getLabel());
        holder.value.setText(s.getValue());

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap();
                map.put(s.getLabel(), holder.value.getText().toString());
                FirebaseFirestore.getInstance().collection("AppConfiguration")
                        .document("AppConfiguration").set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notifyItemChanged(position);
                    }
                });
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView label, value;
        public Button update, delete;

        public ViewHolder(@NonNull View view) {
            super(view);
            label = view.findViewById(R.id.label);
            value = view.findViewById(R.id.value);
            update = view.findViewById(R.id.update);
            delete = view.findViewById(R.id.delete);
        }
    }
}
