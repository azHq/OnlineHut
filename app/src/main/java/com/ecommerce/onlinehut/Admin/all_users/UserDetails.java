package com.ecommerce.onlinehut.Admin.all_users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.Buyer.All_Animals_For_Buyer;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDetails extends AppCompatActivity {

    User user;
    String user_id, user_name, user_type, phone_number, image_path, device_id;
    boolean is_admin,  disabled;
    String location;

    private TextView name, id, empty;
    private ImageView pp;
    private RecyclerView rv;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Animal> animals = new ArrayList<>();
    ArrayList<String> imagesPathList=new ArrayList<>();
    private UserAnimalListAdapter animalListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Intent i = getIntent();
        user_id = i.getStringExtra("user_id");
        user_name = i.getStringExtra("user_name");
        user_type = i.getStringExtra("user_type");
        phone_number = i.getStringExtra("phone_number");
        image_path = i.getStringExtra("image_path");
        device_id = i.getStringExtra("device_id");
        is_admin = i.getBooleanExtra("is_admin", false);
        disabled = i.getBooleanExtra("disabled", false);
        location = i.getStringExtra("location");
        user = new User(user_id, user_name, user_type, phone_number, image_path, device_id, is_admin, disabled, location);
        Log.d("==========", user.getUser_type());
        empty = findViewById(R.id.empty);
        name = findViewById(R.id.nameTV);
        id = findViewById(R.id.idTV);
        pp = findViewById(R.id.pp);
        rv = findViewById(R.id.animalsRV);

        name.setText(user.getUser_name());
        id.setText(user.getUser_id());

        if (user.getImage_path().length() > 5)
            Picasso.get().load(user.getImage_path()).into(pp);

        getAnimals();
    }

    private void getAnimals() {
        Query documentReference=db.collection("AllAnimals");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                animals.clear();
                imagesPathList.clear();
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
                            if(user.getUser_type().equals("seller")){
                                if(!user.getUser_id().equals(map.get("user_id")))
                                    continue;
                            }
                            else
                                if(!map.containsKey("buyer_id") || !user.getUser_id().equals(map.get("buyer_id")))
                                    continue;
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
                            String compress_image_path=""; //map.get("compress_image_path").toString();
                            String[] image_paths=map.get("original_image_path").toString().split(",");
                            String image_path=image_paths[0];
                            System.out.println("image path:"+image_path+" length:"+image_paths.length);
                            String video_path=map.get("video_path").toString();
                            int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                            int total_bid=Integer.parseInt(map.get("total_bid").toString());
                            String animal_alt_id=map.get("alternative_id").toString();
                            Animal animal=new Animal(animal_id,animal_alt_id,user_id,name,price,age,color,weight,height,teeth,born,image_path,video_path,highest_bid,total_bid);
                            animal.setSold_status(map.get("sold_status").toString());
                            animal.setBuyer_id(map.containsKey("buyer_id")?map.get("buyer_id").toString():null);
                            animals.add(animal);
                            imagesPathList.add(compress_image_path);
                        }
                        initRV();
                    }
                    else{
                        rv.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    }

                }
                else{

                    rv.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);

                }

            }


        });
    }

    private void initRV() {
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // specify an adapter (see also next example)
        animalListAdapter = new UserAnimalListAdapter(animals, user);
        rv.setAdapter(animalListAdapter);
    }
}