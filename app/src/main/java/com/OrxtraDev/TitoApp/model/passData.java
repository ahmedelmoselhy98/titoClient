package com.OrxtraDev.TitoApp.model;

import java.io.Serializable;

/**
 * Created by hazemhabeb on 9/27/18.
 */

public class passData implements Serializable {
    private String text;
    private double clat;
    private double clang;
    private double tolat;
    private double tolang;


    public passData() {
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public double getClat() {
        return clat;
    }

    public void setClat(double clat) {
        this.clat = clat;
    }

    public double getClang() {
        return clang;
    }

    public void setClang(double clang) {
        this.clang = clang;
    }

    public double getTolat() {
        return tolat;
    }

    public void setTolat(double tolat) {
        this.tolat = tolat;
    }

    public double getTolang() {
        return tolang;
    }

    public void setTolang(double tolang) {
        this.tolang = tolang;
    }
}
