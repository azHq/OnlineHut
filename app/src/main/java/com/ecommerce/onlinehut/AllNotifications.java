package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Admin.ViewConfirmationRequestForBuy;
import com.ecommerce.onlinehut.Admin.ViewPriceRequest;
import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.Buyer.RequestConfirmation;
import com.ecommerce.onlinehut.Seller.NewPriceRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AllNotifications extends AppCompatActivity {

    ArrayList<Notification> notifications=new ArrayList<Notification>();
    ArrayList<Notification> notifications_temp=new ArrayList<Notification>();
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id;
    TextView empty;
    ProgressDialog progressDialog;
    RecycleAdapter recycleAdapter;
    public TextView title_bar;
    EditText search_et;
    Button back_btn,search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notifications);
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(recycleAdapter);
        empty=findViewById(R.id.empty);
        back_btn=findViewById(R.id.back_btn);
        search_btn=findViewById(R.id.search_btn);
        title_bar=findViewById(R.id.title_bar);
        search_et=findViewById(R.id.search_et);
        search_et.addTextChangedListener(new TextWatcher() {
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
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setVisibility(View.VISIBLE);
                title_bar.setVisibility(View.GONE);
                search_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                title_bar.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                Animation animation=AnimationFactory.getInstance().right_to_left_scale_anim();
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                search_et.startAnimation(animation);
            }
        });
        search_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search_et.getRight() - search_et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                       // search_et.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_reverse));
                        Animation animation=AnimationFactory.getInstance().left_to_right_scale_anim();
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                search_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                title_bar.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                search_et.setVisibility(View.GONE);
                                title_bar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        search_et.startAnimation(animation);
                        return true;
                    }
                }
                return false;
            }
        });
        progressDialog=new ProgressDialog(AllNotifications.this);
        progressDialog.setMessage("Please Wait");
    }
    public void search(String search_string){
        notifications.clear();
        search_string=search_string.toLowerCase().trim();
        for(int i=0;i<notifications_temp.size();i++){
            Notification notification=notifications_temp.get(i);
            String date=notification.time;
            if(date.startsWith(search_string)){
                notifications.add(notification);
            }
        }
        recycleAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_all_notifications();
    }


    public void get_all_notifications(){
        progressDialog.show();
        Query documentReference=db.collection("AllNotifications").whereEqualTo("seen_status","unseen").whereEqualTo("receiver_id",user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                notifications.clear();
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Map<String,Object> map=queryDocumentSnapshot.getData();
                            String title=map.get("title").toString();
                            String body=map.get("body").toString();
                            String sender_id=map.get("sender_id").toString();
                            String sender_name=map.get("sender_name").toString();
                            String sender_type=map.get("sender_type").toString();
                            String sender_image_path=map.get("sender_image_path").toString();
                            String sender_device_id=map.get("sender_device_id").toString();
                            String receiver_id=map.get("receiver_id").toString();
                            String receiver_device_id=map.get("receiver_device_id").toString();
                            String status=map.get("seen_status").toString();
                            String time=DateTimeConverter.getInstance().toDateStr(((Timestamp)map.get("time")).getSeconds()*1000);
                            String document_id=map.get("document_id").toString();
                            String notification_id=map.get("notification_id").toString();
                            String sender_location=map.get("sender_location").toString();
                            String activity_type=map.get("activity_type").toString();
                            Notification notification=new Notification(notification_id,title,body,sender_id,sender_name,sender_image_path,sender_device_id,sender_location,receiver_id,receiver_device_id,status,document_id,time,sender_type,activity_type);
                            notifications.add(notification);

                        }
                        Collections.sort(notifications);
                        notifications_temp.addAll(notifications);
                        Collections.sort(notifications_temp);
                        recyclerView.setVisibility(View.VISIBLE);
                        recycleAdapter.notifyDataSetChanged();
                        empty.setVisibility(View.GONE);

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

        ArrayList<Notification> notifications;
        public RecycleAdapter(ArrayList<Notification> notifications){
            this.notifications=notifications;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            View mView;
            ImageView image,admin_checker;
            LinearLayout item;
            TextView name,user_type,time,location,title,body;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                image=mView.findViewById(R.id.image);
                name=mView.findViewById(R.id.name);
                title=mView.findViewById(R.id.title);
                body=mView.findViewById(R.id.body);
                user_type=mView.findViewById(R.id.user_type);
                location=mView.findViewById(R.id.location);
                item=mView.findViewById(R.id.item);
                time=mView.findViewById(R.id.time);
                admin_checker=mView.findViewById(R.id.admin_checker);
            }


            @Override
            public void onClick(View v) {
                int position= getLayoutPosition();
                Notification notification=notifications.get(position);

                if(notification.activity_type.equalsIgnoreCase("new message")){
                    update_notification_status(notification.activity_type);
                    Intent tnt=new Intent(getApplicationContext(), Messenger.class);
                    tnt.putExtra("sender_id", user_id);
                    tnt.putExtra("notification_id",notification.id);
                    tnt.putExtra("receiver_id",notification.sender_id);
                    tnt.putExtra("document_id",notification.document_id);
                    tnt.putExtra("sender_type",SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_type());
                    tnt.putExtra("receiver_type",notification.sender_type);
                    tnt.putExtra("sender_device_id",notification.receiver_device_id);
                    tnt.putExtra("receiver_device_id",notification.sender_device_id);
                    tnt.putExtra("duplicate",true);
                    startActivity(tnt);
                }
                else if(notification.activity_type.equalsIgnoreCase("new price")){
                    update_notification_status2(notification.id);
                    Intent resultIntent=null;
                    if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("seller")){
                        resultIntent= new Intent(getApplicationContext(), NewPriceRequest.class);
                    }
                    else if(SharedPrefManager.getInstance(getApplicationContext()).getUser().isAdmin()){
                        resultIntent= new Intent(getApplicationContext(), ViewPriceRequest.class);
                    }
                    resultIntent.putExtra("sender_id",notification.sender_id);
                    resultIntent.putExtra("notification_id",notification.id);
                    resultIntent.putExtra("document_id",notification.document_id);
                    resultIntent.putExtra("sender_device_id",notification.sender_device_id);
                    startActivity(resultIntent);
                }
                else if(notification.activity_type.equalsIgnoreCase("seller_want_to_sell")){
                    update_notification_status2(notification.id);
                    Intent resultIntent=null;
                    if(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_type.equalsIgnoreCase("buyer")){
                        resultIntent= new Intent(getApplicationContext(), RequestConfirmation.class);
                    }
                    else if(SharedPrefManager.getInstance(getApplicationContext()).getUser().isAdmin()){
                        resultIntent= new Intent(getApplicationContext(), ViewConfirmationRequestForBuy.class);
                    }
                    resultIntent.putExtra("sender_id",notification.sender_id);
                    resultIntent.putExtra("document_id",notification.document_id);
                    resultIntent.putExtra("notification_id",notification.id);
                    resultIntent.putExtra("sender_device_id",notification.sender_device_id);
                    startActivity(resultIntent);
                }
                else{
                    update_notification_status2(notification.id);
                }

            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.notification_item_layout,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            Notification notification=notifications.get(position);
            holder.item.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale));
            holder.image.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));

            holder.title.setText(notification.title);
            holder.body.setText(notification.body);
            holder.location.setText(notification.sender_location);
            String sender_tmp="";
            if(notification.sender_type.equalsIgnoreCase("seller")){
                sender_tmp=getString(R.string.seller2);
                holder.name.setText(notification.sender_name);
                holder.admin_checker.setVisibility(View.GONE);
            }
            else if(notification.sender_type.equalsIgnoreCase("buyer")){
                sender_tmp=getString(R.string.buyer2);
                holder.name.setText(notification.sender_name);
                holder.admin_checker.setVisibility(View.GONE);
            }
            else if(notification.sender_type.equalsIgnoreCase("admin")){
                sender_tmp=getString(R.string.admin2);
                holder.name.setText(R.string.admin2);
                holder.admin_checker.setVisibility(View.VISIBLE);
            }
            if(notification.sender_image_path!=null&&notification.sender_image_path.length()>5){
                Picasso.get().load(notification.sender_image_path).into(holder.image);
            }
            holder.user_type.setText(sender_tmp);
            holder.time.setText(notification.time);
            holder.name.setSelected(true);
            holder.location.setSelected(true);
            holder.time.setSelected(true);
            holder.title.setSelected(true);
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

    }

    public void update_notification_status(String activity_type){
        Query documentReference=db.collection("AllNotifications").whereEqualTo("seen_status","unseen").whereEqualTo("receiver_id",user_id).whereEqualTo("activity_type",activity_type);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                notifications.clear();
                if (task.isComplete()) {

                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && querySnapshot.size() > 0) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                            Map<String, Object> map =new HashMap<>();
                            map.put("seen_status","seen");
                            String document_id=queryDocumentSnapshot.getId();
                            DocumentReference documentReference1=db.collection("AllNotifications").document(document_id);
                            documentReference1.update(map);
                        }
                    }
                }
            }
        });
    }
    public void update_notification_status2(String document_id){
        Map<String, Object> map =new HashMap<>();
        map.put("seen_status","seen");
        DocumentReference documentReference1=db.collection("AllNotifications").document(document_id);
        documentReference1.update(map);

    }
}
