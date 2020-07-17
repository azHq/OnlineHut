package com.ecommerce.onlinehut.Admin.deposite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.Seller.TransactionHistoryForSeller;
import com.ecommerce.onlinehut.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.ViewAdapter>{

    ArrayList<Transaction> transactions;
    public TransactionListAdapter(ArrayList<Transaction> transactions){
        this.transactions=transactions;
    }
    @NonNull
    @Override
    public TransactionListAdapter.ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_layout,parent,false);
        return new ViewAdapter(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TransactionListAdapter.ViewAdapter holder, final int position) {

        Context context = holder.itemView.getContext();
        Transaction transaction=transactions.get(position);
        holder.item.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale));
        holder.image.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
        holder.name.setText(transaction.user_name);
        holder.id.setText(context.getResources().getString(R.string.sender_id)+" : "+transaction.user_id);
        holder.amount.setText(context.getResources().getString(R.string.money_amount)+" : "+transaction.amount);
        if(transaction.image_path!=null&&transaction.image_path.length()>5){
            Picasso.get().load(transaction.image_path).into(holder.image);
        }
        holder.phone_number.setText(context.getResources().getString(R.string.phone_number)+" : "+transaction.phone_number);
        holder.trxId.setText("trxId : "+transaction.transaction_id);
        holder.payment_method.setText(context.getResources().getString(R.string.medium)+" : "+transaction.payment_method);
        holder.time.setText(context.getResources().getString(R.string.time)+" : "+transaction.time);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }


    class ViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout item;
        ImageView image,logo;
        TextView name,id,phone_number,amount,trxId,payment_method,time;
        View mView;
        public ViewAdapter(View itemView) {
            super(itemView);
            mView=itemView;
            mView.setOnClickListener(this);
            image=mView.findViewById(R.id.image);
            name=mView.findViewById(R.id.name);
            id=mView.findViewById(R.id.id);
            phone_number=mView.findViewById(R.id.phone_number);
            amount=mView.findViewById(R.id.amount);
            payment_method=mView.findViewById(R.id.payment_method);
            time=mView.findViewById(R.id.time);
            trxId=mView.findViewById(R.id.transaction_id);
            item=mView.findViewById(R.id.item);

        }


        @Override
        public void onClick(View v) {


        }
    }
}
