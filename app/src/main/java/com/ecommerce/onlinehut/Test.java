package com.ecommerce.onlinehut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Test extends AppCompatActivity {
    ArrayList<Animal> animals=new ArrayList<Animal>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

    }
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<Animal> animals;
        public RecycleAdapter(ArrayList<Animal> animals){
            this.animals=animals;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder{

            View mView;
            Button compare,details,pricing;
            CardView card1,card2;
            ImageView animal_image1,animal_image2;
            TextView name_tv1,name_tv2,price_tv1,price_tv2,highest_bid_tv1,highest_bid_tv2,total_bid_tv1,total_bid_tv2;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                animal_image1=mView.findViewById(R.id.animal_image1);
                name_tv1=mView.findViewById(R.id.name1);
                price_tv1=mView.findViewById(R.id.price1);
                highest_bid_tv1=mView.findViewById(R.id.highest_bid1);
                total_bid_tv1=mView.findViewById(R.id.total_bid1);
                compare=mView.findViewById(R.id.compare);
                details=mView.findViewById(R.id.details);
                pricing=mView.findViewById(R.id.pricing);
                card1=mView.findViewById(R.id.card1);
            }


        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.animal_list_item_for_buyer,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {

            Animal animal=animals.get(position);
            holder.card1.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale));
            holder.animal_image1.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));
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
        }

        @Override
        public int getItemCount() {
            return animals.size();
        }



    }
}
