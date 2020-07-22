package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.Buyer.Compare;
import com.ecommerce.onlinehut.Buyer.ConfirmationMessageAndPaymentInfo;
import com.ecommerce.onlinehut.Seller.Add_New_Animal;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

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
        new CheckNetWorkConnection(MainActivity.this).execute();

    }

    public void check_user(){
        if(firebaseUser==null){
            startActivity(new Intent(getApplicationContext(), SelectUserType.class));
            finish();
        }
        else{

            progressDialog.show();
            user_id=firebaseUser.getUid();
            get_user_data();
            //startActivity(new Intent(getApplicationContext(), ConfirmationMessageAndPaymentInfo.class));

        }
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
                       SharedPrefManager.getInstance(getApplicationContext()).set_shared_pref(map);
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
        AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button btn=view.findViewById(R.id.ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });

    }

    public void generate_keyhash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
    }
    class CheckNetWorkConnection extends AsyncTask<String, Void,Boolean> {
        Context activity;
        public CheckNetWorkConnection(Context activity) {
            this.activity= activity;
        }
        @Override
        protected Boolean doInBackground(String... strings) {
            boolean networkAvalaible;
            try {
                URL myUrl = new URL("https://www.google.com");
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(600);
                connection.connect();
                networkAvalaible = true;
            } catch (Exception e) {
                e.printStackTrace();
                networkAvalaible = false;
            }
            return networkAvalaible;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (aBoolean){
                            check_user();
                        }else {
                            show_error_dialog();
                        }
                    }
                },600);
                super.onPostExecute(aBoolean);
        }
    }
}
