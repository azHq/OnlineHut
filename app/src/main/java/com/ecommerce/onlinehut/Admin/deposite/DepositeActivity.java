package com.ecommerce.onlinehut.Admin.deposite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.ecommerce.onlinehut.Admin.all_users.UsersListAdapter;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DepositeActivity extends AppCompatActivity {

    private RecyclerView depositeRV;
    private EditText searchET;
    private ImageView searchBtn;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposite);
    }
}