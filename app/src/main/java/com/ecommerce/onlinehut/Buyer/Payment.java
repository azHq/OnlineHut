package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sslcommerz.library.payment.model.datafield.MandatoryFieldModel;
import com.sslcommerz.library.payment.model.dataset.TransactionInfo;
import com.sslcommerz.library.payment.model.util.CurrencyType;
import com.sslcommerz.library.payment.model.util.ErrorKeys;
import com.sslcommerz.library.payment.model.util.SdkCategory;
import com.sslcommerz.library.payment.model.util.SdkType;
import com.sslcommerz.library.payment.viewmodel.listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.viewmodel.management.PayUsingSSLCommerz;

import java.util.HashMap;
import java.util.Map;

public class Payment extends Fragment {
    public String document_id="",seller_id="",seller_device_id,user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView price_history_recycle;
    LinearLayout price_history_layout;
    TextView confirm_message,bkash_acc_number,rocket_acc_number,nagad_acc_number,mail_tv,phone_number_tv;
    Button submit_btn,call_btn,mail_btn,message_btn;
    EditText referral_code_et;
    String admin_device_id="",mail,phone_number="",admin_id="";
    CardView referral_code_card_view;
    public final int PHONE_CALL_PERMISSION=1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_payment, container, false);
        db=FirebaseFirestore.getInstance();
        user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait...");
        confirm_message=view.findViewById(R.id.confirm_message);
        bkash_acc_number=view.findViewById(R.id.bkash_acc_number);
        rocket_acc_number=view.findViewById(R.id.rocket_acc_number);
        nagad_acc_number=view.findViewById(R.id.nagad_acc_number);
        mail_tv=view.findViewById(R.id.mail);
        phone_number_tv=view.findViewById(R.id.phone_number);
        mail_btn=view.findViewById(R.id.mail_btn);
        call_btn=view.findViewById(R.id.call_btn);
        message_btn=view.findViewById(R.id.message_btn);
        referral_code_et=view.findViewById(R.id.referral_code);
        submit_btn=view.findViewById(R.id.submit_btn);
        referral_code_card_view=view.findViewById(R.id.referral_code_card_view);
        confirm_message=view.findViewById(R.id.confirm_message);
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
                Intent tnt=new Intent(getContext(), Messenger.class);
                tnt.putExtra("sender_id", SharedPrefManager.getInstance(getContext()).getUser().getUser_id());
                tnt.putExtra("receiver_id",user_id);
                tnt.putExtra("sender_type",SharedPrefManager.getInstance(getContext()).getUser().getUser_type());
                tnt.putExtra("receiver_type","Admin");
                tnt.putExtra("sender_device_id",SharedPrefManager.getInstance(getContext()).getUser().getDevice_id());
                tnt.putExtra("receiver_device_id",admin_device_id);
                startActivity(tnt);
            }
        });
        get_AppConfigurationData();

        return  view;
    }

    public void start_call(){
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        Toast.makeText(getContext(),"call sending",Toast.LENGTH_LONG).show();
        phoneIntent.setData(Uri.parse("tel:+88"+phone_number));
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(getContext(), "Please Give Permission For Phone Call", Toast.LENGTH_LONG).show();
        }
    }
    public void get_AppConfigurationData(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AppConfiguration").document("AppConfiguration");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    progressDialog.dismiss();
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
                        mail=data.get("email").toString();
                        mail_tv.setText(mail);
                        phone_number=data.get("phone_number").toString();
                        admin_id=data.get("admin_id").toString();
                        phone_number_tv.setText(EngToBanConverter.getInstance().convert(phone_number));
                    }

                }
            }
        });
    }


}
