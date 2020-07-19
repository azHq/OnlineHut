package com.ecommerce.onlinehut;

import com.google.firebase.Timestamp;

import java.util.Comparator;

public class Transaction implements Comparator<Notification> {
    public String image_path;
    public String user_name;
    public String user_id;
    public String phone_number;
    public int amount;
    public String transaction_id;
    public String animal_id;
    public String payment_method;
    public Timestamp time;
    public String time2;

    public Transaction(String image_path, String user_name, String user_id, String phone_number, int amount,String transaction_id,String animal_id, String payment_method, Timestamp time) {
        this.image_path = image_path;
        this.user_name = user_name;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.amount = amount;
        this.transaction_id=transaction_id;
        this.animal_id=animal_id;
        this.payment_method = payment_method;
        this.time = time;
    }
    public Transaction(String image_path, String user_name, String user_id, String phone_number, int amount,String transaction_id,String animal_id, String payment_method, String time2) {
        this.image_path = image_path;
        this.user_name = user_name;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.amount = amount;
        this.transaction_id=transaction_id;
        this.animal_id=animal_id;
        this.payment_method = payment_method;
        this.time2 = time2;
    }
    @Override
    public int compare(Notification o1, Notification o2) {
        return o1.time.compareTo(o2.time);
    }
}
