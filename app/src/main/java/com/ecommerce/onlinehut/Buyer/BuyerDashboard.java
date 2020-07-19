package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.About;
import com.ecommerce.onlinehut.AllNotifications;
import com.ecommerce.onlinehut.AnimationFactory;
import com.ecommerce.onlinehut.DisabledActivity;
import com.ecommerce.onlinehut.CustomAlertDialog;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.SelectUserType;
import com.ecommerce.onlinehut.Seller.All_Animals_For_Seller;
import com.ecommerce.onlinehut.Seller.ContactForSeller;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.ecommerce.onlinehut.Seller.SellerProfile;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.ecommerce.onlinehut.SignIn;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="MAin";
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    GeoPoint geoPoint;
    DrawerLayout drawer;
    ImageView menu,menu2;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    TextView user_name_tv;
    CircleImageView profile_picture;
    public ActionBar actionBar;
    String image_path="";
    ImageView[] indicators=new ImageView[7];
    public TextView title_tv;
    public static EditText search_et;
    Button back_btn,search_btn;
    public String user_id="";
    public static TextView message_unseen;
    ImageView notification_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);
        checkDisabled();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        notification_btn=findViewById(R.id.notification_btn);
        message_unseen=findViewById(R.id.message_unseen);
        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        user_name_tv=findViewById(R.id.user_name);
        profile_picture=findViewById(R.id.profile_picture);
        title_tv=findViewById(R.id.title);
        image_path= SharedPrefManager.getInstance(getApplicationContext()).getUser().image_path;
        user_id=SharedPrefManager.getInstance(getApplicationContext()).getUser().user_id;
        if(image_path!=null&&image_path.length()>5){
            Picasso.get().load(image_path).into(profile_picture);
        }

        indicators[0]=findViewById(R.id.profile_active);
        indicators[1]=findViewById(R.id.total_item_active);
        indicators[2]=findViewById(R.id.total_sold_item_active);
        indicators[3]=findViewById(R.id.total_unsold_item_active);
        indicators[4]=findViewById(R.id.transaction_history_active);
        indicators[5]=findViewById(R.id.contact_active);
        indicators[6]=findViewById(R.id.about_active);
        user_name_tv.setText(SharedPrefManager.getInstance(getApplicationContext()).getUser().user_name);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        menu=findViewById(R.id.menu);
        menu2=findViewById(R.id.menu_icon2);
        search_btn=findViewById(R.id.search_btn);
        title_tv=findViewById(R.id.title_bar);
        search_et=findViewById(R.id.search_et);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_et.setVisibility(View.VISIBLE);
                title_tv.setVisibility(View.GONE);
                menu2.setVisibility(View.GONE);
                notification_btn.setVisibility(View.GONE);
                notification_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                menu2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                search_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                title_tv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                Animation animation= AnimationFactory.getInstance().right_to_left_scale_anim();
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
                                search_et.setText("");
                                search_et.setVisibility(View.GONE);
                                title_tv.setVisibility(View.VISIBLE);
                                menu2.setVisibility(View.VISIBLE);
                                notification_btn.setVisibility(View.VISIBLE);
                                notification_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                menu2.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                search_btn.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                                title_tv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
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

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopup(menu2);

            }
        });
        frameLayout=findViewById(R.id.frame_layout);
        RelativeLayout profile=findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title_tv.setText(R.string.profile);
                active_indicator(0);
                search_btn.setVisibility(View.GONE);
                changeFragmentView(new BuyerProfile());

            }
        });
        RelativeLayout all_cows=findViewById(R.id.all_cows);
        all_cows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.VISIBLE);
                title_tv.setText(R.string.all_animals);
                active_indicator(1);
                changeFragmentView(new All_Animals_For_Buyer());
            }
        });
        RelativeLayout all_subject=findViewById(R.id.buy_item);
        all_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.VISIBLE);
                title_tv.setText(R.string.buy_item_list);
                active_indicator(2);
                changeFragmentView(new BuyItemHistory());
            }
        });
        RelativeLayout my_classes=findViewById(R.id.payment);
        my_classes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.GONE);
                title_tv.setText(R.string.payment);
                active_indicator(3);
                changeFragmentView(new Payment());
            }
        });

        RelativeLayout teachers=findViewById(R.id.transaction_history);
        teachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.GONE);
                title_tv.setText(R.string.transaction_history);
                active_indicator(4);
                changeFragmentView(new TransactionHistoryForBuyer());
            }
        });
        RelativeLayout pay_fees=findViewById(R.id.contact);
        pay_fees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                search_btn.setVisibility(View.VISIBLE);
                title_tv.setText(R.string.contact);
                active_indicator(5);
                changeFragmentView(new ContactForSeller());
            }
        });
        RelativeLayout notice=findViewById(R.id.about);
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_btn.setVisibility(View.GONE);
                title_tv.setText(R.string.about);
                active_indicator(6);
                changeFragmentView(new About());
            }
        });

        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tnt=new Intent(getApplicationContext(), AllNotifications.class);
                startActivity(tnt);
            }
        });


        title_tv.setText(R.string.all_animals);
        active_indicator(1);
        changeFragmentView(new All_Animals_For_Buyer());

    }
    @Override
    protected void onResume() {
        super.onResume();
        get_all_notifications();
    }
    public void active_indicator(int index){
        for(int i=0;i<indicators.length;i++){
            if(i==index){
                indicators[i].setVisibility(View.VISIBLE);
            }
            else
            {
                indicators[i].setVisibility(View.INVISIBLE);
            }
        }
    }
    public void changeFragmentView(Fragment fragment){

        fragmentManager =getSupportFragmentManager();
        fragmentManager.popBackStack();
        int count = fragmentManager.getBackStackEntryCount();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment)
                .addToBackStack(null).commit();
        drawer.closeDrawer(GravityCompat.START);
    }
    public void get_all_notifications(){
        Query documentReference=db.collection("AllNotifications").whereEqualTo("receiver_id",user_id).whereEqualTo("seen_status","unseen");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){

                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot!=null&&querySnapshot.size()>0){

                        message_unseen.setVisibility(View.VISIBLE);
                        if(querySnapshot.size()<100){
                            message_unseen.setText(querySnapshot.size()+"");
                        }
                        else{
                            message_unseen.setText("99+");
                        }
                    }
                    else{
                        message_unseen.setVisibility(View.GONE);

                    }

                }


            }


        });
    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(BuyerDashboard.this, view);

        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.user_dashboard_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.log_out){
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(getApplicationContext(), SelectUserType.class));
                    finish();
                }
                else if(item.getItemId()==R.id.settings){

                    Toast.makeText(getApplicationContext(),"Settings",Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });
        popup.show();
    }






    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        message_unseen=null;
    }

    @Override
    public void onBackPressed() {

        int count = fragmentManager.getBackStackEntryCount();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(count==1){

            CustomAlertDialog.getInstance().show_exit_dialog(BuyerDashboard.this);
        }
        else {

            super.onBackPressed();
        }
    }

    private void checkDisabled() {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> map=documentSnapshot.getData();
                if(map.containsKey("disabled"))
                    if((Boolean) map.get("disabled")){
                        startActivity(new Intent(getApplicationContext(), DisabledActivity.class));
                        finish();
                    }
            }
        });
    }

}
