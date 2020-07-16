package com.ecommerce.onlinehut.Admin.all_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllUsers extends AppCompatActivity {

    private RecyclerView allUsersRV;
    private EditText searchET;
    private ImageView searchBtn;
    private UsersListAdapter usersListAdapter;
    private FirebaseFirestore db;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        allUsersRV = findViewById(R.id.userRV);
        searchET = findViewById(R.id.searchUser);
        searchBtn = findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(
                view -> {
                    //todo:search
                }
        );
        db = FirebaseFirestore.getInstance();

        Log.d("=========", getIntent().getStringExtra("user_type"));
        getAllUsers();

    }

    private void getAllUsers() {
        users = new ArrayList<>();
        db.collection("Users").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!=null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> map=document.getData();
                                if(!map.get("user_type").equals(getIntent().getStringExtra("user_type")))
                                    continue;
                                User user=new User(map.get("user_id")+"",map.get("user_name")+"",map.get("user_type")+"",map.get("phone_number")+"",map.get("image_path")+"",map.get("device_id")+"");
                                user.setAdmin(map.containsKey("admin"));
                                if(map.containsKey("disabled"))
                                    user.setDisabled((Boolean) map.get("disabled"));
                                else user.setDisabled(false);
                                users.add(user);
                            }
                            renderUserRV();
                        } else {
                            Toast.makeText(getApplicationContext(),"Error getting documents:", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void renderUserRV(){
        allUsersRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // specify an adapter (see also next example)
        usersListAdapter = new UsersListAdapter(users);
        allUsersRV.setAdapter(usersListAdapter);
    }


}