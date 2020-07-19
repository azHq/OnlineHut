package com.ecommerce.onlinehut;

import java.util.Comparator;
import java.util.Date;

public class Messages implements Comparable<Messages> {
    public String id;
    public String message;
    public String sender_id;
    public String sender_name;
    public String sender_image_path;
    public String sender_type;
    public String receiver_id;
    public String receiver_name;
    public String receiver_image_path;
    public String receiver_type;
    public String time;

    public Messages(String id, String message, String sender_id, String sender_name, String sender_image_path, String sender_type, String receiver_id, String receiver_name, String receiver_image_path, String receiver_type, String time) {
        this.id = id;
        this.message = message;
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.sender_image_path = sender_image_path;
        this.sender_type = sender_type;
        this.receiver_id = receiver_id;
        this.receiver_name = receiver_name;
        this.receiver_image_path = receiver_image_path;
        this.receiver_type = receiver_type;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_type() {
        return sender_type;
    }

    public void setSender_type(String sender_type) {
        this.sender_type = sender_type;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver_type() {
        return receiver_type;
    }

    public void setReceiver_type(String receiver_type) {
        this.receiver_type = receiver_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public int compareTo(Messages o) {
        DateTimeConverter dateTimeConverter= DateTimeConverter.getInstance();
        Date date1=dateTimeConverter.convert_string_to_date_time2(time);
        Date date2=dateTimeConverter.convert_string_to_date_time2(o.time);
        return date1.compareTo(date2);
    }

}
