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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "user_name='" + user_name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", amount=" + amount +
                ", transaction_id='" + transaction_id + '\'' +
                ", payment_method='" + payment_method + '\'' +
                ", time='" + time + '\'' +
                '}';
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
