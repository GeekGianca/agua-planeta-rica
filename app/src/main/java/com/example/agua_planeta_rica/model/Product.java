package com.example.agua_planeta_rica.model;

public class Product {
    private int code;
    private String name;
    private double price;

    public Product(int code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public Product() {
    }

    public int getCode() {
        return code;
    }

    public Product setCode(int code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    @Override
    public String toString() {
        return "Product{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
