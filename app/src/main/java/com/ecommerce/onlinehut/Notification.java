package com.ecommerce.onlinehut;


import com.google.firebase.Timestamp;

import java.util.Comparator;
import java.util.Date;

public class Notification implements Comparable<Notification> {
    public String id;
    public String title;
    public String body;
    public String sender_id;
    public String sender_name;
    public String sender_image_path;
    public String sender_device_id;
    public String sender_location;
    public String receiver_id;
    public String receiver_device_id;
    public String status;
    public String document_id;
    public String time;
    public String sender_type;
    public String activity_type;

    public Notification(String id, String title, String body, String sender_id, String sender_name, String sender_image_path, String sender_device_id, String sender_location, String receiver_id, String receiver_device_id, String status, String document_id, String time, String sender_type,String activity_type) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.sender_image_path = sender_image_path;
        this.sender_device_id = sender_device_id;
        this.sender_location = sender_location;
        this.receiver_id = receiver_id;
        this.receiver_device_id = receiver_device_id;
        this.status = status;
        this.document_id = document_id;
        this.time = time;
        this.sender_type = sender_type;
        this.activity_type=activity_type;
    }

    @Override
    public int compareTo(Notification o) {
        DateTimeConverter dateTimeConverter= DateTimeConverter.getInstance();
        Date date1=dateTimeConverter.convert_string_to_date_time(time);
        Date date2=dateTimeConverter.convert_string_to_date_time(o.time);

        System.out.println("date1:"+date1);
        System.out.println("date2:"+date2);
        System.out.println("result:"+date1.compareTo(date2));
        return date1.compareTo(date2)*-1;
    }
}
