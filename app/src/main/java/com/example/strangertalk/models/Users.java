package com.example.strangertalk.models;

public class Users {
    public Users(){}
    private String uID;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }

    private String name;
    private String profile;
    private String city;
    private long coins;

    public Users(String uID, String name, String profile, String city, long coins) {
        this.uID = uID;
        this.name = name;
        this.profile = profile;
        this.city = city;
        this.coins = coins;
    }
}
