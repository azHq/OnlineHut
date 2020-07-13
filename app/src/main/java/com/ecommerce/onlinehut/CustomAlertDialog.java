package com.ecommerce.onlinehut;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomAlertDialog {

    AlertDialog alertDialog;
    private static CustomAlertDialog customAlertDialog=new CustomAlertDialog();
    private CustomAlertDialog(){

    }
    public static CustomAlertDialog getInstance(){

        if(customAlertDialog==null) customAlertDialog=new CustomAlertDialog();
        return customAlertDialog;
    }
    public void show_exit_dialog(Context context){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.exit_panel,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button yes=view.findViewById(R.id.yes);
        Button no=view.findViewById(R.id.no);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(R.string.app_name);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(R.string.are_you_sure_to_exit);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ((Activity)context).finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    public void show_error_dialog(Context context,String title,String body){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.connection_error_layout,null);
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
}
