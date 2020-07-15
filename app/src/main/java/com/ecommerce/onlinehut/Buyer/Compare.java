package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Compare extends AppCompatActivity {
    ArrayList<String> imagesPathList=new ArrayList<>();
    RecyclerView recyclerView;
    public String animal_id1="",animal_id2="",user_id="";
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Animal animal;
    ImageView imageView1;
    TextView tv_price,tv_name,tv_color,tv_weight,tv_age,tv_height,tv_born,tv_teeth,highest_price_tv;
    ImageView imageView2;
    TextView tv_price2,tv_name2,tv_color2,tv_weight2,tv_age2,tv_height2,tv_born2,tv_teeth2,highest_price_tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        animal_id1=getIntent().getStringExtra("animal_id1");
        animal_id2=getIntent().getStringExtra("animal_id2");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        imageView1=findViewById(R.id.image1);
        imageView2=findViewById(R.id.image2);
        tv_name=findViewById(R.id.name);
        tv_price=findViewById(R.id.price);
        tv_age=findViewById(R.id.age);
        tv_weight=findViewById(R.id.weight);
        tv_height=findViewById(R.id.height);
        tv_color=findViewById(R.id.color);
        tv_born=findViewById(R.id.born);
        tv_teeth=findViewById(R.id.teeth);
        highest_price_tv=findViewById(R.id.highest_price);
        tv_name2=findViewById(R.id.name2);
        tv_price2=findViewById(R.id.price2);
        tv_age2=findViewById(R.id.age2);
        tv_weight2=findViewById(R.id.weight2);
        tv_height2=findViewById(R.id.height2);
        tv_color2=findViewById(R.id.color2);
        tv_born2=findViewById(R.id.born2);
        tv_teeth2=findViewById(R.id.teeth2);
        highest_price_tv2=findViewById(R.id.highest_price2);
        get_animal_data1();
        get_animal_data2();
    }
    public void get_animal_data1(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AllAnimals").document(animal_id1);
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
                    if(image_paths[0].length()>0){
                        Picasso.get().load(image_paths[0]).into(imageView1);
                    }
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

    public void get_animal_data2(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AllAnimals").document(animal_id2);
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
                    tv_name2.setText(name);
                    tv_price2.setText(price+" "+getString(R.string.taka));
                    String year=(int)(age/12)+"";
                    String month=(int)(age%12)+"";
                    tv_age2.setText(year+" "+getString(R.string.year)+" "+month+" "+getString(R.string.month));
                    tv_color2.setText(color);
                    tv_weight2.setText(weight+" "+getString(R.string.kg));
                    tv_height2.setText(height+" "+getString(R.string.feet));
                    tv_teeth2.setText(teeth+" "+getString(R.string.ti));
                    tv_born2.setText(born+"");
                    highest_price_tv2.setText(highest_bid+" "+getString(R.string.taka));
                    if(image_paths[0].length()>0){
                        Picasso.get().load(image_paths[0]).into(imageView2);
                    }
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
