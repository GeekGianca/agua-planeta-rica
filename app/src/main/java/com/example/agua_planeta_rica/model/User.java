package com.example.agua_planeta_rica.model;

public class User {
    private String identification;
    private String name;
    private String direction;
    private String phone;
    private String street;
    private String description;
    private double lat;
    private double lng;
    private String email;
    private String password;
    private int role;

    public User(String identification, String name, String direction, String phone, String street, String description, double lat, double lng, String email, String password) {
        this.identification = identification;
        this.name = name;
        this.direction = direction;
        this.phone = phone;
        this.street = street;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public String getIdentification() {
        return identification;
    }

    public User setIdentification(String identification) {
        this.identification = identification;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getDirection() {
        return direction;
    }

    public User setDirection(String direction) {
        this.direction = direction;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public User setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public User setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public User setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public User setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getRole() {
        return role;
    }

    public User setRole(int role) {
        this.role = role;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "identification='" + identification + '\'' +
                ", name='" + name + '\'' +
                ", direction='" + direction + '\'' +
                ", phone='" + phone + '\'' +
                ", street='" + street + '\'' +
                ", description='" + description + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
