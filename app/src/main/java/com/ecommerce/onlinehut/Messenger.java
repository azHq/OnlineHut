package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messenger extends AppCompatActivity {

    TextView message_view;
    EditText et_message;
    public static RecyclerView recyclerView;
    Button send;
    Thread thread;
    public static RecycleAdapter recycleAdapter;
    TextView tv_name;
    Button back;
    CircleImageView profile_pic;
    String device_id;
    Handler handler;
    public static Context context;
    boolean isMessaging=true;
    public static ArrayList<Messages> messages=new ArrayList<>();
    public String image_path="";
    public static String sender_id="",receiver_id="",sender_type="",receiver_type="",sender_device_id="",receiver_device_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        context=getApplicationContext();
        send=findViewById(R.id.send);

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
        image_path=getIntent().getStringExtra("image_path");


        if(image_path!=null&&!image_path.equalsIgnoreCase("null")&&!image_path.equalsIgnoreCase("")){


            Picasso.get().load(image_path).placeholder(R.drawable.profile10).into(profile_pic);
        }
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
                    sendMessage(message,sender_id,receiver_id,sender_type,receiver_type,sender_device_id,receiver_device_id);
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





    }

    @Override
    protected void onPause() {
        super.onPause();

        Messenger.context=null;
    }

    public  void refresh(){
        //messages.add(new Messages("1","how are you","2","2","8","jussj","jssks","ajjaj"));
        getAllMessage();
    }



    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }


    private void getAllMessage() {


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
            TextView my_message_view,oponent_message_view;
            RelativeLayout my_layout,oponent_layout;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                my_message_view=mView.findViewById(R.id.my_message_view);
                oponent_message_view=mView.findViewById(R.id.oponent_message_view);
                my_react=mView.findViewById(R.id.my_react);
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
            if(memberInfo.sender_id.equalsIgnoreCase(SharedPrefManager.getInstance(getApplicationContext()).getUser().getUser_id())){
                holder.my_layout.setVisibility(View.VISIBLE);
                holder.my_message_view.setText(memberInfo.message);
                holder.oponent_layout.setVisibility(View.GONE);
            }
            else{
                holder.oponent_layout.setVisibility(View.VISIBLE);
                holder.oponent_message_view.setText(memberInfo.message);
                holder.my_layout.setVisibility(View.GONE);
                if(!image_path.equalsIgnoreCase("null")){


                    Picasso.get().load(image_path).placeholder(R.drawable.profile10).into(holder.oponent_profile_pic);
                }
            }



        }

        @Override
        public int getItemCount() {
            return messages.size();
        }



    }

    public void sendMessage(final String message, final String sender_id, final String receiver_id,final String sender_type,final String receiver_type,final String sender_device_id,final String receiver_device_id){



    }
}
