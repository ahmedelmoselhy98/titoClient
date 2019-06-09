package com.OrxtraDev.TitoApp.model;

public class CategLocal {
    private int image = 0;
    private String title ="";
    private String subTitle ="";
    private String id ="";

    public CategLocal() {
    }


    public CategLocal(int image, String title, String subTitle, String id) {
        this.image = image;
        this.title = title;
        this.subTitle = subTitle;
        this.id = id;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
