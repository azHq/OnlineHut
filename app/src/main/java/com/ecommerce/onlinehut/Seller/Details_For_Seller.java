package com.ecommerce.onlinehut.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.Buyer.Details;
import com.ecommerce.onlinehut.Buyer.PriceHistoryForBuyer;
import com.ecommerce.onlinehut.Buyer.PriceHistoryItem;
import com.ecommerce.onlinehut.CustomAlertDialog;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Details_For_Seller extends AppCompatActivity {

    ArrayList<String> imagesPathList=new ArrayList<>();
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
    AlertDialog alertDialog;
    int price=0,previous_price=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details__for__seller);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        animal_id=getIntent().getStringExtra("animal_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        imagesPathList.add("");
        imagesPathList.add("");
        imagesPathList.add("");
        imagesPathList.add("");
        recyclerView=findViewById(R.id.recycle);
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
        price_history_recycle=findViewById(R.id.price_history_recycle);
        price_history_layout=findViewById(R.id.price_history_layout);
        recycleAdapterPriceHistory=new RecycleAdapterPriceHistory(priceHistoryItems);
        price_history_recycle.setAdapter(recycleAdapterPriceHistory);
        id_tv.setText(animal_id);
        highest_price_tv=findViewById(R.id.highest_price);
        recycleAdapter=new RecycleAdapter(imagesPathList);
        recyclerView.setAdapter(recycleAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_animal_data();
        get_price_history();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void read_details(View view){

        Intent tnt=new Intent(getApplicationContext(), PriceHistoryForSeller.class);
        tnt.putExtra("animal_id",animal_id);
        startActivity(tnt);
    }



    public static String toDateStr(long milliseconds)
    {
        String format="dd-MM-yyyy hh:mm aa";
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }
    public void get_price_history(){
        Query documentReference=db.collection("BidHistory");
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
                    String compress_image_path=map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                    System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();
                    int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                    int total_bid=Integer.parseInt(map.get("total_bid").toString());
                    String animal_alt_id=map.get("alternative_id").toString();
                    animal=new Animal(animal_id,animal_alt_id,user_id,"",name,price,age,color,weight,height,teeth,born,image_path,compress_image_path,video_path,highest_bid,total_bid);
                    imagesPathList.addAll(Arrays.asList(image_paths));
                    recycleAdapter.notifyDataSetChanged();
                    tv_name.setText(name);
                    tv_price.setText(price+" "+getString(R.string.taka));
                    String year=(int)(age/12)+"";
                    String month=(int)(age%12)+"";
                    tv_age.setText(year+" "+getString(R.string.year)+" "+month+" "+getString(R.string.month));
                    tv_color.setText(color);
                    tv_weight.setText(weight+" "+getString(R.string.kg));
                    tv_height.setText(height+" "+getString(R.string.feet));
                    tv_teeth.setText(teeth+" "+getString(R.string.ti));
                    tv_born.setText(born+"");
                    highest_price_tv.setText(highest_bid+" "+getString(R.string.taka));
                    get_user_data(user_id);
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

        ArrayList<String> animals;
        public RecycleAdapter(ArrayList<String> animals){
            this.animals=animals;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button option_menu;
            TextView image_name;
            RelativeLayout item;
            ImageView animal_image;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                animal_image=mView.findViewById(R.id.animal_image);
                image_name=mView.findViewById(R.id.image_name);
                item=mView.findViewById(R.id.item_layout);
                option_menu=mView.findViewById(R.id.option_btn);
            }

            @Override
            public void onClick(View v) {
                int position =getLayoutPosition();
                String image_path=animals.get(position);
                if(image_path.length()>0){
                    Picasso.get().load(image_path).into( imageView);
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


            String image_path=animals.get(position);
            holder.image_name.setVisibility(View.GONE);
            holder.option_menu.setVisibility(View.GONE);
            if(image_path.length()>0){
                holder.item.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale));
                holder.animal_image.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));
                Picasso.get().load(image_path).into( holder.animal_image);
            }
            if(position==0&&image_path.length()>0){
                Picasso.get().load(image_path).into(imageView);
            }
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
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                name=mView.findViewById(R.id.name);
                price=mView.findViewById(R.id.price);
                time=mView.findViewById(R.id.time);
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
        }

        @Override
        public int getItemCount() {
            return priceHistoryItems.size();
        }
    }

    public void edit(View view){

        Intent intent=new Intent(getApplicationContext(),EditAnimalInfo.class);
        intent.putExtra("animal_id",animal_id);
        startActivity(intent);

    }
    public void delete(View view){

        show_exit_dialog(Details_For_Seller.this);

    }

    public void yes_delete(){
        progressDialog.show();
        DocumentReference  documentReference= db.collection("AllAnimals").document(animal_id);
        documentReference.delete();
        StorageReference storageReference=null;
        for(int i=0;i<imagesPathList.size();i++){
            if(imagesPathList.get(i).length()>5){
                storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(imagesPathList.get(i));
                storageReference.delete();
            }

        }
        if(animal.compress_image_path.length()>5){
            storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(animal.compress_image_path);
            storageReference.delete();
        }
        if(animal.video_path.length()>0){
            storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(animal.video_path);

            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    finish();
                }
            });
        }
    }

    public void show_exit_dialog(Context context){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.exit_panel,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button yes=view.findViewById(R.id.yes);
        Button no=view.findViewById(R.id.no);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.app_name);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(R.string.delete_permission);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                yes_delete();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
