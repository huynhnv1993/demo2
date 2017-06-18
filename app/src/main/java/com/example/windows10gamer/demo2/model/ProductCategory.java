package com.example.windows10gamer.demo2.model;

/**
 * Created by Windows 10 Gamer on 06/06/2017.
 */

public class ProductCategory {
    public int id;
    public String name;
    public String image;

    public ProductCategory(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
