package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    public String status;
    private FirebaseAnalytics _mFirebaseAnalytics;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=null;
    FirebaseFirestore db;
    String user_id;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser=firebaseAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        _mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.fade_in);
        LinearLayout logo=findViewById(R.id.back);
        logo.startAnimation(animation);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait...");
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {

               if(firebaseUser==null){

                   startActivity(new Intent(getApplicationContext(),SelectUserType.class));
                   finish();
               }
               else{
                   progressDialog.show();
                   user_id=firebaseUser.getUid();
                   get_user_data();


               }
            }
        };
        handler.postDelayed(runnable,2000);
    }
    public void get_user_data(){

       DocumentReference documentReference= db.collection("Users").document(user_id);
       documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful()) {
                   DocumentSnapshot document = task.getResult();
                   progressDialog.dismiss();
                   if (document.exists()) {

                       Map<String,Object> map=document.getData();
                       String user_type=map.get("user_type").toString();
                       User user=new User(map.get("user_id")+"",map.get("user_name")+"",map.get("user_type")+"",map.get("phone_number")+"",map.get("image_path")+"",map.get("device_id")+"");
                       user.setAdmin(map.containsKey("admin"));
                       if(map.containsKey("disabled"))
                           user.setDisabled((Boolean) map.get("disabled"));
                       else user.setDisabled(false);
                       Log.d("=============", String.valueOf(user.isDisabled()));
                       SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                       /*if(*//*!user.isAdmin() &&*//*user.isDisabled()){
                           Log.d("=============", String.valueOf(user.isDisabled()));
                           startActivity(new Intent(getApplicationContext(), DisabledActivity.class));
                            finish();
                       }*/
                       if(user_type.equalsIgnoreCase("seller")){

                           startActivity(new Intent(getApplicationContext(), SellerDashboard.class));
                           finish();
                       }
                       else{
                           startActivity(new Intent(getApplicationContext(), BuyerDashboard.class));
                           finish();
                       }

                   } else {
                       startActivity(new Intent(getApplicationContext(),SignIn.class));
                       finish();
                   }
               } else {
                   show_error_dialog();
               }
           }
       });

    }

    public void show_error_dialog(){
        AlertDialog.Builder alert=new AlertDialog.Builder(getApplicationContext());
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button btn=view.findViewById(R.id.ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }
}
