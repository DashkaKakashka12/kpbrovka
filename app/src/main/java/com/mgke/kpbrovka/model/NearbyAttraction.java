package com.mgke.kpbrovka.model;


import com.google.firebase.Timestamp;

public class NearbyAttraction {
    public String id;
    public String description;
    public String photos;
    public Timestamp dataCreation;
    public Timestamp dataEdit;
    public Coordinates coordinates;

    public NearbyAttraction(String id, String description, String photos, Timestamp dataCreation, Timestamp dataEdit, Coordinates coordinates) {
        this.id = id;
        this.description = description;
        this.photos = photos;
        this.dataCreation = dataCreation;
        this.dataEdit = dataEdit;
        this.coordinates = coordinates;
    }

    public NearbyAttraction() {
    }
}
