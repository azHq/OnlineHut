package com.ecommerce.onlinehut;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationSender {

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String serverKey ="key="+"AAAAKYvTyKk:APA91bGcu1-qcRSGZ9SKYlq73fRuYm5RGF_cxaurnploEUwhuinV-fKfdw-RvecRhVaoFAoHZGIlQI96H4VdrLJeD9n8n45Q5SpL0L_N4Nr3yyPlywkXEg18rRf3oXjXs3e_LuGaFSV6";
    private static final String contentType = "application/json";
    public static NotificationSender instance=new NotificationSender();
    private  NotificationSender(){

    }
    public static NotificationSender getInstance(){
        if(instance==null){
            instance=new NotificationSender();
        }
        return instance;
    }

    public void createNotification(String title,String body,String sender_id,String receiver_id,String document_id,String sender_device_id,String receiver_device_id,String activity_type){

        JSONObject notification_payload = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notification.put("body", body);
            notification.put("title", title);
            payload.put("sender_id", sender_id);
            payload.put("receiver_id", receiver_id);
            payload.put("sender_id",sender_id);
            payload.put("receiver_id",receiver_id);
            payload.put("document_id",document_id);
            payload.put("sender_device_id",sender_device_id);
            payload.put("receiver_device_id",receiver_device_id);
            payload.put("activity_type",activity_type);
            data.put("notification",notification);
            data.put("data",payload);
            notification_payload.put("data",data);
            notification_payload.put("to", "d9iQeLAUQuGrEICxeNAEoE:APA91bGRuW-gtxKtUuV9TQKkv0GSBKQMNyzeriCvQBilbUJSIlVI7j0HoYzqGZrQP1zjPVlRtXawa_HcD3aOvD8BfoFVW-AMH_HedVMen-hZE6YZpVf2y9PZqjuutUyV9QIKUFdwhL3i");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendNotification(notification_payload);




    }
    public void sendNotification(JSONObject jsonObject) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


}
