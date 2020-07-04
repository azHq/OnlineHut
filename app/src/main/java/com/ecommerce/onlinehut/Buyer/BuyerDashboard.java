package com.ecommerce.onlinehut.Buyer;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.SelectUserType;
import com.ecommerce.onlinehut.Seller.SellerDashboard;
import com.ecommerce.onlinehut.SignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);
        menu=findViewById(R.id.menu);
        menu2=findViewById(R.id.menu_icon2);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                    finish();
                    startActivity(new Intent(getApplicationContext(), SelectUserType.class));
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
    public void upload_location(GeoPoint geoPoint){

        if(db!=null&&firebaseUser!=null){

            Map<String,GeoPoint> map=new HashMap<>();
            map.put("loaction",geoPoint);
            db.collection("clients").document(firebaseUser.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }

}
