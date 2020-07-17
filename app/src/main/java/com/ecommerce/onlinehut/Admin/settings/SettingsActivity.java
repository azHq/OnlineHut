package com.ecommerce.onlinehut.Admin.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.models.Setting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Setting> settings;
    private LinearLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();
        parent = findViewById(R.id.parent);

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
        java.util.Collections.sort(settings);
        for (Setting s : settings) {
            View v = getLayoutInflater().inflate(R.layout.list_item_appconfig, null);
            TextView label = v.findViewById(R.id.label);
            EditText value = v.findViewById(R.id.phone);
            ImageView edit = v.findViewById(R.id.edit);
            label.setText(s.getLabel());
            value.setText(s.getValue());
            edit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d("=======", value.getText().toString());
                                            Map<String, Object> map = new HashMap();
                                            String val = value.getText().toString();
                                            map.put(s.getLabel(), val);
                                            FirebaseFirestore.getInstance().collection("AppConfiguration")
                                                    .document("AppConfiguration").update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    s.setValue(val);
                                                    Toast.makeText(SettingsActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                            );
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                            builder.setMessage(String.format("Are you sure you want to update %s to %s?", s.getLabel(), value.getText().toString()));
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
            );

            parent.addView(v);
        }
    }

    /*

    * */

}