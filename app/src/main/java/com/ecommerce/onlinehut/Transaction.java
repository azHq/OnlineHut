package com.ecommerce.onlinehut;

public class Transaction {
    public String image_path;
    public String user_name;
    public String user_id;
    public String phone_number;
    public int amount;
    public String transaction_id;
    public String payment_method;
    public String time;

    public Transaction(String image_path, String user_name, String user_id, String phone_number, int amount,String transaction_id, String payment_method, String time) {
        this.image_path = image_path;
        this.user_name = user_name;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.amount = amount;
        this.transaction_id=transaction_id;
        this.payment_method = payment_method;
        this.time = time;
    }
}
