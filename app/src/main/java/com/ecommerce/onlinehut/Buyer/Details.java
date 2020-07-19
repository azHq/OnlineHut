package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.CustomAlertDialog;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.Messenger;
import com.ecommerce.onlinehut.NotificationSender;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.ecommerce.onlinehut.SignIn;
import com.ecommerce.onlinehut.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Details extends AppCompatActivity {

    ArrayList<String[]> imagesPathList=new ArrayList<>();
    ArrayList<PriceHistoryItem> priceHistoryItems=new ArrayList<>();
    RecycleAdapter recycleAdapter;
    RecyclerView recyclerView;
    public String animal_id="",user_id="";
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Animal animal;
    ImageView imageView;
    TextView tv_price,tv_name,tv_color,tv_weight,tv_age,tv_height,tv_born,tv_teeth,highest_price_tv,id_tv;
    RecyclerView price_history_recycle;
    RecycleAdapterPriceHistory recycleAdapterPriceHistory;
    LinearLayout price_history_layout;
    EditText price_et;
    User user;
    int price=0,previous_price=0;
    String admin_device_id="",mail,phone_number="",admin_id="";
    Button send_message;
    VideoView videoView;
    MediaController mediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        animal_id=getIntent().getStringExtra("animal_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        mediaController=new MediaController(this);
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        videoView=findViewById(R.id.video_view);
        imageView=findViewById(R.id.image_view);
        tv_name=findViewById(R.id.name);
        tv_price=findViewById(R.id.price);
        tv_age=findViewById(R.id.age);
        tv_weight=findViewById(R.id.weight);
        tv_height=findViewById(R.id.height);
        tv_color=findViewById(R.id.color);
        tv_born=findViewById(R.id.born);
        tv_teeth=findViewById(R.id.teeth);
        price_et=findViewById(R.id.price_input);
        id_tv=findViewById(R.id.id);
        send_message=findViewById(R.id.send_message);
        price_history_recycle=findViewById(R.id.price_history_recycle);
        price_history_layout=findViewById(R.id.price_history_layout);
        recycleAdapterPriceHistory=new RecycleAdapterPriceHistory(priceHistoryItems);
        price_history_recycle.setAdapter(recycleAdapterPriceHistory);
        highest_price_tv=findViewById(R.id.highest_price);
        recycleAdapter=new RecycleAdapter(imagesPathList);
        recyclerView.setAdapter(recycleAdapter);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(videoView.isPlaying()){
                    videoView.pause();
                }
                else{
                    videoView.resume();
                }

            }
        });
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getApplicationContext(), Messenger.class);
                tnt.putExtra("sender_id", user_id);
                tnt.putExtra("receiver_id",admin_id);
                tnt.putExtra("sender_type",SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_type());
                tnt.putExtra("receiver_type","Admin");
                tnt.putExtra("sender_device_id",SharedPrefManager.getInstance(getApplicationContext()).getUser().getDevice_id());
                tnt.putExtra("receiver_device_id",admin_device_id);
                startActivity(tnt);
            }
        });
        get_animal_data();
        get_price_history();
        get_AppConfigurationData();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void read_details(View view){

        Intent tnt=new Intent(getApplicationContext(),PriceHistoryForBuyer.class);
        tnt.putExtra("animal_id",animal_id);
        startActivity(tnt);
    }
    public void submit_price(View view){

       if(price_et.getText().length()>0){
           price=Integer.parseInt(price_et.getText().toString());
       }
       else{
           CustomAlertDialog.getInstance().show_error_dialog(Details.this,getString(R.string.input_error),getString(R.string.price)+getString(R.string.write));
           return;
       }
        upload_new_price();

    }
    public void upload_new_price(){
        progressDialog.show();
        if(price>animal.price){
            update_animal_info(price,animal.total_bid+1);
        }
        DocumentReference documentReference=db.collection("BidHistory").document();
        Map<String,Object> map=new HashMap<>();
        map.put("seller_id",animal.user_id);
        map.put("seller_name",user.user_name);
        map.put("seller_location",user.location);
        map.put("buyer_id",SharedPrefManager.getInstance(getApplicationContext()).getUser().user_id);
        map.put("buyer_name",SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name);
        map.put("buyer_location",SharedPrefManager.getInstance(getApplicationContext()).getUser().location);
        map.put("animal_id",animal.animal_id);
        map.put("price",price);
        map.put("document_id",documentReference.getId());
        map.put("time", FieldValue.serverTimestamp());
        String document_id=documentReference.getId();
        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                price_history_layout.setVisibility(View.VISIBLE);
                priceHistoryItems.add(new PriceHistoryItem(map.get("seller_id")+"",map.get("seller_name")+"",map.get("seller_location")+"",map.get("buyer_id")+"",map.get("buyer_name")+"",map.get("buyer_location")+"",map.get("animal_id")+"",map.get("price")+"",map.get("time")+""));
                recycleAdapterPriceHistory.notifyDataSetChanged();
                NotificationSender.getInstance().createNotification(getString(R.string.new_price_request),SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name+" আপনার পশুটি "+ EngToBanConverter.getInstance().convert(price+"")+" টাকায় কিনতে চায়।",user_id,SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name,SharedPrefManager.getInstance(getApplicationContext()).getUser().image_path,"buyer",animal.user_id,document_id,SharedPrefManager.getInstance(getApplicationContext()).getUser().device_id,user.device_id,"new price");
                NotificationSender.getInstance().createNotification(getString(R.string.new_price_request),SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name+" পশুটি "+ EngToBanConverter.getInstance().convert(price+"")+" টাকায় কিনতে চায়।",user_id,SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name,SharedPrefManager.getInstance(getApplicationContext()).getUser().image_path,"buyer",admin_id,document_id,SharedPrefManager.getInstance(getApplicationContext()).getUser().device_id,admin_device_id,"new price");
                progressDialog.dismiss();
            }
        });


    }


    public void update_animal_info(int price,int total_bid){

        DocumentReference documentReference=db.collection("AllAnimals").document(animal_id);
        Map<String,Object> map=new HashMap<>();
        map.put("highest_bid",price);
        map.put("total_bid",total_bid);
        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

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
    public void get_price_history(){
        Query documentReference=db.collection("BidHistory").whereEqualTo("animal_id",animal_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot.size()>0){
                        for(DocumentSnapshot documentSnapshot:querySnapshot){
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
                            priceHistoryItems.add(new PriceHistoryItem(seller_id,seller_name,seller_location,buyer_id,buyer_name,buyer_location,animal_id,price,time));
                        }
                        price_history_layout.setVisibility(View.VISIBLE);
                        recycleAdapterPriceHistory.notifyDataSetChanged();
                    }
                    else{

                        price_history_layout.setVisibility(View.GONE);
                    }
                }
                else{

                }
            }
        });
    }
    public void get_animal_data(){
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
                    String compress_image_path=""; map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                    System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();
                    int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                    int total_bid=Integer.parseInt(map.get("total_bid").toString());
                    String animal_alt_id=map.get("alternative_id").toString();
                    String animal_type=map.get("type").toString();
                    animal=new Animal(animal_id,animal_type,animal_alt_id,user_id,name,price,age,color,weight,height,teeth,born,image_path,video_path,highest_bid,total_bid);
                    for(int i=0;i<image_paths.length;i++){
                        String[] str={image_paths[i],"image"};
                        imagesPathList.add(str);
                    }
                    if(video_path.length()>5){
                        String[] str={video_path,"video"};
                        imagesPathList.add(str);
                    }
                    recycleAdapter.notifyDataSetChanged();
                    tv_name.setText(name);
                    tv_price.setText(EngToBanConverter.getInstance().convert(price+"")+" "+getString(R.string.taka));
                    String year=(int)(age/12)+"";
                    String month=(int)(age%12)+"";
                    tv_age.setText(EngToBanConverter.getInstance().convert(year+"")+" "+getString(R.string.year)+" "+EngToBanConverter.getInstance().convert(month+"")+" "+getString(R.string.month));
                    tv_color.setText(color);
                    tv_weight.setText(EngToBanConverter.getInstance().convert(weight+"")+" "+getString(R.string.kg));
                    tv_height.setText(EngToBanConverter.getInstance().convert(height+"")+" "+getString(R.string.feet));
                    tv_teeth.setText(EngToBanConverter.getInstance().convert(teeth+"")+" "+getString(R.string.ti));
                    tv_born.setText(born+"");
                    id_tv.setText("A-"+animal.animal_alt_id);
                    highest_price_tv.setText(EngToBanConverter.getInstance().convert(highest_bid+"")+" "+getString(R.string.taka));
                    get_user_data(user_id);
                    videoView.setVideoPath(video_path);
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
    public void get_user_data(String user_id){

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
                        String location="";
                        if(map.containsKey("location")){
                            location=map.get("location").toString();
                        }
                        user=new User(map.get("user_id")+"",map.get("user_name")+"",map.get("user_type")+"",map.get("phone_number")+"",map.get("image_path")+"",map.get("device_id")+"",location);

                    }
                }
            }
        });

    }
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<String[]> animals;
        public RecycleAdapter(ArrayList<String[]> animals){
            this.animals=animals;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button option_menu;
            TextView image_name;
            RelativeLayout item;
            ImageView animal_image;
            LinearLayout play_btn;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                animal_image=mView.findViewById(R.id.animal_image);
                image_name=mView.findViewById(R.id.image_name);
                item=mView.findViewById(R.id.item_layout);
                option_menu=mView.findViewById(R.id.option_btn);
                play_btn=mView.findViewById(R.id.play_btn);
            }

            @Override
            public void onClick(View v) {
                int position =getLayoutPosition();
                String[] image_path=animals.get(position);
                if(image_path[0].length()>0){
                    videoView.setVisibility(View.GONE);
                    Picasso.get().load(image_path[0]).into( imageView);

                }
            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.animal_images,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            String[] image_path=animals.get(position);
            holder.image_name.setVisibility(View.GONE);
            holder.option_menu.setVisibility(View.GONE);
            if(image_path[0].length()>0&&image_path[1].equalsIgnoreCase("image")){
                holder.play_btn.setVisibility(View.GONE);
                holder.item.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale));
                holder.animal_image.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));
                Picasso.get().load(image_path[0]).into( holder.animal_image);
            }
            else if(image_path[0].length()>0&&image_path[1].equalsIgnoreCase("video")){
                holder.play_btn.setVisibility(View.VISIBLE);

            }
            if(position==0&&image_path[0].length()>0){
                Picasso.get().load(image_path[0]).into(imageView);
            }
            holder.play_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.setVideoPath(image_path[0]);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();
                    videoView.requestFocus();
                    progressDialog.show();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            progressDialog.dismiss();
                        }
                    });
                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            progressDialog.dismiss();
                            return false;
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return animals.size();
        }
    }
    public class RecycleAdapterPriceHistory extends RecyclerView.Adapter<RecycleAdapterPriceHistory.ViewAdapter>{

        ArrayList<PriceHistoryItem> priceHistoryItems;
        public RecycleAdapterPriceHistory(ArrayList<PriceHistoryItem> priceHistoryItems){
            this.priceHistoryItems=priceHistoryItems;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements AdapterView.OnItemClickListener {

            View mView;
            Button option_menu;
            TextView name,price,time;
            CircleImageView circleImageView;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                name=mView.findViewById(R.id.name);
                price=mView.findViewById(R.id.price);
                time=mView.findViewById(R.id.time);
                circleImageView=mView.findViewById(R.id.image);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.pricing_history_item_layout2,parent,false);
            return new ViewAdapter(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            PriceHistoryItem image_path=priceHistoryItems.get(position);
            holder.name.setText(image_path.buyer_name);
            holder.name.setSelected(true);
            holder.price.setText(EngToBanConverter.getInstance().convert(image_path.price)+" "+getString(R.string.taka));
            holder.time.setText(image_path.time);
            load_image(holder.circleImageView,image_path.buyer_id);
        }

        @Override
        public int getItemCount() {
            return priceHistoryItems.size();
        }
    }
    public void load_image(CircleImageView circleImageView, String user_id){
        DocumentReference  documentReference= db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    Map<String,Object> map=documentSnapshot.getData();
                    if(map.containsKey("image_path")){
                        String image_path=map.get("image_path").toString();
                        if(image_path.length()>5){
                            Picasso.get().load(image_path).into(circleImageView);
                        }
                    }
                }
            }
        });
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

                        phone_number=data.get("phone_number").toString();
                        admin_id=data.get("admin_id").toString();
                        get_device_id(admin_id);
                    }

                }
            }
        });
    }
    public void get_device_id(String user_id){
        DocumentReference documentReference = db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        if (map.containsKey("device_id")) {

                            admin_device_id=map.get("device_id").toString();
                        }
                    }
                }
            }
        });
    }
}
