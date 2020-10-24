package com.example.agua_planeta_rica.model;

public class Purchase {
    private String code;
    private String datetime;
    private String user;
    private double total;
    private String type;

    public Purchase(String code, String datetime, String user, double total, String type) {
        this.code = code;
        this.datetime = datetime;
        this.user = user;
        this.total = total;
        this.type = type;
    }

    public Purchase() {
    }

    public String getCode() {
        return code;
    }

    public Purchase setCode(String code) {
        this.code = code;
        return this;
    }

    public String getDatetime() {
        return datetime;
    }

    public Purchase setDatetime(String datetime) {
        this.datetime = datetime;
        return this;
    }

    public String getUser() {
        return user;
    }

    public Purchase setUser(String user) {
        this.user = user;
        return this;
    }

    public double getTotal() {
        return total;
    }

    public Purchase setTotal(double total) {
        this.total = total;
        return this;
    }

    public String getType() {
        return type;
    }

    public Purchase setType(String type) {
        this.type = type;
        return this;
    }
}
