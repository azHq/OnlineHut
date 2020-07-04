package com.ecommerce.onlinehut;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectUserType extends AppCompatActivity {

    String user_type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);
    }
    public void seller(View view){
        user_type="seller";
        Intent tnt=new Intent(getApplicationContext(),SignIn.class);
        tnt.putExtra("user_type",user_type);
        startActivity(tnt);
        finish();
    }
    public void buyer(View view){
        user_type="buyer";
        Intent tnt=new Intent(getApplicationContext(),SignIn.class);
        tnt.putExtra("user_type",user_type);
        startActivity(tnt);
        finish();
    }
}
