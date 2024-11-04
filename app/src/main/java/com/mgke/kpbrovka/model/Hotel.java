package com.mgke.kpbrovka.model;



import com.google.firebase.Timestamp;



public class Hotel {
    public String id;
    public String facilities;
    public String hotelName;
    public String photos;
    public String description;
    public boolean isActive;
    public String userId;
    public Timestamp dataCreation;
    public Timestamp dataEdit;
    public Coordinates coordinates;


    public Hotel(String id, String facilities, String hotelName, String photos, String description, boolean isActive, String userId, Timestamp dataCreation, Timestamp dataEdit, Coordinates coordinates) {
        this.id = id;
        this.facilities = facilities;
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
