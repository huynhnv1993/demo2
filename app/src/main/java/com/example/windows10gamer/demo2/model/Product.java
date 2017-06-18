package com.example.windows10gamer.demo2.model;

import java.io.Serializable;

/**
 * Created by Windows 10 Gamer on 06/06/2017.
 */

public class Product implements Serializable {
    public int product_id;
    public String name;
    public int list_price;
    public String description_sale;
    public String website_description;
    public String image;
    public int warranty;
    public String active;
    public String sale_ok;

    public Product(int product_id, String name, int list_price, String description_sale, String website_description, String image, int warranty, String active, String sale_ok) {
        this.product_id = product_id;
        this.name = name;
        this.list_price = list_price;
        this.description_sale = description_sale;
        this.website_description = website_description;
        this.image = image;
        this.warranty = warranty;
        this.active = active;
        this.sale_ok = sale_ok;
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

    public String getDescription_sale() {
        return description_sale;
    }

    public void setDescription_sale(String description_sale) {
        this.description_sale = description_sale;
    }

    public String getWebsite_description() {
        return website_description;
    }

    public void setWebsite_description(String website_description) {
        this.website_description = website_description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getSale_ok() {
        return sale_ok;
    }

    public void setSale_ok(String sale_ok) {
        this.sale_ok = sale_ok;
    }
}
