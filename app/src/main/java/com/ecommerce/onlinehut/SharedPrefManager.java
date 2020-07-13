package com.ecommerce.onlinehut;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {


    private static final String SHARED_PREF_NAME = "USER_INFO";
    private static final String USER_ID= "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_TYPE= "USER_TYPE";
    private static final String PHONE_NUMBER= "PHONE_NUMBER";
    private static final String IMAGE_PATH= "IMAGE_PATH";
    private static final String DEVICE_ID="DEVICE_ID";
    private static final String LOCATION="LOCATION";
    private static final String GEOPOINT="GEOPOINT";
    private static final String NUMBER_OF_UNSEEN_NOTIFICATION="NUMBER_OF_UNSEEN_NOTIFICATION";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }


    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, user.getUser_id());
        editor.putString(USER_NAME,user.getUser_name());
        editor.putString(USER_TYPE, user.getUser_type());
        editor.putString(PHONE_NUMBER, user.getPhone_number());
        editor.putString(IMAGE_PATH, user.getImage_path());
        editor.putString(DEVICE_ID, user.getDevice_id());
        editor.putString(LOCATION, user.getLocation());
        editor.apply();
    }
    public void increase_unseen_notification() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NUMBER_OF_UNSEEN_NOTIFICATION,get_unseen_notification()+1);
        editor.apply();
    }
    public int get_unseen_notification() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int count=sharedPreferences.getInt(NUMBER_OF_UNSEEN_NOTIFICATION,0);
        return count;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID, null) != null;
    }


    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(USER_ID, null),
                sharedPreferences.getString(USER_NAME,null),
                sharedPreferences.getString(USER_TYPE, null),
                sharedPreferences.getString(PHONE_NUMBER, null),
                sharedPreferences.getString(IMAGE_PATH,null),
                sharedPreferences.getString(DEVICE_ID,null),
                sharedPreferences.getString(LOCATION,null)
        );
    }

    public void changeDeviceId(String device_id){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVICE_ID,device_id);
    }


    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}