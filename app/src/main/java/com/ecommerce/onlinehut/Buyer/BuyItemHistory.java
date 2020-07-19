package com.ecommerce.onlinehut.Buyer;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class BuyItemHistory extends Fragment {
    ArrayList<Animal> animals=new ArrayList<Animal>();
    ArrayList<Animal> animals_temp=new ArrayList<Animal>();
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id;
    TextView empty;
    ProgressDialog progressDialog;
    Button add_new_animal;
    long countdownmillis=0;
    RecycleAdapter recycleAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_buy_item_history, container, false);
        firebaseAuth= FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db= FirebaseFirestore.getInstance();
        recyclerView=view.findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(animals);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(recycleAdapter);
        empty=view.findViewById(R.id.empty);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait");

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
        get_AppConfigurationData();

        return view;
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
        recycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        get_all_animals_data();
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
                        int expire_time=Integer.parseInt(data.get("confirmation_expire_time").toString());
                        countdownmillis=expire_time*60*60*1000;
                    }

                }
            }
        });
    }
    public void get_all_animals_data(){
        progressDialog.show();
        Query documentReference=db.collection("AllAnimals").whereIn("sold_status", Arrays.asList("sold", "confirm"));
        documentReference.whereEqualTo("buyer_id",user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                animals.clear();
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
                            String animal_id=map.get("animal_id").toString();
                            String user_id=map.get("user_id").toString();
                            String animal_type=map.get("type").toString();
                            String buyer_id=map.get("buyer_id").toString();
                            String sold_status=map.get("sold_status").toString();
                            int payment_complete=Integer.parseInt(map.get("payment_complete").toString());
                            int charge=Integer.parseInt(map.get("charge").toString());
                            String name=map.get("name").toString();
                            String time= toDateStr(((Timestamp)map.get("sold_time")).getSeconds()*1000);
                            int price=Integer.parseInt(map.get("price").toString());
                            float age=Integer.parseInt(map.get("age").toString());
                            String color=map.get("color").toString();
                            float weight=Float.parseFloat(map.get("weight").toString());
                            float height=Float.parseFloat(map.get("height").toString());
                            int teeth=Integer.parseInt(map.get("teeth").toString());
                            String born=map.get("born").toString();
                            String compress_image_path=map.get("compress_image_path").toString();
                            String[] image_paths=map.get("original_image_path").toString().split(",");
                            String video_path=map.get("video_path").toString();
                            int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                            int total_bid=Integer.parseInt(map.get("total_bid").toString());
                            String animal_alt_id=map.get("alternative_id").toString();
                            int sold_price= Integer.parseInt(map.get("sold_price").toString());
                            Animal animal=new Animal(animal_id,animal_alt_id,user_id,buyer_id,sold_status,sold_price,payment_complete,charge,time,animal_type,name,price,age,color,weight,height,teeth,born,image_paths[0],video_path,highest_bid,total_bid,compress_image_path);
                            animals.add(animal);


                        }
                        animals_temp.addAll(animals);
                        recyclerView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                        recycleAdapter.notifyDataSetChanged();
                    }
                    else{
                        recyclerView.setVisibility(View.GONE);
                        empty.setVisibility(View.VISIBLE);
                        recycleAdapter.notifyDataSetChanged();
                    }

                }
                else{

                    recyclerView.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                    recycleAdapter.notifyDataSetChanged();

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
        LinearLayout item;
        ImageView animal_image1,buyer_image_view,seller_image_view;
        TextView id_tv,name_tv1,price_tv1,payment_complete_tv1,charge_tv,sold_price;
        TextView seller_id_tv,seller_name_tv,seller_location_tv;
        TextView buyer_id_tv,buyer_name_tv,buyer_location_tv;
        TextView status,time_remain,timer,time;
        public ViewAdapter(View itemView) {
            super(itemView);
            mView=itemView;
            mView.setOnClickListener(this);
            id_tv=mView.findViewById(R.id.id);
            item=mView.findViewById(R.id.item);
            animal_image1=mView.findViewById(R.id.image);
            buyer_image_view=mView.findViewById(R.id.buyer_image);
            seller_image_view=mView.findViewById(R.id.seller_image);
            name_tv1=mView.findViewById(R.id.name);
            price_tv1=mView.findViewById(R.id.price);
            payment_complete_tv1=mView.findViewById(R.id.payment_complete);
            charge_tv=mView.findViewById(R.id.charge);
            seller_id_tv=mView.findViewById(R.id.seller_id);
            seller_name_tv=mView.findViewById(R.id.seller_name);
            seller_location_tv=mView.findViewById(R.id.seller_location);
            buyer_id_tv=mView.findViewById(R.id.buyer_id);
            buyer_name_tv=mView.findViewById(R.id.buyer_name);
            buyer_location_tv=mView.findViewById(R.id.buyer_location);
            status=mView.findViewById(R.id.status);
            time_remain=mView.findViewById(R.id.time_remain);
            time=mView.findViewById(R.id.time);
            timer=mView.findViewById(R.id.timer);
            sold_price=mView.findViewById(R.id.sold_price);

        }


        @Override
        public void onClick(View v) {


        }
    }
    @NonNull
    @Override
    public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.buy_item_layout,parent,false);
        return new ViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

        Animal animal=animals.get(position);
        holder.item.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
        holder.animal_image1.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
        holder.id_tv.setText("A-"+animal.animal_alt_id);
        holder.name_tv1.setText(animal.name);
        holder.price_tv1.setText(EngToBanConverter.getInstance().convert(animal.price+"") +" "+getString(R.string.taka));
        holder.payment_complete_tv1.setText(EngToBanConverter.getInstance().convert(animal.payment_complete+"")+" "+getString(R.string.taka));
        holder.charge_tv.setText(EngToBanConverter.getInstance().convert(animal.charge+"")+" "+getString(R.string.taka));
        holder.sold_price.setText(EngToBanConverter.getInstance().convert(animal.sold_price+"")+" "+getString(R.string.taka));
        if(animal.image_path!=null&&animal.image_path.length()>5){
            Picasso.get().load(animal.image_path).into(holder.animal_image1);
        }
        holder.name_tv1.setSelected(true);
        holder.price_tv1.setSelected(true);
        holder.payment_complete_tv1.setSelected(true);
        holder.charge_tv.setSelected(true);
        holder.id_tv.setSelected(true);

        holder.buyer_id_tv.setText(animal.buyer_id);
        holder.buyer_name_tv.setText(getString(R.string.buyer_name)+": "+ SharedPrefManager.getInstance(getContext()).getUser().user_name);
        holder.buyer_location_tv.setText(SharedPrefManager.getInstance(getContext()).getUser().location);
        if(SharedPrefManager.getInstance(getContext()).getUser().image_path.length()>0){
            Picasso.get().load(SharedPrefManager.getInstance(getContext()).getUser().image_path).into(holder.seller_image_view);
        }
        holder.buyer_id_tv.setSelected(true);
        holder.buyer_name_tv.setSelected(true);
        holder.buyer_location_tv.setSelected(true);
        if(animal.sold_status.equalsIgnoreCase("sold"))
        {
            holder.time_remain.setVisibility(View.GONE);
            holder.timer.setVisibility(View.GONE);
        }
        else{
            holder.time_remain.setVisibility(View.VISIBLE);
            long countMillis=System.currentTimeMillis()-getMillisFromDate(animal.time);
            if(countMillis<countdownmillis){
                start_countdown_timer(holder.timer,(countdownmillis-countMillis)/1000);
            }
            else{
                holder.timer.setText("FINISH!!");
            }

        }
        holder.time.setText(animal.time);
        holder.time.setSelected(true);
        holder.seller_id_tv.setText(animal.user_id);
        holder.seller_id_tv.setSelected(true);
        get_user_data(animal.user_id,holder.seller_image_view,holder.seller_name_tv,holder.seller_location_tv);



    }

    @Override
    public int getItemCount() {
        return animals.size();
    }

    }
    public void start_countdown_timer(TextView timer,long countdowntime){
        new CountDownTimer(countdowntime, 1000){
            long counter=countdowntime;
            public void onTick(long millisUntilFinished){
                String hours=(int)(counter/(3600))+"";
                String minutes=((int)(counter/(60))%60)+"";
                String seconds=((int)(counter)%60)+"";
                String time=hours+"'h:"+minutes+"'m:"+seconds+"'s";
                timer.setText(time);
                if(counter>0) counter--;
                System.out.println("time count:"+(counter/1000));
                System.out.println("time count:"+((counter/1000)%2));
            }
            public  void onFinish(){
                timer.setText("FINISH!!");
            }
        }.start();
    }
    public long getMillisFromDate(String str){
        String strDate = str;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        Date date=null;
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date!=null){
            return date.getTime();
        }
        else return 0;

    }
    public static String toDateStr(long milliseconds)
    {
        String format="dd-MM-yyyy hh:mm aa";
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }

    public void get_user_data(String user_id,ImageView imageView,TextView name,TextView location) {

        progressDialog.show();
        DocumentReference documentReference = db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        if (map.containsKey("user_name")) {
                            name.setText(getString(R.string.seller_name)+": "+map.get("user_name").toString());
                        }
                        if (map.containsKey("address") && map.get("address") != null && map.get("address").toString().length() > 0) {
                            location.setText(map.get("address").toString());
                        }
                        if (map.containsKey("image_path") && map.get("image_path") != null) {
                            String image_path = map.get("image_path").toString();
                            if (image_path.length() > 5) {
                                Picasso.get().load(image_path).into(imageView);
                            }
                        }
                        name.setSelected(true);
                        location.setSelected(true);


                    }
                } else {

                }
            }
        });

    }
}
