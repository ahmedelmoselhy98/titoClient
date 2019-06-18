package com.OrxtraDev.TitoApp.model;

public class WalletModel {


    private String tripId ="";
    private String driverId  = "";
    private String clientId = "";
    private String price = "";
    private String paidPrice = "";
    private String time = "";

    public WalletModel() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String id) {
        this.tripId = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(String paidPrice) {
        this.paidPrice = paidPrice;
    }
}
