package com.mgke.kpbrovka.model;


import com.google.firebase.Timestamp;

public class NearbyAttraction {
    public String id;
    public String description;
    public String photos;
    public Coordinates coordinates;

    public NearbyAttraction(String id, String description, String photos, Coordinates coordinates) {
        this.id = id;
        this.description = description;
        this.photos = photos;
        this.coordinates = coordinates;
    }

    public NearbyAttraction() {
    }
}
