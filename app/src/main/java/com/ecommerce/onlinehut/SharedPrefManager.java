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
    private static final String IS_ADMIN="IS_ADMIN";
    private static final String GEOPOINT="GEOPOINT";
    private static final String IS_DISABLED = "DISABLED";


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
        editor.putBoolean(IS_ADMIN, user.isAdmin());
        editor.putBoolean(IS_DISABLED, user.isDisabled());
        editor.apply();
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
                sharedPreferences.getBoolean(IS_ADMIN, false),
                sharedPreferences.getBoolean(IS_DISABLED, false)
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