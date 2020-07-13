package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.R;
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
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class PriceHistoryForBuyer extends AppCompatActivity {

    ArrayList<String> imagesPathList=new ArrayList<>();
    ArrayList<PriceHistoryItem> priceHistoryItems=new ArrayList<>();
    public String animal_id="",user_id="";
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView price_history_recycle;
    RecycleAdapter recycleAdapter;
    LinearLayout price_history_layout;
    EditText price_et;
    User user;
    int price=0,previous_price=0;
    RecyclerView recyclerView;
    TextView name_tv,price_tv,id_tv;
    ImageView imageView;
    Animal animal;
    TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_history_for_buyer);
        animal_id=getIntent().getStringExtra("animal_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        name_tv=findViewById(R.id.name);
        price_tv=findViewById(R.id.price);
        id_tv=findViewById(R.id.id);
        empty=findViewById(R.id.empty);
        recycleAdapter=new RecycleAdapter(priceHistoryItems);
        imageView=findViewById(R.id.image);
        recyclerView.setAdapter(recycleAdapter);
        get_animal_data();
        get_price_history();
    }
    public void Pricing(View view){

        Intent intent=new Intent(getApplicationContext(),Details.class);
        intent.putExtra("animal_id",animal_id);
        startActivity(intent);
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
                        recyclerView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                        recycleAdapter.notifyDataSetChanged();

                    }
                    else{
                        empty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                else{

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
                    animal=new Animal(animal_id,user_id,name,price,age,color,weight,height,teeth,born,image_path,video_path,highest_bid,total_bid);
                    if(image_paths[0].length()>0){

                        Picasso.get().load(image_paths[0]).into(imageView);
                    }
                    name_tv.setText(name);
                    price_tv.setText(price+" "+getString(R.string.taka));
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

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<PriceHistoryItem> priceHistoryItems;
        public RecycleAdapter(ArrayList<PriceHistoryItem> priceHistoryItems){
            this.priceHistoryItems=priceHistoryItems;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements AdapterView.OnItemClickListener {

            View mView;
            Button option_menu;
            TextView name,price,time,location;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                name=mView.findViewById(R.id.name);
                price=mView.findViewById(R.id.price);
                time=mView.findViewById(R.id.time);
                location=mView.findViewById(R.id.location);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.pricing_history_item_layout,parent,false);
            return new ViewAdapter(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            PriceHistoryItem image_path=priceHistoryItems.get(position);
            holder.name.setText(image_path.buyer_name);
            holder.name.setSelected(true);
            holder.price.setText(EngToBanConverter.getInstance().convert(image_path.price)+" "+getString(R.string.taka));
            holder.time.setText(image_path.time);
            holder.location.setText(image_path.buyer_location);
        }

        @Override
        public int getItemCount() {
            return priceHistoryItems.size();
        }
    }

}
