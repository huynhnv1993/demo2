package com.example.windows10gamer.demo2.model;

/**
 * Created by Windows 10 Gamer on 10/06/2017.
 */

public class Cart {
    public int product_id;
    public String name;
    public int list_price;
    public String image;
    public int qty;

    public Cart(int product_id, String name, int list_price, String image, int qty) {
        this.product_id = product_id;
        this.name = name;
        this.list_price = list_price;
        this.image = image;
        this.qty = qty;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getList_price() {
        return list_price;
    }

    public void setList_price(int list_price) {
        this.list_price = list_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
