package com.ecommerce.onlinehut.Buyer;

public class PriceHistoryItem {
    public String seller_id;
    public String seller_name;
    public String seller_location;
    public String buyer_id;
    public String buyer_name;
    public String buyer_location;
    public String animal_id;
    public String price;
    public String time;

    public PriceHistoryItem(String seller_id, String seller_name, String seller_location, String buyer_id, String buyer_name, String buyer_location, String animal_id, String price, String time) {
        this.seller_id = seller_id;
        this.seller_name = seller_name;
        this.seller_location = seller_location;
        this.buyer_id = buyer_id;
        this.buyer_name = buyer_name;
        this.buyer_location = buyer_location;
        this.animal_id = animal_id;
        this.price = price;
        this.time = time;
    }
}
