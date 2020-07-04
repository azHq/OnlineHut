package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignIn extends AppCompatActivity {

    EditText phone_number_et;
    public String phone_number;
    AlertDialog alertDialog;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    Pinview pinview;
    String verification_code;
    FirebaseUser firebaseUser=null;
    FirebaseFirestore db;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    CallbackManager mCallbackManager;
    String user_type="",device_id="",user_name="",image_path="",email="";
    TextView open_sign_up_panel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        user_type=getIntent().getStringExtra("user_type");
        progressDialog=new ProgressDialog(SignIn.this);
        progressDialog.setMessage("Please Wait...");
        firebaseAuth=FirebaseAuth.getInstance();
        phone_number_et=findViewById(R.id.phone_number);
        db=FirebaseFirestore.getInstance();
        open_sign_up_panel=findViewById(R.id.open_sign_up_panel);

        open_sign_up_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent tnt=new Intent(getApplicationContext(),SignUp.class);
                tnt.putExtra("user_type",user_type);
                startActivity(tnt);
            }
        });
    }
    public void sign_in(View view){
        phone_number=phone_number_et.getText().toString();
        if(!isValidPhoneNumber(phone_number)){
            show_error_dialog();
            return;
        }
        clean_country_code();
        Intent tnt=new Intent(getApplicationContext(),PinViewLayout.class);
        tnt.putExtra("user_name","");
        tnt.putExtra("user_type","");
        tnt.putExtra("phone_number",phone_number);
        tnt.putExtra("activity_type","sign_in");
        startActivity(tnt);


    }
    public void clean_country_code(){

        if(phone_number.startsWith("88")) phone_number=phone_number.replace(phone_number.substring(0,2),"");
        if(phone_number.startsWith("+88")) phone_number=phone_number.replace(phone_number.substring(0,3),"");
    }
    public void faceboo_sign_in(View view){

        progressDialog.show();
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager login_manager= LoginManager.getInstance();
        login_manager.logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

        login_manager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            user_name=user.getDisplayName();
                            phone_number=user.getPhoneNumber();
                            email=user.getEmail();
                            image_path="https://graph.facebook.com/" + token.getUserId() + "/picture?type=large";
                            get_user_data(user.getUid());
                        } else {

                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void show_error_dialog(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button btn=view.findViewById(R.id.ok);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.phone_error_title);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(R.string.phone_error_body);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public boolean isValidPhoneNumber( String phone_number){

        phone_number=phone_number.replace("-","");
        phone_number=phone_number.replaceAll("\\s","");
        String pattern="(^([+]{1}[8]{2}|88)?(01){1}[1-9]{1}\\d{8})$";

        return phone_number.matches(pattern);
    }

    public void get_user_data(String user_id){

        progressDialog.show();
        DocumentReference documentReference= db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_type=document.get("user_type").toString();
                        getDeviceId(user_id);

                    } else {

                        set_user_data(user_id);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void getDeviceId(String user_id){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"Please Check Your Internet Connection",Toast.LENGTH_LONG).show();
                            return;
                        }

                        device_id = task.getResult().getToken();
                        update_login_status(device_id,user_id);



                    }
                });
    }
    public void  update_login_status(String device_id,String user_id){

        DocumentReference documentReference= db.collection("Users").document(user_id);
        Map<String, Object> user = new HashMap<>();
        user.put("device_id", device_id);
        user.put("logged_in", true);
        user.put("online", true);

        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(user_type.equalsIgnoreCase("seller")){

                    startActivity(new Intent(getApplicationContext(), SellerDashboard.class));
                    finish();
                }
                else{
                    startActivity(new Intent(getApplicationContext(), BuyerDashboard.class));
                    finish();
                }
            }
        });
    }
    public void set_user_data(String user_id){
        DocumentReference documentReference= db.collection("Users").document(user_id);
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", user_name);
        user.put("user_type", user_type);
        user.put("phone_number", phone_number);
        user.put("email", email);
        user.put("image_path", image_path);
        user.put("device_id", device_id);
        user.put("create_at", FieldValue.serverTimestamp());
        user.put("logged_in", true);
        user.put("online", true);

        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(user_type.equalsIgnoreCase("seller")){

                    startActivity(new Intent(getApplicationContext(), SellerDashboard.class));
                    finish();
                }
                else{
                    startActivity(new Intent(getApplicationContext(), BuyerDashboard.class));
                    finish();
                }
            }
        });
    }




}
