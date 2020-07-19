package com.ecommerce.onlinehut.Buyer;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class TransactionHistoryForBuyer extends Fragment {
    ArrayList<Transaction> transactions=new ArrayList<Transaction>();
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
        View view=inflater.inflate(R.layout.fragment_transaction_history_for_buyer, container, false);
        firebaseAuth= FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db= FirebaseFirestore.getInstance();
        recyclerView=view.findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(transactions);
        recyclerView.setAdapter(recycleAdapter);
        empty=view.findViewById(R.id.empty);
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Please Wait");
        get_transaction_history_data();
        return view;
    }
    public void get_transaction_history_data()
    {
        Query query=db.collection("Transaction").whereEqualTo("receiver_id",user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                QuerySnapshot queryDocumentSnapshots=task.getResult();
                if(queryDocumentSnapshots.size()>0){
                    transactions.clear();
                    for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                        Map<String,Object> data=documentSnapshot.getData();
                        String image_path=data.get("image_path").toString();
                        String sender_name=data.get("sender_name").toString();
                        String sender_id=data.get("sender_id").toString();
                        String phone_number=data.get("phone_number").toString();
                        int amount=Integer.parseInt(data.get("amount").toString());
                        String trxId=data.get("transaction_id").toString();
                        String animal_id=data.get("animal_id").toString();
                        String payment_method=data.get("payment_method").toString();
                        Timestamp time= (Timestamp)data.get("sold_time");
                        transactions.add(new Transaction(image_path,sender_name,sender_id,phone_number,amount,trxId,animal_id,payment_method,time));
                    }
                    recycleAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
                else{
//                    recyclerView.setVisibility(View.GONE);
//                    empty.setVisibility(View.VISIBLE);

                    //should remove
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);

                    Date date=new Date();
                    date.getTime();
                    for(int i=0;i<10;i++){
                        transactions.add(new Transaction("","Azazul","123456","01795528283",1000,"1234","12345","Bkash","12-12-2020"));
                    }

                    recycleAdapter.notifyDataSetChanged();

                }

            }
        });
    }
    public static String toDateStr(long milliseconds)
    {
        String format="dd-MM-yyyy hh:mm aa";
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<Transaction> transactions;
        public RecycleAdapter(ArrayList<Transaction> transactions){
            this.transactions=transactions;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

            LinearLayout item;
            ImageView image,logo;
            TextView name,id,phone_number,amount,trxId,animal_id,payment_method,time;
            View mView;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                mView.setOnClickListener(this);
                image=mView.findViewById(R.id.image);
                name=mView.findViewById(R.id.name);
                id=mView.findViewById(R.id.id);
                logo=mView.findViewById(R.id.logo);
                phone_number=mView.findViewById(R.id.phone_number);
                amount=mView.findViewById(R.id.amount);
                payment_method=mView.findViewById(R.id.payment_method);
                time=mView.findViewById(R.id.time);
                trxId=mView.findViewById(R.id.transaction_id);
                item=mView.findViewById(R.id.item);
                animal_id=mView.findViewById(R.id.animal_id);

            }


            @Override
            public void onClick(View v) {


            }
        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getContext()).inflate(R.layout.transaction_item_layout,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            Transaction transaction=transactions.get(position);
            holder.item.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_scale));
            holder.image.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade_transition_animation));
            holder.name.setText(transaction.user_name);
            holder.id.setText(getString(R.string.sender_id)+" : "+transaction.user_id);
            holder.animal_id.setText(getString(R.string.id)+" : A-"+transaction.animal_id);
            holder.amount.setText(getString(R.string.money_amount)+" : "+transaction.amount);
            if(transaction.image_path!=null&&transaction.image_path.length()>5){
                Picasso.get().load(transaction.image_path).into(holder.image);
            }
            holder.trxId.setText(transaction.transaction_id);
            if(transaction.payment_method.equalsIgnoreCase("bkash")){
                holder.logo.setImageResource(R.drawable.bkash_icon);
            }
            else if(transaction.payment_method.equalsIgnoreCase("rocket")){
                holder.logo.setImageResource(R.drawable.rocket_icon);
            }
            else if(transaction.payment_method.equalsIgnoreCase("nagad")){
                holder.logo.setImageResource(R.drawable.nagad_icon);
            }
            holder.phone_number.setText(getString(R.string.phone_number)+" : "+transaction.phone_number);
            holder.trxId.setText("trxId : "+transaction.transaction_id);
            holder.payment_method.setText(getString(R.string.medium)+" : "+transaction.payment_method);
            holder.time.setText(getString(R.string.time)+" : "+transaction.time);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

    }
}
