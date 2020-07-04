package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PinViewLayout extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    Pinview pinview;
    String verification_code;
    FirebaseUser firebaseUser=null;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String user_name="",phone_number="",user_type="",device_id="",image_path="",user_id="",activity_type="";
    String mVerificationId;
    AlertDialog alertDialog;
    CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_view_layout);
        cardView=findViewById(R.id.code_resend);
        firebaseAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user_name=getIntent().getStringExtra("user_name");
        user_type=getIntent().getStringExtra("user_type");
        phone_number="+88"+getIntent().getStringExtra("phone_number");
        activity_type=getIntent().getStringExtra("activity_type");
        pinview = (Pinview) findViewById(R.id.pinview);
        progressDialog=new ProgressDialog(PinViewLayout.this);
        progressDialog.setMessage("Please Wait...");

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {


                verification_code=pinview.getValue();


            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                progressDialog.show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {


                Toast.makeText(getApplicationContext(),"Fail To Send Code",Toast.LENGTH_LONG).show();
                System.out.println("fial to send code:"+e.toString());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }


            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                mVerificationId = verificationId;
                mResendToken = token;

            }
        };
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phone_number,
                        mResendToken);

            }
        });
        send_code(mCallbacks,phone_number);
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    public void send_code(PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks,String phone_number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            if(user!=null){
                                user_id=user.getUid();
                               if(activity_type.equalsIgnoreCase("sign_in")) get_user_data(user_id);
                               else getDeviceId();
                            }
                            // ...
                        } else {
                            progressDialog.dismiss();
                           pin_error_dialog();
                        }
                    }
                });
    }
    public void get_user_data(String user_id){

        DocumentReference documentReference= db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {
                        user_type=document.get("user_type").toString();
                        getDeviceId();

                    } else {
                        FirebaseAuth.getInstance().signOut();
                        invalid_user_dialog();
                    }
                } else {
                    FirebaseAuth.getInstance().signOut();
                    invalid_user_dialog();
                }
            }
        });

    }
    public void  update_login_status(String device_id){

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
    public void getDeviceId(){

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"Please Check Your Internet Connection",Toast.LENGTH_LONG).show();
                            return;
                        }

                        device_id = task.getResult().getToken();
                        if(activity_type.equalsIgnoreCase("sign_in")) update_login_status(device_id);
                        else set_user_data();


                    }
                });
    }
    public void set_user_data(){
        DocumentReference documentReference= db.collection("Users").document(user_id);
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", user_name);
        user.put("user_type", user_type);
        user.put("phone_number", phone_number);
        user.put("image_path", "");
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
    public void invalid_user_dialog(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        alertDialog=alert.show();
        Button btn=view.findViewById(R.id.ok);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.account_error_tile);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(R.string.account_error_body);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(new Intent(getApplicationContext(),SignUp.class));
            }
        });

    }
    public void pin_error_dialog(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        alertDialog=alert.show();
        Button btn=view.findViewById(R.id.ok);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.pin_error_tile);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(R.string.pin_error_body);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

}
