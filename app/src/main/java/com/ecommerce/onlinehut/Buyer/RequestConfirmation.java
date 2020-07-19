package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.NotificationSender;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestConfirmation extends AppCompatActivity {

    ArrayList<String> imagesPathList=new ArrayList<>();
    PriceHistoryItem priceHistoryItem;
    public String document_id="",seller_id="",seller_device_id,user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView price_history_recycle;
    LinearLayout price_history_layout;
    EditText price_et;
    int price=0,previous_price=0;
    RecyclerView recyclerView;
    TextView name_tv,price_tv,id_tv;
    TextView buyer_name_tv,buyer_price_tv,buyer_location_tv,time_tv,comfirm_message;
    ImageView imageView;
    Animal animal;
    public long charge_percentage=0;
    TextView empty;
    String notification_id="";
    public boolean read_complete=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_confirmation);
        document_id=getIntent().getStringExtra("document_id");
        seller_id=getIntent().getStringExtra("sender_id");
        seller_device_id=getIntent().getStringExtra("sender_device_id");
        notification_id=getIntent().getStringExtra("notification_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        name_tv=findViewById(R.id.name);
        price_tv=findViewById(R.id.price);
        buyer_price_tv=findViewById(R.id.buyer_price);
        comfirm_message=findViewById(R.id.buyer_price);
        id_tv=findViewById(R.id.id);
        imageView=findViewById(R.id.image);
        get_price_history(document_id);
        get_AppConfigurationData();
        if(notification_id!=null&&notification_id.length()>5){
            update_notification_status2(notification_id);
        }

    }
    public void update_notification_status2(String document_id){
        Map<String, Object> map =new HashMap<>();
        map.put("seen_status","seen");
        DocumentReference documentReference1=db.collection("AllNotifications").document(document_id);
        documentReference1.update(map);

    }
    public void confirm(View view){

       if(read_complete){
           update_bidhistory();
           NotificationSender.getInstance().createNotification("ক্রেতা পশুটি কিনতে রাজি হয়েছেন", SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name+" আপনার পশুটি "+ EngToBanConverter.getInstance().convert(priceHistoryItem.price+"")+" টাকায় কিনতে রাজি হয়েছেন।",user_id,SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name,SharedPrefManager.getInstance(getApplicationContext()).getUser().image_path,"Buyer",seller_id,document_id,SharedPrefManager.getInstance(getApplicationContext()).getUser().device_id,seller_device_id,"confirm");

       }
       else{
           Toast.makeText(getApplicationContext(),"Please Press Confirm Button After Few Seconds",Toast.LENGTH_LONG).show();
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
                        charge_percentage=(long)data.get("charge");
                        comfirm_message.setText("বিঃদ্রঃ আপনাকে "+expire_time+" ঘণ্টার মধ্যে "+minimum_payment+"% পেমেন্ট কমপ্লিট করতে হবে।");

                    }

                }
            }
        });
    }
    public void update_bidhistory(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("BidHistory").document(document_id);
        Map<String,Object> map=new HashMap<>();
        map.put("sold_status","confirm");
        map.put("sold_time", FieldValue.serverTimestamp());
        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
        System.out.println("price:"+priceHistoryItem.price+" charge_percentage"+charge_percentage);
        int charge=(int)(Integer.parseInt(priceHistoryItem.price)*(charge_percentage/100.0f));
        documentReference=db.collection("AllAnimals").document(animal.animal_id);
        map=new HashMap<>();
        map.put("sold_status","confirm");
        map.put("buyer_id",user_id);
        map.put("payment_complete",0);
        map.put("charge",charge);
        map.put("sold_price",priceHistoryItem.price);
        map.put("sold_time", FieldValue.serverTimestamp());
        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Intent intent=new Intent(getApplicationContext(),ConfirmationMessageAndPaymentInfo.class);
                intent.putExtra("document_id",animal.animal_id);
                startActivity(intent);
                finish();
            }
        });
    }
    public void cancel(View view){
        finish();
    }
    public void get_price_history(String document_id){
        progressDialog.show();
        DocumentReference documentReference=db.collection("BidHistory").document(document_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){

                        System.out.println("read price history");
                        Map<String,Object> map=documentSnapshot.getData();
                        String seller_id=map.get("seller_id").toString();
                        String seller_name=map.get("seller_name").toString();
                        String seller_location=map.get("seller_location").toString();
                        String buyer_id=map.get("buyer_id").toString();
                        String buyer_name=map.get("buyer_name").toString();
                        String buyer_location=map.get("buyer_location").toString();
                        String animal_id=map.get("animal_id").toString();
                        String price=map.get("price").toString();
                        Timestamp timestamp = (Timestamp)map.get("time");
                        String time=toDateStr(timestamp.getSeconds()*1000);
                        priceHistoryItem=new PriceHistoryItem(seller_id,seller_name,seller_location,buyer_id,buyer_name,buyer_location,animal_id,price,time);
                        get_animal_data(animal_id);
                        buyer_price_tv.setText("আপনি "+ EngToBanConverter.getInstance().convert(price)+" "+getString(R.string.taka)+" দাম করেছেন।");
                        buyer_price_tv.setSelected(true);


                    }
                    progressDialog.dismiss();
                }


            }
        });
    }
    public static String toDateStr(long milliseconds)
    {
        String format="dd-MM-yyyy hh:mm aa";
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }
    public void get_animal_data(String animal_id){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AllAnimals").document(animal_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                imagesPathList.clear();
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot.exists()){
                    Map<String,Object> map=documentSnapshot.getData();
                    String animal_id=map.get("animal_id").toString();
                    String user_id=map.get("user_id").toString();
                    String name=map.get("name").toString();
                    int price=Integer.parseInt(map.get("price").toString());
                    float age=Integer.parseInt(map.get("age").toString());
                    String color=map.get("color").toString();
                    float weight=Float.parseFloat(map.get("weight").toString());
                    float height=Float.parseFloat(map.get("height").toString());
                    int teeth=Integer.parseInt(map.get("teeth").toString());
                    String born=map.get("born").toString();
                    String compress_image_path=map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                    System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();
                    int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                    int total_bid=Integer.parseInt(map.get("total_bid").toString());
                    String animal_alt_id=map.get("alternative_id").toString();
                    String animal_type=map.get("type").toString();
                    animal=new Animal(animal_id,animal_type,animal_alt_id,user_id,name,price,age,color,weight,height,teeth,born,image_path,video_path,highest_bid,total_bid);
                    if(image_paths[0].length()>0){

                        Picasso.get().load(image_paths[0]).into(imageView);
                    }
                    read_complete=true;
                    name_tv.setText(name);
                    price_tv.setText(EngToBanConverter.getInstance().convert( price+"")+" "+getString(R.string.taka));
                    id_tv.setText("A-"+animal.animal_alt_id);
                }
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }
}
