package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.CustomAlertDialog;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.Messenger;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfirmationMessageAndPaymentInfo extends AppCompatActivity {

    public String document_id="",seller_id="",seller_device_id,user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView price_history_recycle;
    LinearLayout price_history_layout;
    TextView confirm_message,bkash_acc_number,rocket_acc_number,nagad_acc_number,mail_tv,phone_number_tv;
    Button submit_btn,call_btn,mail_btn,message_btn;
    EditText referral_code_et;
    String admin_device_id="",mail,phone_number="";
    CardView referral_code_card_view;
    public final  int PHONE_CALL_PERMISSION=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comfimation_message_and_payment_info);
        document_id=getIntent().getStringExtra("document_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        confirm_message=findViewById(R.id.confirm_message);
        bkash_acc_number=findViewById(R.id.bkash_acc_number);
        rocket_acc_number=findViewById(R.id.rocket_acc_number);
        nagad_acc_number=findViewById(R.id.nagad_acc_number);
        mail_tv=findViewById(R.id.mail);
        phone_number_tv=findViewById(R.id.phone_number);
        mail_btn=findViewById(R.id.mail_btn);
        call_btn=findViewById(R.id.call_btn);
        message_btn=findViewById(R.id.message_btn);
        referral_code_et=findViewById(R.id.referral_code);
        submit_btn=findViewById(R.id.submit_btn);
        referral_code_card_view=findViewById(R.id.referral_code_card_view);
        confirm_message=findViewById(R.id.confirm_message);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();

            }
        });
        mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mail});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });
        message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getApplicationContext(), Messenger.class);
                tnt.putExtra("sender_id", SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id());
                tnt.putExtra("receiver_id",user_id);
                tnt.putExtra("sender_type",SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_type());
                tnt.putExtra("receiver_type","Admin");
                tnt.putExtra("sender_device_id",SharedPrefManager.getInstance(getApplicationContext()).getUser().getDevice_id());
                tnt.putExtra("receiver_device_id",admin_device_id);
                startActivity(tnt);
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(referral_code_et.getText().toString().length()==5){
                   upload_referral_code("referral_code",referral_code_et.getText().toString());
               }
               else{
                   CustomAlertDialog.getInstance().show_error_dialog(getApplicationContext(),getString(R.string.input_error),getString(R.string.referral_code_length));
               }
            }
        });
        get_AppConfigurationData();
    }
    public void upload_referral_code(String key,String value){

        Map<String, Object> user = new HashMap<>();
        user.put(key, value);
        DocumentReference documentReference=db.collection("AllAnimals").document(document_id);
        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                referral_code_card_view.setVisibility(View.GONE);
            }
        });

    }
    public void start_call(){
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        Toast.makeText(getApplicationContext(),"call sending",Toast.LENGTH_LONG).show();
        phoneIntent.setData(Uri.parse("tel:+88"+phone_number));
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHONE_CALL_PERMISSION);
            } else {
                startActivity(phoneIntent);
            }
        }
        else{
            startActivity(phoneIntent);
        }

    }
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHONE_CALL_PERMISSION);
            } else {
                start_call();
            }
        } else {
            start_call();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PHONE_CALL_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            start_call();
        } else {
            Toast.makeText(getApplicationContext(), "Please Give Permission For Phone Call", Toast.LENGTH_LONG).show();
        }
    }
    public void get_AppConfigurationData(){
        DocumentReference documentReference=db.collection("AppConfiguration").document("AppConfiguration");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists())
                    {
                        Map<String,Object> data=documentSnapshot.getData();
                        String minimum_payment=data.get("minimum_payment").toString();
                        String expire_time=data.get("confirmation_expire_time").toString();
                        String str=data.get("bkash_account_number").toString()+"("+data.get("bkash_account_number_status")+")";
                        bkash_acc_number.setText(EngToBanConverter.getInstance().convert(str));
                        str=data.get("rocket_account_number").toString()+"("+data.get("rocket_account_number_status")+")";
                        rocket_acc_number.setText(EngToBanConverter.getInstance().convert(str));
                        str=data.get("nagad_account_number").toString()+"("+data.get("nagad_account_number_status")+")";
                        nagad_acc_number.setText(EngToBanConverter.getInstance().convert(str));
                        confirm_message.setText("বিঃদ্রঃ আপনাকে "+ EngToBanConverter.getInstance().convert(expire_time) +" ঘণ্টার মধ্যে "+EngToBanConverter.getInstance().convert(minimum_payment)+"% পেমেন্ট কমপ্লিট করতে হবে।অন্যথায় "+EngToBanConverter.getInstance().convert(expire_time)+" ঘণ্টা পর অর্ডারটি বাতিল হয়ে যাবে।");
                        mail=data.get("email").toString();
                        mail_tv.setText(mail);
                        phone_number=data.get("phone_number").toString();
                        phone_number_tv.setText(EngToBanConverter.getInstance().convert(phone_number));
                    }

                }
            }
        });
    }
}
