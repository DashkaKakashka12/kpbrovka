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
    public String description;
    public boolean isActive;
    public String userId;
    public Timestamp dataCreation;
    public Timestamp dataEdit;
    public Coordinates coordinates;


    public Hotel(String id, List<String> facilities, String adress, String hotelName, String photos, String description, boolean isActive, String userId, Timestamp dataCreation, Timestamp dataEdit, Coordinates coordinates) {
        this.id = id;
        this.facilities = facilities;
        this.adress = adress;
        this.hotelName = hotelName;
        this.photos = photos;
        this.description = description;
        this.isActive = isActive;
        this.userId = userId;
        this.dataCreation = dataCreation;
        this.dataEdit = dataEdit;
        this.coordinates = coordinates;
    }

    public Hotel() {
    }
}
