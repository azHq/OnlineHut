package com.ecommerce.onlinehut;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    EditText et_user_name;
    EditText et_phone_number;
    String user_name="",phone_number="",user_type="";
    Spinner sp_user_type;
    ArrayList<Integer> user_types=new ArrayList<>();
    AlertDialog alertDialog;
    TextView open_sign_in_panel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user_type=getIntent().getStringExtra("user_type");
        Log.d("SignUp",user_type);
        user_types.add(R.string.user_type);
        user_types.add(R.string.seller);
        user_types.add(R.string.seller);
        open_sign_in_panel=findViewById(R.id.open_sign_in_panel);
        et_user_name=findViewById(R.id.name);
        et_phone_number=findViewById(R.id.phone_number);
        open_sign_in_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getApplicationContext(),SignIn.class);
                tnt.putExtra("user_type",user_type);
                startActivity(tnt);
                finish();
            }
        });
    }
    public void sign_up(View view){

        user_name=et_user_name.getText().toString();
        if(user_name.length()<=0){
            show_error_dialog(R.string.input_error,R.string.name_error);
            return;
        }
        phone_number=et_phone_number.getText().toString();
        if(!isValidPhoneNumber(phone_number)){
            show_error_dialog(R.string.phone_error_title,R.string.phone_error_body);
            return;
        }

        if(user_type.length()<=0){
            show_error_dialog(R.string.input_error,R.string.type_error);
            return;
        }
        clean_country_code();
        Intent tnt=new Intent(getApplicationContext(),PinViewLayout.class);
        tnt.putExtra("user_name",user_name);
        tnt.putExtra("user_type",user_type);
        tnt.putExtra("phone_number",phone_number);
        tnt.putExtra("activity_type","sign_up");
        startActivity(tnt);
        finish();
    }
    public void clean_country_code(){

       if(phone_number.startsWith("88")) phone_number=phone_number.replace(phone_number.substring(0,2),"");
       if(phone_number.startsWith("+88")) phone_number=phone_number.replace(phone_number.substring(0,3),"");
    }

    public boolean isValidPhoneNumber( String phone_number){

        phone_number=phone_number.replace("-","");
        phone_number=phone_number.replaceAll("\\s","");
        String pattern="(^([+]{1}[8]{2}|88)?(01){1}[1-9]{1}\\d{8})$";

        return phone_number.matches(pattern);
    }
    public void show_error_dialog(int title,int body){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button btn=view.findViewById(R.id.ok);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(title);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(body);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }



    public static class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<Integer> user_types;
        LayoutInflater inflter;
        int flag;

        public CustomAdapter(Context applicationContext, int flag, ArrayList<Integer> user_types) {
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
            view = inflter.inflate(R.layout.user_type_layout, null);
            CircleImageView circleImageView = view.findViewById(R.id.user_icon);
            final TextView names =view.findViewById(R.id.user_type);
            names.setText(user_types.get(i));

            if (i==1) {

                circleImageView.setImageResource(R.drawable.buyer);
            }
            else if(i==2){
                circleImageView.setImageResource(R.drawable.seller);
            }
            else{
                circleImageView.setVisibility(View.GONE);
            }

            return view;
        }
    }
}
