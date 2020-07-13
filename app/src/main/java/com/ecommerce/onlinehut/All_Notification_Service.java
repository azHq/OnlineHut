package com.ecommerce.onlinehut;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.Buyer.RequestConfirmation;
import com.ecommerce.onlinehut.Seller.Add_New_Animal;
import com.ecommerce.onlinehut.Seller.NewPriceRequest;
import com.ecommerce.onlinehut.Seller.SellerDashboard;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class All_Notification_Service extends Service {

    public static boolean inside_messenger=false;
    String title="",body="",activity_type="",sender_id="",receiver_id="",document_id="",sender_device_id="";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        title=(String)intent.getExtras().get("title");
        body=(String)intent.getExtras().get("body");
        sender_id=(String)intent.getExtras().get("sender_id");
        receiver_id=(String)intent.getExtras().get("receiver_id");
        document_id=(String)intent.getExtras().get("document_id");
        sender_device_id=(String)intent.getExtras().get("sender_device_id");
        activity_type=(String)intent.getExtras().get("activity_type");
        System.out.println(activity_type+","+document_id);
        generateNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void generateNotification(){

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId=(int) System.currentTimeMillis();
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(body);
        Intent resultIntent=null;
        mBuilder.setAutoCancel(true);

        if(activity_type.length()>0&&activity_type.equalsIgnoreCase("message")&&!inside_messenger){
            resultIntent= new Intent(this, Messenger.class);
            resultIntent.putExtra("sender_id",sender_id);
            resultIntent.putExtra("document_id",document_id);
            resultIntent.putExtra("sender_device_id",sender_device_id);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            Notification note = mBuilder.build();
            mNotificationManager.notify(notificationId, note);
            SharedPrefManager.getInstance(getApplicationContext()).increase_unseen_notification();
            if(SellerDashboard.message_unseen!=null){

                int unseen_message=SharedPrefManager.getInstance(getApplicationContext()).get_unseen_notification();
                if(unseen_message<0)SellerDashboard.message_unseen.setText(unseen_message+"");
                else{
                    SellerDashboard.message_unseen.setText("99+");
                }
            }
            else if(BuyerDashboard.message_unseen!=null){
                int unseen_message=SharedPrefManager.getInstance(getApplicationContext()).get_unseen_notification();
                if(unseen_message<0){
                    BuyerDashboard.message_unseen.setText(unseen_message+"");
                }
                else{
                    BuyerDashboard.message_unseen.setText("99+");
                }
            }
        }
        else if(activity_type.length()>0&&activity_type.equalsIgnoreCase("new price")){
            resultIntent= new Intent(this, NewPriceRequest.class);
            resultIntent.putExtra("sender_id",sender_id);
            resultIntent.putExtra("document_id",document_id);
            resultIntent.putExtra("sender_device_id",sender_device_id);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            Notification note = mBuilder.build();
            mNotificationManager.notify(notificationId, note);
            SharedPrefManager.getInstance(getApplicationContext()).increase_unseen_notification();
            if(SellerDashboard.message_unseen!=null){

                int unseen_message=SharedPrefManager.getInstance(getApplicationContext()).get_unseen_notification();
                if(unseen_message<0)SellerDashboard.message_unseen.setText(unseen_message+"");
                else{
                    SellerDashboard.message_unseen.setText("99+");
                }
            }
        }
        else if(activity_type.length()>0&&activity_type.equalsIgnoreCase("seller_want_to_sell")){
            resultIntent= new Intent(this, RequestConfirmation.class);
            resultIntent.putExtra("sender_id",sender_id);
            resultIntent.putExtra("document_id",document_id);
            resultIntent.putExtra("sender_device_id",sender_device_id);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            Notification note = mBuilder.build();
            mNotificationManager.notify(notificationId, note);
            SharedPrefManager.getInstance(getApplicationContext()).increase_unseen_notification();
            if(BuyerDashboard.message_unseen!=null){

                int unseen_message=SharedPrefManager.getInstance(getApplicationContext()).get_unseen_notification();
                if(unseen_message<0)BuyerDashboard.message_unseen.setText(unseen_message+"");
                else{
                    BuyerDashboard.message_unseen.setText("99+");
                }
            }
        }
    }
}
