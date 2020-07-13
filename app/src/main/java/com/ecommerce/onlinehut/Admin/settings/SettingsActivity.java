package com.ecommerce.onlinehut.Admin.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Admin.all_users.UsersListAdapter;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.ecommerce.onlinehut.models.Setting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SettingsListAdapter settingsListAdapter;
    private FirebaseFirestore db;
    private List<Setting> settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rv = findViewById(R.id.appConfigRV);
        db = FirebaseFirestore.getInstance();

        settings = new ArrayList<>();
        db.collection("AppConfiguration").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                for (String key : map.keySet()) {
                                    Setting setting = new Setting(key, map.get(key).toString());
                                    settings.add(setting);
                                }
                                break;
                            }
                            renderSettingsRV();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error getting documents:", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void renderSettingsRV() {
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // specify an adapter (see also next example)
        settingsListAdapter = new SettingsListAdapter(settings);
        rv.setAdapter(settingsListAdapter);
    }

}