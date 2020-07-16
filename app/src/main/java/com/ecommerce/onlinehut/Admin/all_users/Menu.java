package com.ecommerce.onlinehut.Admin.all_users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Menu {
    public static void viewProfile(User user, Context context) {
        TextView name, phone, email, address, userID;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the custom layout
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.dialog_view_profile, null);
        name = customLayout.findViewById(R.id.name);
        phone = customLayout.findViewById(R.id.phone_number);
        email = customLayout.findViewById(R.id.email);
        address = customLayout.findViewById(R.id.address);
        userID = customLayout.findViewById(R.id.userID);

        name.setText(user.getUser_name());
        phone.setText(user.getPhone_number());
        email.setText("Email Here");
        address.setText("Address here");
        userID.setText(user.getUser_id());

        builder.setView(customLayout);
        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.rinho);
        dialog.show();
    }

    public static void deleteProfile(User u, Context context) {
        Toast.makeText(context, "Deletion not supported", Toast.LENGTH_SHORT).show();
    }

    public static void disableProfile(User u, int pos, UsersListAdapter usersListAdapter, AlertDialog alertDialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap();
        map.put("disabled", true);
        db.collection("Users").document(u.getUser_id()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                alertDialog.dismiss();
                u.setDisabled(true);
                usersListAdapter.notifyItemChanged(pos);
            }
        });
    }

    public static void enableProfile(User u, int pos, UsersListAdapter usersListAdapter, AlertDialog alertDialog) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap();
        map.put("disabled", false);
        db.collection("Users").document(u.getUser_id()).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                alertDialog.dismiss();
                u.setDisabled(false);
                usersListAdapter.notifyItemChanged(pos);
            }
        });
    }
}
