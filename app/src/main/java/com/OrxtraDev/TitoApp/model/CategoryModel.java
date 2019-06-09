package com.OrxtraDev.TitoApp.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    String id="",name="",image="";

    double openPrice=0,kiloPrice=0,minutePrice=0,minimumPrice=0,companyPercent=0;
    public CategoryModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getKiloPrice() {
        return kiloPrice;
    }

    public void setKiloPrice(double kiloPrice) {
        this.kiloPrice = kiloPrice;
    }

    public double getMinutePrice() {
        return minutePrice;
    }

    public void setMinutePrice(double minutePrice) {
        this.minutePrice = minutePrice;
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public double getCompanyPercent() {
        return companyPercent;
    }

    public void setCompanyPercent(double companyPercent) {
        this.companyPercent = companyPercent;
    }
}
