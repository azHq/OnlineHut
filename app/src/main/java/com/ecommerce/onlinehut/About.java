package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class About extends Fragment {

    public String document_id="",seller_id="",seller_device_id,user_id;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView mission_tv,slogan_tv;
    CardView referral_code_card_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_about, container, false);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait....");
        mission_tv=view.findViewById(R.id.mission);
        slogan_tv=view.findViewById(R.id.slogan);
        db=FirebaseFirestore.getInstance();
        get_AppConfigurationData();
        return view;
    }
    public void get_AppConfigurationData(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AppConfiguration").document("AboutUs");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){
                    progressDialog.dismiss();
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists())
                    {
                        Map<String,Object> data=documentSnapshot.getData();
                        String slogan=data.get("about_us").toString();
                        String our_mission=data.get("our_mission").toString();
                        mission_tv.setText(our_mission);
                        slogan_tv.setText(slogan);

                    }

                }
            }
        });
    }
}
