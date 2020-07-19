package com.ecommerce.onlinehut;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    String body,sender_type="",title,sender_id,receiver_id,sender_device_id,receiver_device_id,document_id,activity_type,notification_id="";
    public JSONObject jsonObject=null,dataObject,notification;
    public FirebaseMessagingService(){

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        try {
            jsonObject = new JSONObject(remoteMessage.getData().toString());
            notification=jsonObject.getJSONObject("notification");
            title = notification.getString("title");
            body=notification.getString("body");
            dataObject = jsonObject.getJSONObject("data");
            sender_id=dataObject.getString("sender_id");
            receiver_id=dataObject.getString("receiver_id");
            sender_device_id=dataObject.getString("sender_device_id");
            receiver_device_id=dataObject.getString("receiver_device_id");
            document_id=dataObject.getString("document_id");
            activity_type=dataObject.getString("activity_type");
            sender_type=dataObject.getString("sender_type");
            notification_id=dataObject.getString("notification_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent tnt=new Intent(FirebaseMessagingService.this, All_Notification_Service.class);
        tnt.putExtra("title",title);
        tnt.putExtra("body",body);
        tnt.putExtra("sender_id",sender_id);
        tnt.putExtra("receiver_id",receiver_id);
        tnt.putExtra("sender_device_id",sender_device_id);
        tnt.putExtra("receiver_device_id",receiver_device_id);
        tnt.putExtra("document_id",document_id);
        tnt.putExtra("activity_type",activity_type);
        tnt.putExtra("notification_id",notification_id);
        tnt.putExtra("sender_type",sender_type);
        startService(tnt);

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        updateDeviceId(s);
        SharedPrefManager.getInstance(getApplicationContext()).changeDeviceId(s);
    }
    public void updateDeviceId(final String device_id){

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        if(firebaseUser!=null){
            DocumentReference documentReference= db.collection("Users").document(firebaseUser.getUid());
            Map<String, Object> user = new HashMap<>();
            user.put("device_id", device_id);


            documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }


    }


}
