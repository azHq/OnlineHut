package com.ecommerce.onlinehut.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.Buyer.PriceHistoryItem;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.NotificationSender;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.ecommerce.onlinehut.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewPriceRequest extends AppCompatActivity {

    ArrayList<String> imagesPathList=new ArrayList<>();
    PriceHistoryItem priceHistoryItem;
    public String document_id="",buyer_id="",buyer_device_id,user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView price_history_recycle;
    LinearLayout price_history_layout;
    EditText price_et;
    int price=0,previous_price=0;
    RecyclerView recyclerView;
    TextView name_tv,price_tv,id_tv;
    TextView buyer_name_tv,buyer_price_tv,buyer_location_tv,time_tv;
    ImageView imageView;
    Animal animal;
    TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_price_request);
        document_id=getIntent().getStringExtra("document_id");
        buyer_id=getIntent().getStringExtra("sender_id");
        buyer_device_id=getIntent().getStringExtra("sender_device_id");
        document_id=getIntent().getStringExtra("document_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        name_tv=findViewById(R.id.name);
        price_tv=findViewById(R.id.price);
        id_tv=findViewById(R.id.id);
        buyer_name_tv=findViewById(R.id.buyer_name);
        buyer_price_tv=findViewById(R.id.buyer_price);
        buyer_location_tv=findViewById(R.id.buyer_location);
        time_tv=findViewById(R.id.time);
        empty=findViewById(R.id.empty);
        imageView=findViewById(R.id.image);
        get_price_history(document_id);
    }

    public void cancel(View view){
        finish();
    }
    public void accept(View view){
        update_bidhistory();
        NotificationSender.getInstance().createNotification(getString(R.string.sell_request), SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name+" পশুটি আপনার কাছে "+ EngToBanConverter.getInstance().convert(priceHistoryItem.price+"")+" টাকায় বিক্রি করতে রাজি হয়েছেন।",user_id,buyer_id,document_id,SharedPrefManager.getInstance(getApplicationContext()).getUser().device_id,buyer_device_id,"seller_want_to_sell");
    }
    public void update_bidhistory(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("BidHistory").document(document_id);
        Map<String,Object> map=new HashMap<>();
        map.put("state","seller_want_to_sell");
        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }

    public void get_price_history(String document_id){
        DocumentReference documentReference=db.collection("BidHistory").document(document_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){

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
                        buyer_name_tv.setText(buyer_name+" "+EngToBanConverter.getInstance().convert(price)+" "+getString(R.string.taka)+" দাম করেছেন।");
                        buyer_location_tv.setText(buyer_location);
                        time_tv.setText(time);
                        buyer_price_tv.setText(EngToBanConverter.getInstance().convert(price)+" "+getString(R.string.taka));
                    }
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
                    animal=new Animal(animal_id,animal_alt_id,user_id,name,price,age,color,weight,height,teeth,born,image_path,video_path,highest_bid,total_bid);
                    if(image_paths[0].length()>0){

                        Picasso.get().load(image_paths[0]).into(imageView);
                    }
                    name_tv.setText(name);
                    price_tv.setText(EngToBanConverter.getInstance().convert( price+"")+" "+getString(R.string.taka));
                    id_tv.setText(animal.animal_id);
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
