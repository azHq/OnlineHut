package com.ecommerce.onlinehut.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ecommerce.onlinehut.Admin.all_users.AllUsers;
import com.ecommerce.onlinehut.Admin.deposite.DepositeActivity;
import com.ecommerce.onlinehut.Admin.settings.SettingsActivity;
import com.ecommerce.onlinehut.R;

public class AdminPanel extends AppCompatActivity {

    private CardView sellers, buyers, deposite, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        sellers = findViewById(R.id.sellers);
        buyers = findViewById(R.id.buyers);
        deposite = findViewById(R.id.deposite);
        settings = findViewById(R.id.settings);

        sellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AllUsers.class).putExtra("user_type", "seller"));
            }
        });

        buyers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AllUsers.class).putExtra("user_type", "buyer"));
            }
        });

        deposite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DepositeActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        });
    }
}