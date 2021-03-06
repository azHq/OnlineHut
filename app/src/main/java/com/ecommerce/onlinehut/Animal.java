package com.ecommerce.onlinehut;

public class Animal implements Comparable<Animal> {
    public String animal_id;
    public String animal_alt_id;
    public String user_id;
    public String buyer_id;
    public String sold_status;
    public int sold_price;
    public int payment_complete;
    public int charge;
    public String animal_type;
    public String name;
    public int price;
    public String time;
    public float age;
    public String color;
    public float weight;
    public float height;
    public int teeth;
    public String born;
    public String image_path;
    public String video_path;
    public int highest_bid;
    public int total_bid;
    public String compress_image_path;
    public String seller_name;
    public String seller_location;
    public String seller_device_id;
    public Animal(String animal_id,String animal_alt_id,String user_id, String name, int price, float age, String color, float weight, float height, int teeth, String born, String image_path, String video_path, int highest_bid, int total_bid) {
        this.animal_id = animal_id;
        this.animal_type=animal_type;
        this.animal_alt_id=animal_alt_id;
        this.user_id=user_id;
        this.name = name;
        this.price = price;
        this.age = age;
        this.color = color;
        this.weight = weight;
        this.height = height;
        this.teeth = teeth;
        this.born = born;
        this.image_path = image_path;
        this.video_path = video_path;
        this.highest_bid = highest_bid;
        this.total_bid = total_bid;
    }
    public Animal(String animal_id,String animal_type,String animal_alt_id,String user_id, String name, int price, float age, String color, float weight, float height, int teeth, String born, String image_path, String video_path, int highest_bid, int total_bid) {
        this.animal_id = animal_id;
        this.animal_type=animal_type;
        this.animal_alt_id=animal_alt_id;
        this.user_id=user_id;
        this.name = name;
        this.price = price;
        this.age = age;
        this.color = color;
        this.weight = weight;
        this.height = height;
        this.teeth = teeth;
        this.born = born;
        this.image_path = image_path;
        this.video_path = video_path;
        this.highest_bid = highest_bid;
        this.total_bid = total_bid;
    }
    public Animal(String animal_id,String animal_type,String animal_alt_id,String user_id,String sold_status,int sold_price,String name, int price, float age, String color, float weight, float height, int teeth, String born, String image_path, String video_path, int highest_bid, int total_bid) {
        this.animal_id = animal_id;
        this.animal_type=animal_type;
        this.animal_alt_id=animal_alt_id;
        this.user_id=user_id;
        this.sold_status = sold_status;
        this.sold_price=sold_price;
        this.name = name;
        this.price = price;
        this.age = age;
        this.color = color;
        this.weight = weight;
        this.height = height;
        this.teeth = teeth;
        this.born = born;
        this.image_path = image_path;
        this.video_path = video_path;
        this.highest_bid = highest_bid;
        this.total_bid = total_bid;
    }

    public Animal(String animal_id,String animal_alt_id, String user_id, String animal_type, String name, int price, float age, String color, float weight, float height, int teeth, String born, String image_path,String compress_image_path, String video_path, int highest_bid, int total_bid) {
        this.animal_id = animal_id;
        this.animal_alt_id=animal_alt_id;
        this.user_id = user_id;
        this.animal_type = animal_type;
        this.name = name;
        this.price = price;
        this.age = age;
        this.color = color;
        this.weight = weight;
        this.height = height;
        this.teeth = teeth;
        this.born = born;
        this.image_path = image_path;
        this.compress_image_path=compress_image_path;
        this.video_path = video_path;
        this.highest_bid = highest_bid;
        this.total_bid = total_bid;
    }

    public Animal(String animal_id,String animal_alt_id,String user_id, String buyer_id, String sold_status,int sold_price,int payment_complete,int charge,String time, String animal_type, String name, int price, float age, String color, float weight, float height, int teeth, String born, String image_path, String video_path, int highest_bid, int total_bid, String compress_image_path) {
        this.animal_id = animal_id;
        this.animal_alt_id=animal_alt_id;
        this.user_id = user_id;
        this.buyer_id = buyer_id;
        this.sold_status = sold_status;
        this.sold_price=sold_price;
        this.payment_complete=payment_complete;
        this.charge=charge;
        this.time=time;
        this.animal_type = animal_type;
        this.name = name;
        this.price = price;
        this.age = age;
        this.color = color;
        this.weight = weight;
        this.height = height;
        this.teeth = teeth;
        this.born = born;
        this.image_path = image_path;
        this.video_path = video_path;
        this.highest_bid = highest_bid;
        this.total_bid = total_bid;
        this.compress_image_path = compress_image_path;
    }

    public String getId() {
        return animal_id;
    }

    public void setId(String animal_id) {
        this.animal_id = animal_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getAge() {
        return age;
    }

    public void setAge(float age) {
        this.age = age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTeeth() {
        return teeth;
    }

    public void setTeeth(int teeth) {
        this.teeth = teeth;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getVideo_path() {
        return video_path;
    }

    public void setVideo_path(String video_path) {
        this.video_path = video_path;
    }

    public double getHighest_bid() {
        return highest_bid;
    }

    public void setHighest_bid(int highest_bid) {
        this.highest_bid = highest_bid;
    }

    public int getTotal_bid() {
        return total_bid;
    }

    public void setTotal_bid(int total_bid) {
        this.total_bid = total_bid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getSold_status() {
        return sold_status;
    }

    public void setSold_status(String sold_status) {
        this.sold_status = sold_status;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getSeller_location() {
        return seller_location;
    }

    public String getSeller_device_id() {
        return seller_device_id;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public void setSeller_location(String seller_location) {
        this.seller_location = seller_location;
    }

    public void setSeller_device_id(String seller_device_id) {
        this.seller_device_id = seller_device_id;
    }

    @Override
    public int compareTo(Animal o) {
        int id1=Integer.parseInt(animal_alt_id);
        int id2=Integer.parseInt(o.animal_alt_id);
        if(id1>id2){

            return 1;
        }
        else return -1;
    }
}
