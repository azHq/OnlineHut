package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

public class Messenger extends AppCompatActivity {

    TextView message_view;
    EditText et_message;
    public RecyclerView recyclerView;
    Button send;
    Thread thread;
    public RecycleAdapter recycleAdapter;
    TextView tv_name;
    Button back;
    CircleImageView profile_pic;
    String device_id;
    Handler handler;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    public  Context context;
    public static boolean isMessaging=true;
    public ArrayList<Messages> messages=new ArrayList<>();
    public String image_path="";
    public String my_id="";
    ListenerRegistration listenerRegistration;
    ProgressDialog progressDialog;
    public  String sender_id="",receiver_id="",sender_type="",receiver_type="",sender_device_id="",receiver_device_id="",sender_name="",receiver_name="",sender_image_path="",receiver_image_path="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        context=getApplicationContext();

        send=findViewById(R.id.send);
        progressDialog=new ProgressDialog(Messenger.this);
        progressDialog.setMessage("Please wait....");
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null) my_id=firebaseAuth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        messages=new ArrayList<>();
        recyclerView=findViewById(R.id.recycle);
        et_message=findViewById(R.id.message);
        tv_name=findViewById(R.id.user_name);
        back=findViewById(R.id.back);
        profile_pic=findViewById(R.id.profile_image);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recycleAdapter=new RecycleAdapter(messages);
        recyclerView.setAdapter(recycleAdapter);

        //getDeviceId();
        sender_id=getIntent().getStringExtra("sender_id");
        receiver_id=getIntent().getStringExtra("receiver_id");
        sender_type=getIntent().getStringExtra("sender_type");
        receiver_type=getIntent().getStringExtra("receiver_type");
        sender_device_id=getIntent().getStringExtra("sender_device_id");
        receiver_device_id=getIntent().getStringExtra("receiver_device_id");
        sender_name=SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name;
        sender_image_path=SharedPrefManager.getInstance(getApplicationContext()).getUser().image_path;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message=et_message.getText().toString();
                if(message.length()>0){

                    et_message.setText("");
                    sendMessage(message);
                }
                else{

                    Toast.makeText(getApplicationContext(),"Please Enter Message",Toast.LENGTH_LONG).show();
                }


            }
        });
        et_message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (et_message.getRight() - et_message.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Toast.makeText(getApplicationContext(),"Click On Drawable",Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                return false;
            }
        });
        get_receiver_data();


    }


    @Override
    protected void onPause() {
        super.onPause();
        isMessaging=false;
        if(listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllMessage();
        set_listener();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isMessaging=false;
        if(listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    public void set_listener(){

            listenerRegistration=db.collection("AllMessages").whereEqualTo("sender_id", receiver_id).addSnapshotListener(MetadataChanges.INCLUDE,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               if(queryDocumentSnapshots.size()>0){

                   for(DocumentChange documentChange:queryDocumentSnapshots.getDocumentChanges()){

                       if(documentChange.getType()==ADDED){
                           System.out.println("message send");
                           Map<String,Object> data=documentChange.getDocument().getData();
                           String receiver_id=data.get("receiver_id").toString();
                           if(receiver_id.equalsIgnoreCase(Messenger.this.sender_id)){
                               String message_id=data.get("message_id").toString();
                               String sender_id=data.get("sender_id").toString();
                               String sender_name=Messenger.this.sender_name;
                               String sender_image_path=Messenger.this.sender_image_path;
                               String sender_type=data.get("sender_type").toString();
                               String receiver_name=Messenger.this.receiver_name;
                               String receiver_image_path=Messenger.this.receiver_image_path;
                               String receiver_type=data.get("receiver_type").toString();
                               String message=data.get("message").toString();
                               String time=DateTimeConverter.getInstance().get_current_data_time();;
                               messages.add(new Messages(message_id,message,sender_id,sender_name,sender_image_path,sender_type,receiver_id,receiver_name,receiver_image_path,receiver_type,time));
                           }
                       }

                   }
                   recycleAdapter.notifyDataSetChanged();
               }
            }
        });
    }


    private void getAllMessage() {

        progressDialog.show();
        Query documentReference=db.collection("AllMessages").whereIn("sender_id", Arrays.asList(sender_id,receiver_id));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isComplete()){
                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot.size()>0){

                        for(DocumentSnapshot documentSnapshot:querySnapshot){

                            Map<String,Object> data=documentSnapshot.getData();
                            String receiver_id=data.get("receiver_id").toString();
                            if(receiver_id.equalsIgnoreCase(Messenger.this.sender_id)||receiver_id.equalsIgnoreCase(Messenger.this.receiver_id)){
                                String message_id=data.get("message_id").toString();
                                String sender_id=data.get("sender_id").toString();
                                String sender_name=Messenger.this.sender_name;
                                String sender_image_path=Messenger.this.sender_image_path;
                                String sender_type=data.get("sender_type").toString();
                                String receiver_name=Messenger.this.receiver_name;
                                String receiver_image_path=Messenger.this.receiver_image_path;
                                String receiver_type=data.get("receiver_type").toString();
                                String message=data.get("message").toString();
                                String time=DateTimeConverter.getInstance().toDateStr(((Timestamp)data.get("time")).getSeconds()*1000);
                                messages.add(new Messages(message_id,message,sender_id,sender_name,sender_image_path,sender_type,receiver_id,receiver_name,receiver_image_path,receiver_type,time));
                            }
                        }
                        recycleAdapter.notifyDataSetChanged();
                    }
                    else{

                    }

                    progressDialog.dismiss();
                }


            }
        });

    }

    public void get_receiver_data(){

        DocumentReference documentReference=db.collection("Users").document(receiver_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){

                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){
                        Map<String,Object> data=documentSnapshot.getData();
                        receiver_name=data.get("user_name").toString();
                        receiver_image_path=data.get("image_path").toString();
                    }

                }
            }
        });
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<Messages> messages;
        public RecycleAdapter(ArrayList<Messages> memberInfos){
            this.messages=memberInfos;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder{

            View mView;
            ImageView my_react,oponent_react;
            CircleImageView my_profile_pic,oponent_profile_pic;
            TextView my_message_view,oponent_message_view,name;
            RelativeLayout my_layout,oponent_layout;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                my_message_view=mView.findViewById(R.id.my_message_view);
                oponent_message_view=mView.findViewById(R.id.oponent_message_view);
                my_react=mView.findViewById(R.id.my_react);
                name=mView.findViewById(R.id.user_name);
                oponent_react=mView.findViewById(R.id.oponent_reat);
                my_profile_pic=mView.findViewById(R.id.my_profile_pic);
                oponent_profile_pic=mView.findViewById(R.id.oponent_profile_pic);
                my_layout=mView.findViewById(R.id.my_layout);
                oponent_layout=mView.findViewById(R.id.oponent_layout);

            }


        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_message_recv,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            final  Messages memberInfo=messages.get(position);
            if(memberInfo.sender_id.equalsIgnoreCase(my_id)){
                holder.my_layout.setVisibility(View.VISIBLE);
                holder.my_message_view.setText(memberInfo.message);
                holder.oponent_layout.setVisibility(View.GONE);
            }
            else{
                holder.oponent_layout.setVisibility(View.VISIBLE);
                holder.oponent_message_view.setText(memberInfo.message);
                holder.my_layout.setVisibility(View.GONE);
                if(image_path.length()>0&&!memberInfo.receiver_type.equalsIgnoreCase("admin")){
                    Picasso.get().load(image_path).placeholder(R.drawable.profile10).into(holder.oponent_profile_pic);
                    holder.name.setText(memberInfo.receiver_name);
                }
                else if(memberInfo.receiver_type.equalsIgnoreCase("admin")){
                    holder.name.setText("Admin");
                }

            }



        }

        @Override
        public int getItemCount() {
            return messages.size();
        }



    }

    public void sendMessage(final String message){


        DocumentReference documentReference=db.collection("AllMessages").document();
        String message_id=documentReference.getId();
        Map<String,Object> message_data=new HashMap<>();
        message_data.put("message_id",message_id);
        message_data.put("sender_id",sender_id);
        message_data.put("sender_type",sender_type);
        message_data.put("receiver_id",receiver_id);
        message_data.put("receiver_type",receiver_type);
        message_data.put("message",message);
        message_data.put("time", FieldValue.serverTimestamp());

        documentReference.set(message_data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        String time=DateTimeConverter.getInstance().get_current_data_time();
        set_own_message_data(message_id,message,sender_id,receiver_id,sender_type,receiver_type,time);


    }
    public void set_own_message_data(String message_id,String message,String sender_id,String receiver_id,String sender_type,String receiver_type,String time){

        messages.add(new Messages(message_id,message,sender_id,sender_name,sender_image_path,sender_type,receiver_id,receiver_name,receiver_image_path,receiver_type,time));
        recycleAdapter.notifyDataSetChanged();
        String sender_tmp="";
        if(sender_type.equalsIgnoreCase("seller")){
            sender_tmp=getString(R.string.seller);
        }
        else if(sender_type.equalsIgnoreCase("buyer")){
            sender_tmp=getString(R.string.buyer);
        }
        else if(sender_type.equalsIgnoreCase("admin")){
            sender_tmp=getString(R.string.admin2);
        }
        NotificationSender.getInstance().createNotification(sender_tmp+" আপনাকে মেসেজ পাঠিয়েছেন।",message,sender_id,receiver_id,message_id,sender_device_id,receiver_device_id,"new message");
    }
}
