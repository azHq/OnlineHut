package com.ecommerce.onlinehut.Seller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class All_Animals_For_Seller extends Fragment {

    ArrayList<ArrayList<Animal>> animals=new ArrayList<ArrayList<Animal>>();
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id;
    TextView empty;
    ProgressDialog progressDialog;
    Button add_new_animal;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all_animals, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        recyclerView=view.findViewById(R.id.recycle);
        add_new_animal=view.findViewById(R.id.add_new_animal);
        empty=view.findViewById(R.id.empty);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait");
        add_new_animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Add_New_Animal.class));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        get_all_animals_data();
    }

    public void get_all_animals_data(){
        progressDialog.show();
        Query documentReference=db.collection("AllAnimals");
        documentReference.whereEqualTo("user_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                animals.clear();
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        ArrayList<Animal> animals2=new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
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
                            String image_path=map.get("compress_image_path").toString();
                            String video_path=map.get("video_path").toString();
                            int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                            int total_bid=Integer.parseInt(map.get("total_bid").toString());
                            Animal animal=new Animal(animal_id,user_id,name,price,age,color,weight,height,teeth,born,image_path,video_path,highest_bid,total_bid);
                            animals2.add(animal);
                            if(animals2.size()==2){
                                animals.add(animals2);
                                animals2=new ArrayList<>();
                            }


                        }
                        if(animals2.size()>0){
                            animals.add(animals2);
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.INVISIBLE);
                        RecycleAdapter recycleAdapter=new RecycleAdapter(animals);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(recycleAdapter);
                    }
                    else{
                        recyclerView.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                    }

                }
                else{

                    recyclerView.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);

                }
                progressDialog.dismiss();

            }


        });
    }
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<ArrayList<Animal>> animals;
        public RecycleAdapter(ArrayList<ArrayList<Animal>> animals){
            this.animals=animals;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button show_bid_history1,profile;
            Button show_bid_history2;
            CardView card1,card2;
            ImageView animal_image1,animal_image2;
            TextView name_tv1,name_tv2,price_tv1,price_tv2,highest_bid_tv1,highest_bid_tv2,total_bid_tv1,total_bid_tv2;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                animal_image1=mView.findViewById(R.id.animal_image1);
                animal_image2=mView.findViewById(R.id.animal_image2);
                name_tv1=mView.findViewById(R.id.name1);
                name_tv2=mView.findViewById(R.id.name2);
                price_tv1=mView.findViewById(R.id.price1);
                price_tv2=mView.findViewById(R.id.price2);
                highest_bid_tv1=mView.findViewById(R.id.highest_bid1);
                highest_bid_tv2=mView.findViewById(R.id.highest_bid2);
                total_bid_tv1=mView.findViewById(R.id.total_bid1);
                total_bid_tv2=mView.findViewById(R.id.total_bid2);
                show_bid_history1=mView.findViewById(R.id.show_bid_history1);
                show_bid_history2=mView.findViewById(R.id.show_bid_history2);
                card1=mView.findViewById(R.id.card1);
                card2=mView.findViewById(R.id.card2);
            }


            @Override
            public void onClick(View v) {


            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.animal_list_item_for_seller,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            ArrayList<Animal> animals2=animals.get(position);
            if(animals2.size()==2){

                holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.card2.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.animal_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                holder.animal_image2.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                Animal animal=animals2.get(0);
                holder.name_tv1.setText(getString(R.string.name2) +" : "+animal.name);
                holder.price_tv1.setText(getString(R.string.price)+" : "+animal.price+" "+getString(R.string.taka));
                holder.highest_bid_tv1.setText(getString(R.string.highest_bid)+" : "+animal.highest_bid+" "+getString(R.string.taka));
                holder.total_bid_tv1.setText(getString(R.string.total_bid)+" : "+animal.total_bid+" "+getString(R.string.ti));
                if(animal.image_path!=null&&animal.image_path.length()>5){
                    Picasso.get().load(animal.image_path).into(holder.animal_image1);
                }

                holder.name_tv1.setSelected(true);
                holder.price_tv1.setSelected(true);
                holder.highest_bid_tv1.setSelected(true);
                holder.total_bid_tv1.setSelected(true);
                holder.card2.setVisibility(View.VISIBLE);
                animal=animals2.get(1);
                holder.name_tv2.setText(getString(R.string.name2) +" : "+animal.name);
                holder.price_tv2.setText(getString(R.string.price)+" : "+animal.price+" "+getString(R.string.taka));
                holder.highest_bid_tv2.setText(getString(R.string.highest_bid)+" : "+animal.highest_bid+" "+getString(R.string.taka));
                holder.total_bid_tv2.setText(getString(R.string.total_bid)+" : "+animal.total_bid+" "+getString(R.string.jon));
                if(animal.image_path!=null&&animal.image_path.length()>5){
                    Picasso.get().load(animal.image_path).into(holder.animal_image2);
                }
                holder.name_tv2.setSelected(true);
                holder.price_tv2.setSelected(true);
                holder.highest_bid_tv2.setSelected(true);
                holder.total_bid_tv2.setSelected(true);
            }
            else if(animals2.size()==1){
                holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.animal_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                Animal animal=animals2.get(0);
                holder.name_tv1.setText(getString(R.string.name2) +" : "+animal.name);
                holder.price_tv1.setText(getString(R.string.price)+" : "+animal.price+" "+getString(R.string.taka));
                holder.highest_bid_tv1.setText(getString(R.string.highest_bid)+" : "+animal.highest_bid+" "+getString(R.string.taka));
                holder.total_bid_tv1.setText(getString(R.string.total_bid)+" : "+animal.total_bid+" "+getString(R.string.jon));
                if(animal.image_path!=null&&animal.image_path.length()>5){
                    Picasso.get().load(animal.image_path).into(holder.animal_image1);
                }

                holder.name_tv1.setSelected(true);
                holder.price_tv1.setSelected(true);
                holder.highest_bid_tv1.setSelected(true);
                holder.total_bid_tv1.setSelected(true);
                holder.card2.setVisibility(View.INVISIBLE);
            }

            holder.card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(),Details_For_Seller.class);
                    tnt.putExtra("animal_id",animals2.get(0).animal_id);
                    startActivity(tnt);
                }
            });
            holder.card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(),Details_For_Seller.class);
                    tnt.putExtra("animal_id",animals2.get(1).animal_id);
                    startActivity(tnt);
                }
            });

            holder.show_bid_history1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(),PriceHistoryForSeller.class);
                    tnt.putExtra("animal_id",animals2.get(0).animal_id);
                    startActivity(tnt);
                }
            });
            holder.show_bid_history2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tnt=new Intent(getContext(),PriceHistoryForSeller.class);
                    tnt.putExtra("animal_id",animals2.get(1).animal_id);
                    startActivity(tnt);
                }
            });




        }

        @Override
        public int getItemCount() {
            return animals.size();
        }



    }
}
