package com.mgke.kpbrovka.model;



import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;


public class Hotel {
    public String id;

    public List<String> facilities = new ArrayList<>();
    public String adress;
    public String hotelName;
    public String photos;
    public boolean isActive;
    public String userId;
    public Coordinates coordinates;
    public String city;


    public Hotel(String id, List<String> facilities, String adress, String hotelName, String photos, boolean isActive, String userId, Coordinates coordinates, String city) {
        this.id = id;
        this.facilities = facilities;
        this.adress = adress;
        this.hotelName = hotelName;
        this.photos = photos;
        this.isActive = isActive;
        this.userId = userId;
        this.coordinates = coordinates;
        this.city = city;
    }

    public Hotel() {
    }
}
