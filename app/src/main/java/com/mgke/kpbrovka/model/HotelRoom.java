package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class HotelRoom {

    public String id;
    public String hotelId;
    public Timestamp dataCreation;
    public String photos;

    public String name;

    public List<String> facilities = new ArrayList<>();
    public double costWithout;
    public double costWith;
    public int count;




    public HotelRoom() {
    }

    public HotelRoom(String id, String hotelId, Timestamp dataCreation, String photos, String name, List<String> facilities, double costWithout, double costWith, int count) {
        this.id = id;
        this.hotelId = hotelId;
        this.dataCreation = dataCreation;
        this.photos = photos;
        this.name = name;
        this.facilities = facilities;
        this.costWithout = costWithout;
        this.costWith = costWith;
        this.count = count;
    }
}
