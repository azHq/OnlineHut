package com.ecommerce.onlinehut.Buyer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.CustomAlertDialog;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.NotificationSender;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.Seller.Add_New_Animal;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class All_Animals_For_Buyer extends Fragment {

    AlertDialog alertDialog;
    String admin_device_id="",mail,phone_number="",admin_id="";
    ArrayList<Animal> animals=new ArrayList<Animal>();
    ArrayList<Animal> animals_temp=new ArrayList<Animal>();
    ArrayList<String> animal_types=new ArrayList<>();
    ArrayList<String> price_range=new ArrayList<>();
    ArrayList<String> age_range=new ArrayList<>();
    ArrayList<String> colors=new ArrayList<>();
    ArrayList<String> weight_range=new ArrayList<>();
    ArrayList<String> borns=new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id;
    TextView empty;
    ProgressDialog progressDialog;
    Button add_new_animal;
    ArrayList<Animal> sold_animals=new ArrayList<>();
    RecycleAdapter2 horizontal_recycleAdapter;
    RecyclerView horizontal_recycleview;
    Button filter_panel_btn;
    DrawerLayout drawer;
    Spinner animal_type_sp,animal_price_sp,age_range_sp,color_sp,weight_range_sp,born_sp;
    String animal_type="",color="",born="";
    int price_start=0,price_end=0,age_start=0,age_end=0,weight_start=0,weight_end=0;
    int minimum_price;
    RecycleAdapter recycleAdapter;
    Button btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all_animals_for_buyer, container, false);

        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        recyclerView=view.findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(animals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recycleAdapter);
        empty=view.findViewById(R.id.empty);
        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        filter_panel_btn=view.findViewById(R.id.filter_panel_btn);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait");
        horizontal_recycleview=view.findViewById(R.id.horizontal_recycle);
        horizontal_recycleAdapter=new RecycleAdapter2(sold_animals);
        horizontal_recycleview.setAdapter(horizontal_recycleAdapter);

        BuyerDashboard.search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search_string=s.toString();
                search(search_string);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        filter_panel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.closeDrawer(GravityCompat.END);
                }
                else{
                    drawer.openDrawer(GravityCompat.END);
                }

            }
        });
        animal_types.add("সব");
        animal_types.add(getString(R.string.cow));
        animal_types.add(getString(R.string.buffalo));
        animal_types.add(getString(R.string.camel));
        animal_types.add(getString(R.string.dumba));
        animal_types.add(getString(R.string.goat));
        animal_types.add(getString(R.string.sheep));
        animal_type_sp=view.findViewById(R.id.animal_type);
        animal_type_sp.setAdapter(new CustomAdapter(getContext(),0,animal_types));
        animal_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){
                    animal_type=animal_types.get(position);
                }
                else{
                    animal_type="";
                }
                filter();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        price_range.add("সব");
        price_range.add("২০,০০০-৩০,০০০");
        price_range.add("৩০,০০০-৪০,০০০");
        price_range.add("৪০,০০০-৫০,০০০");
        price_range.add("৫০,০০০-৬০,০০০");
        price_range.add("৬০,০০০-৮০,০০০");
        price_range.add("৮০,০০০-১,০০,০০০");
        price_range.add("১,০০,০০০-১,২০,০০০");
        price_range.add("১,২০,০০০-১,৫০,০০০");
        price_range.add("১,৫০,০০০-২,০০,০০০");
        price_range.add("২,০০,০০০-৩,০০,০০০");
        price_range.add("৩,০০,০০০-৪,০০,০০০");
        price_range.add("৪,০০,০০০-৫,০০,০০০");
        price_range.add("৫,০০,০০০-১০,০০,০০০");
        price_range.add("১০,০০,০০০-১৫,০০,০০০");
        price_range.add("১৫,০০,০০০-২০,০০,০০০");
        animal_price_sp=view.findViewById(R.id.price);
        animal_price_sp.setAdapter(new CustomAdapter(getContext(),0,price_range));
        animal_price_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){

                    if(position==1)
                    {
                        price_start=20000;
                        price_end=30000;
                    }
                    else if(position==2)
                    {
                        price_start=30000;
                        price_end=40000;
                    }
                    else if(position==3)
                    {
                        price_start=40000;
                        price_end=50000;
                    }
                    else if(position==4)
                    {
                        price_start=50000;
                        price_end=60000;
                    }
                    else if(position==5)
                    {
                        price_start=60000;
                        price_end=80000;
                    }
                    else if(position==6)
                    {
                        price_start=80000;
                        price_end=100000;
                    }
                    else if(position==7)
                    {
                        price_start=100000;
                        price_end=120000;
                    }
                    else if(position==8)
                    {
                        price_start=120000;
                        price_end=150000;
                    }
                    else if(position==9)
                    {
                        price_start=150000;
                        price_end=200000;
                    }
                    else if(position==10)
                    {
                        price_start=200000;
                        price_end=300000;
                    }
                    else if(position==11)
                    {
                        price_start=300000;
                        price_end=400000;
                    }
                    else if(position==12)
                    {
                        price_start=400000;
                        price_end=500000;
                    }
                    else if(position==13)
                    {
                        price_start=500000;
                        price_end=1000000;
                    }
                    else if(position==14)
                    {
                        price_start=500000;
                        price_end=1000000;
                    }
                    else if(position==15)
                    {
                        price_start=1000000;
                        price_end=1500000;
                    }
                    else if(position==16)
                    {
                        price_start=1500000;
                        price_end=2000000;
                    }
                    filter();
                }
                else{
                    price_start=200;
                    price_end=6000000;
                    filter();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        colors.add(getString(R.string.all));
        colors.add(getString(R.string.white));
        colors.add(getString(R.string.black));
        colors.add(getString(R.string.red));
        colors.add(getString(R.string.brown));
        colors.add(getString(R.string.gray));
        colors.add(getString(R.string.mixed));
        color_sp=view.findViewById(R.id.color);
        color_sp.setAdapter(new CustomAdapter(getContext(),0,colors));
        color_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               if(position>0){
                   color=colors.get(position);
                   filter();
               }
               else{
                   color="";
                   filter();
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        age_range.add(getString(R.string.all));
        age_range.add("২-৩ বছর");
        age_range.add("৩-৪ বছর");
        age_range.add("৪-৫ বছর");
        age_range.add("৫-৬ বছর");
        age_range.add("৬-৭ বছর");
        age_range_sp=view.findViewById(R.id.age);
        age_range_sp.setAdapter(new CustomAdapter(getContext(),0,age_range));
        age_range_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){

                    if(position==1){
                        age_start=2*12;
                        age_end=3*12;
                    }
                    else if(position==2){
                        age_start=3*12;
                        age_end=4*12;
                    }
                    else if(position==3){
                        age_start=4*12;
                        age_end=5*12;
                    }
                    else if(position==4){
                        age_start=5*12;
                        age_end=6*12;
                    }
                    else if(position==5){
                        age_start=6*12;
                        age_end=7*12;
                    }
                    filter();
                }
                else{

                    age_start=0;
                    age_end=10*12;
                    filter();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        weight_range.add(getString(R.string.all));
        weight_range.add("১০০-১২০ কেজি");
        weight_range.add("১২০-১৫০ কেজি");
        weight_range.add("১৫০-১৮০ কেজি");
        weight_range.add("১৮০-২০০ কেজি");
        weight_range.add("১৮০-২০০ কেজি");
        weight_range.add("২০০-২৫০ কেজি");
        weight_range.add("২৫০-৩০০ কেজি");
        weight_range.add("৩০০-৩৫০ কেজি");
        weight_range.add("৩৫০-৪০০ কেজি");
        weight_range.add("৪০০-৫০০ কেজি");
        weight_range.add("৫০০-১০০০ কেজি");
        weight_range.add("১০০০-২০০০ কেজি");
        weight_range_sp=view.findViewById(R.id.weight);
        weight_range_sp.setAdapter(new CustomAdapter(getContext(),0,weight_range));
        weight_range_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){

                    if(position==1){
                        weight_start=100;
                        weight_end=120;
                    }
                    else if(position==2){
                        weight_start=120;
                        weight_end=150;
                    }
                    else if(position==3){
                        weight_start=150;
                        weight_end=180;
                    }
                    else if(position==4){
                        weight_start=180;
                        weight_end=200;
                    }
                    else if(position==5){
                        weight_start=200;
                        weight_end=250;
                    }
                    else if(position==6){
                        weight_start=250;
                        weight_end=300;
                    }
                    else if(position==7){
                        weight_start=300;
                        weight_end=350;
                    }
                    else if(position==8){
                        weight_start=350;
                        weight_end=400;
                    }
                    else if(position==9){
                        weight_start=400;
                        weight_end=500;
                    }
                    else if(position==10){
                        weight_start=500;
                        weight_end=1000;
                    }
                    else if(position==11){
                        weight_start=1000;
                        weight_end=2000;
                    }
                    filter();
                }
                else{
                    weight_start=0;
                    weight_end=10000;
                    filter();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        born_sp=view.findViewById(R.id.born);
        born_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               if(position>0){
                   born=borns.get(position);
                   filter();
               }
               else{
                   born="";
                   filter();
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    public void filter(){
        animals.clear();
        if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }
        for(int i=0;i<animals_temp.size();i++){
            Animal animal=animals_temp.get(i);

            //type filter
            if(animal_type.length()==0||animal_type.equalsIgnoreCase(animal.animal_type)){
                animals.add(animal);
            }
            else if(!animal_type.equalsIgnoreCase(animal.animal_type)){
                animals.remove(animal);
            }
            //price filter
            if(((animal.price>=price_start&&animal.price<=price_end))&&!animals.contains(animal)){
               // animals.add(animal);

            }
            else if((animal.price<price_start||animal.price>price_end)){
                animals.remove(animal);
            }
            //color filter
            if(color.equalsIgnoreCase(animal.color)&&!animals.contains(animal))
            {
               // animals.add(animal);
            }
            else if(color.length()>0&&!color.equalsIgnoreCase(animal.color)){
                animals.remove(animal);
            }
            //age filter
            if((animal.age>=age_start&&animal.age<=age_end)&&!animals.contains(animal))
            {
                //animals.add(animal);
            }
            else if((animal.age<age_start||animal.age>age_end)){
                animals.remove(animal);
            }
            //weight filter
            if(((animal.weight>=weight_start&&animal.weight<=weight_end))&&!animals.contains(animal))
            {
                //animals.add(animal);
            }
            else if((animal.weight<weight_start||animal.weight>weight_end)){
                animals.remove(animal);
            }
            //born filter
            if((born.equalsIgnoreCase(animal.born))&&!animals.contains(animal))
            {
               // animals.add(animal);
            }
            else if(born.length()>0&&!born.equalsIgnoreCase(animal.born)){
                animals.remove(animal);
            }
        }
        recycleAdapter.notifyDataSetChanged();
    }
    public void search(String search_string){

        animals.clear();
        search_string=search_string.toLowerCase().trim();
        for(int i=0;i<animals_temp.size();i++){
            Animal animal=animals_temp.get(i);
            String animal_id="A-"+animal.animal_alt_id;
            animal_id=animal_id.toLowerCase();
            String animal_name=animal.name.toLowerCase();
            if(animal_id.startsWith(search_string)||animal_name.startsWith(search_string)){
                animals.add(animal);
            }
        }
        filter();
        //recycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        get_all_animals_data();
        get_AppConfigurationData();
    }
    public class RecycleAdapter2 extends RecyclerView.Adapter<RecycleAdapter2.ViewAdapter>{

        ArrayList<Animal> animals;
        public RecycleAdapter2(ArrayList<Animal> animals){
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

                int position=getLayoutPosition();
                Animal animal=animals.get(position);
                Intent tnt=new Intent(getContext(),Sold_Item_Details.class);
                tnt.putExtra("animal_id",animal.animal_id);
                startActivity(tnt);

            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.animal_sold,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            Animal animal=animals.get(position);
            if(animal.image_path.length()>0){
                holder.item.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
                holder.animal_image.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
                Picasso.get().load(animal.image_path).into(holder.animal_image);
                //holder.image_name.setVisibility(View.GONE);
                holder.image_name.setText(getString(R.string.sold)+"\n"+ EngToBanConverter.getInstance().convert(animal.sold_price+"") +" "+getString(R.string.taka));
            }
            holder.option_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });



        }

        @Override
        public int getItemCount() {
            return animals.size();
        }



    }

    public void get_all_animals_data(){
        progressDialog.show();
        Query documentReference=db.collection("AllAnimals");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                animals.clear();
                animals_temp.clear();
                borns.clear();
                borns.add(getString(R.string.all));
                sold_animals.clear();
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
                            String animal_id=map.get("animal_id").toString();
                            String user_id=map.get("user_id").toString();
                            String name=map.get("name").toString();
                            String animal_type=map.get("type").toString();
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
                            if(!borns.contains(born)) borns.add(born);
                            String animal_alt_id=map.get("alternative_id").toString();
                            String sold_status=map.get("sold_status").toString();
                            Animal animal=new Animal(animal_id,animal_type,animal_alt_id,user_id,name,price,age,color,weight,height,teeth,born,image_path,video_path,highest_bid,total_bid);
                            int sold_price=0;
                            if(!sold_status.equalsIgnoreCase("unsold")&&map.containsKey("sold_price")){
                                sold_price=Integer.parseInt(map.get("sold_price").toString());
                                Animal animal2=new Animal(animal_id,animal_type,animal_alt_id,user_id,sold_status,sold_price,name,price,age,color,weight,height,teeth,born,compress_image_path,video_path,highest_bid,total_bid);
                                sold_animals.add(animal2);
                            }
                            else{
                                animals.add(animal);
                                get_user_data(animal.user_id,animal);
                            }
                        }
                        animals_temp.addAll(animals);
                        Collections.sort(animals);
                        Collections.sort(animals_temp);
                        horizontal_recycleAdapter.notifyDataSetChanged();
                        recycleAdapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.INVISIBLE);
                        born_sp.setAdapter(new CustomAdapter(getContext(),0,borns));
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

        ArrayList<Animal> animals;
        public RecycleAdapter(ArrayList<Animal> animals){
            this.animals=animals;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            Button compare,details,pricing;
            CardView card1,card2;
            ImageView animal_image1,animal_image2;
            TextView id_tv,name_tv1,name_tv2,price_tv1,price_tv2,highest_bid_tv1,highest_bid_tv2,weight_tv1,total_bid_tv2;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                animal_image1=mView.findViewById(R.id.animal_image1);
                id_tv=mView.findViewById(R.id.id);
                name_tv1=mView.findViewById(R.id.name1);
                price_tv1=mView.findViewById(R.id.price1);
                highest_bid_tv1=mView.findViewById(R.id.highest_bid1);
                weight_tv1=mView.findViewById(R.id.weight);
                compare=mView.findViewById(R.id.compare);
                details=mView.findViewById(R.id.details);
                pricing=mView.findViewById(R.id.pricing);
                card1=mView.findViewById(R.id.card1);
            }


            @Override
            public void onClick(View v) {

                Animal animal=animals.get(getLayoutPosition());
                Intent tnt=new Intent(getContext(),Details.class);
                tnt.putExtra("animal_id",animal.animal_id);
                startActivity(tnt);
            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.animal_list_item_for_buyer,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            Animal animal=animals.get(position);
            holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
            holder.animal_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
            holder.name_tv1.setText(getString(R.string.name2) +" : "+animal.name);
            holder.price_tv1.setText(getString(R.string.price)+" : "+EngToBanConverter.getInstance().convert(animal.price+"") +" "+getString(R.string.taka));
            holder.highest_bid_tv1.setText(getString(R.string.highest_bid)+" : "+EngToBanConverter.getInstance().convert(animal.highest_bid+"") +getString(R.string.taka));
            holder.weight_tv1.setText(getString(R.string.weight2)+" : "+EngToBanConverter.getInstance().convert((int)animal.weight+"")+" "+getString(R.string.kg));
            holder.id_tv.setText("A-"+animal.animal_alt_id);
            if(animal.image_path!=null&&animal.image_path.length()>5){
                Picasso.get().load(animal.image_path).into(holder.animal_image1);
            }
            holder.name_tv1.setSelected(true);
            holder.price_tv1.setSelected(true);
            holder.highest_bid_tv1.setSelected(true);
            holder.weight_tv1.setSelected(true);
            holder.compare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_compare_animal_list(animal.animal_id);
                }
            });
            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent tnt=new Intent(getContext(),Details.class);
                    tnt.putExtra("animal_id",animal.animal_id);
                    startActivity(tnt);
                }
            });
            holder.pricing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_price_writing_dialog(animal);
                }
            });
        }

        @Override
        public int getItemCount() {
            return animals.size();
        }

    }

    public static class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> user_types;
        LayoutInflater inflter;
        int flag;

        public CustomAdapter(Context applicationContext, int flag, ArrayList<String> user_types) {
            this.context = applicationContext;
            this.flag = flag;
            this.user_types = user_types;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return user_types.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.filter_item_layout, null);
            final TextView names =view.findViewById(R.id.user_type);
            names.setText(user_types.get(i));



            return view;
        }
    }

    public void show_compare_animal_list(String animal_id){
        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.compare_dialogbox_layout,null);
        alert.setView(view);
        progressDialog.dismiss();
        alertDialog=alert.show();;
        RecyclerView recyclerView=view.findViewById(R.id.recycle);
        RecycleAdapter_For_Compare recycleAdapter_for_compare=new RecycleAdapter_For_Compare(animals,animal_id);
        recyclerView.setAdapter(recycleAdapter_for_compare);
        Button cancel=view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });



    }
    public class RecycleAdapter_For_Compare extends RecyclerView.Adapter<RecycleAdapter_For_Compare.ViewAdapter>{

        ArrayList<Animal> animals;
        String animal_id;
        public RecycleAdapter_For_Compare(ArrayList<Animal> animals,String animal_id){
            this.animals=animals;
            this.animal_id=animal_id;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder{

            View mView;
            CardView card1,card2;
            ImageView animal_image1;
            TextView name_tv1,price_tv1,weight_tv1;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                animal_image1=mView.findViewById(R.id.image);
                name_tv1=mView.findViewById(R.id.name);
                price_tv1=mView.findViewById(R.id.price);
                weight_tv1=mView.findViewById(R.id.weight);
                card1=mView.findViewById(R.id.card);
            }


        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.compare_item,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            Animal animal=animals.get(position);
            holder.card1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
            holder.animal_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
            holder.name_tv1.setText(getString(R.string.name2) +" : "+animal.name);
            holder.price_tv1.setText(getString(R.string.price)+" : "+animal.price+" "+getString(R.string.taka));
            holder.weight_tv1.setText(getString(R.string.highest_bid)+" : "+animal.highest_bid+" "+getString(R.string.taka));
            if(animal.image_path!=null&&animal.image_path.length()>5){
                Picasso.get().load(animal.image_path).into(holder.animal_image1);
            }
            holder.name_tv1.setSelected(true);
            holder.price_tv1.setSelected(true);
            holder.card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();
                    Intent tnt=new Intent(getContext(),Compare.class);
                    tnt.putExtra("animal_id1",animal_id);
                    tnt.putExtra("animal_id2",animal.animal_id);
                    startActivity(tnt);
                }
            });

        }

        @Override
        public int getItemCount() {
            return animals.size();
        }

    }

    public void show_price_writing_dialog(Animal animal){

        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.price_writing_layout,null);
        alert.setView(view);
        alertDialog=alert.show();
        Button submit=view.findViewById(R.id.submit);
        Button cancel=view.findViewById(R.id.cancel);
        EditText price_et=view.findViewById(R.id.price);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price_str=price_et.getText().toString();
                if(price_str.length()>0){
                    int price=Integer.parseInt(EngToBanConverter.getInstance().convert_bangla_to_english(price_str));
                    if(price>=minimum_price&&minimum_price<=animal.price||minimum_price>animal.price){
                        alertDialog.dismiss();
                        upload_new_price(price,animal);
                    }
                    else {
                        CustomAlertDialog.getInstance().show_error_dialog(getContext(),getString(R.string.app_name),"পশুটির সর্বনিম্ন দাম "+minimum_price+" "+getString(R.string.taka));
                    }

                }
                else{
                    Toast.makeText(getContext(),getString(R.string.animal_price_write)+"",Toast.LENGTH_LONG).show();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


    public void upload_new_price(int price,Animal animal){
        progressDialog.show();
        if(price>animal.price){
            update_animal_info(price,animal.total_bid+1,animal);
        }
        DocumentReference documentReference=db.collection("BidHistory").document();
        Map<String,Object> map=new HashMap<>();
        map.put("seller_id",animal.user_id);
        map.put("seller_name",animal.seller_name);
        map.put("seller_location",animal.seller_location);
        map.put("buyer_id", SharedPrefManager.getInstance(getContext()).getUser().user_id);
        map.put("buyer_name",SharedPrefManager.getInstance(getContext()).getUser().user_name);
        map.put("buyer_location",SharedPrefManager.getInstance(getContext()).getUser().location);
        map.put("animal_id",animal.animal_id);
        map.put("price",price);
        map.put("document_id",documentReference.getId());
        map.put("time", FieldValue.serverTimestamp());
        String document_id=documentReference.getId();
        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                NotificationSender.getInstance().createNotification(getString(R.string.new_price_request),SharedPrefManager.getInstance(getContext()).getUser().user_name+" আপনার পশুটি "+ EngToBanConverter.getInstance().convert(price+"")+" টাকায় কিনতে চায়।",user_id,SharedPrefManager.getInstance(getContext()).getUser().user_name,SharedPrefManager.getInstance(getContext()).getUser().image_path,"buyer",animal.user_id,document_id,SharedPrefManager.getInstance(getContext()).getUser().device_id,animal.seller_device_id,"new price");
                NotificationSender.getInstance().createNotification(getString(R.string.new_price_request),SharedPrefManager.getInstance(getContext()).getUser().user_name+" পশুটি "+ EngToBanConverter.getInstance().convert(price+"")+" টাকায় কিনতে চায়।",user_id,SharedPrefManager.getInstance(getContext()).getUser().user_name,SharedPrefManager.getInstance(getContext()).getUser().image_path,"buyer",admin_id,document_id,SharedPrefManager.getInstance(getContext()).getUser().device_id,admin_device_id,"new price");
                progressDialog.dismiss();
            }
        });


    }

    public void update_animal_info(int price,int total_bid,Animal animal){

        DocumentReference documentReference=db.collection("AllAnimals").document(animal.animal_id);
        Map<String,Object> map=new HashMap<>();
        map.put("highest_bid",price);
        map.put("total_bid",total_bid);
        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                get_all_animals_data();
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
                        minimum_price =Integer.parseInt(data.get("minimum_price").toString());
                        String expire_time=data.get("confirmation_expire_time").toString();
                        String str=data.get("bkash_account_number").toString()+"("+data.get("bkash_account_number_status")+")";
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
    public void get_user_data(String user_id,Animal animal){
        DocumentReference documentReference = db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        String seller_name=map.get("user_name").toString();
                        String address="";
                        if(map.containsKey("address")){
                            address=map.get("address").toString();
                        }
                        String seller_device_id=map.get("device_id").toString();
                        animal.setSeller_device_id(seller_device_id);
                        animal.setSeller_location(address);
                        animal.setSeller_name(seller_name);
                    }
                }
            }
        });
    }

}
