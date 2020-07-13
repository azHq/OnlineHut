package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DisabledActivity extends AppCompatActivity {

    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disabled);
        msg = findViewById(R.id.disabledMsg);

        FirebaseFirestore.getInstance().collection("AppConfiguration").document("AppConfiguration").get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot ds = task.getResult();
                            if(ds.exists()){
                                Map<String, Object> map = ds.getData();
                                if(map.containsKey("account_disabled_message"))
                                    msg.setText(map.get("account_disabled_message").toString());
                            }
                        }
                    }
                }
        );
    }
}