package com.example.agua_planeta_rica.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String code;
    private String type;
    private int quantity;
    private double totalPrice;
    private double lat;
    private double lng;
    private String detail;
    private String userPhone;
    private String username;
    private String state;
    private boolean deleted;

    public Request(String code, String type, int quantity, double totalPrice, double lat, double lng, String detail, String userPhone, String username, String state, boolean deleted) {
        this.code = code;
        this.type = type;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.lat = lat;
        this.lng = lng;
        this.detail = detail;
        this.userPhone = userPhone;
        this.username = username;
        this.state = state;
        this.deleted = deleted;
    }

    public Request() {
    }

    public String getCode() {
        return code;
    }

    public Request setCode(String code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public Request setType(String type) {
        this.type = type;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public Request setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Request setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public Request setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public Request setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public Request setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public Request setUserPhone(String userPhone) {
        this.userPhone = userPhone;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Request setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getState() {
        return state;
    }

    public Request setState(String state) {
        this.state = state;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Request setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("detail", detail);
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("quantity", quantity);
        result.put("state", state);
        result.put("totalPrice", totalPrice);
        result.put("type", type);
        result.put("username", username);
        result.put("deleted", deleted);
        return result;
    }
}
