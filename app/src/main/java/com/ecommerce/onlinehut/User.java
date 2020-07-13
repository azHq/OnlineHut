package com.ecommerce.onlinehut;

public class User {
    public String user_id;
    public String user_name;
    public String user_type;
    public String phone_number;
    public String image_path;
    public String device_id;
    public String location;

    public User(String user_id, String user_name, String user_type, String phone_number, String image_path, String device_id, String location) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_type = user_type;
        this.phone_number = phone_number;
        this.image_path = image_path;
        this.device_id = device_id;
        this.location = location;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
