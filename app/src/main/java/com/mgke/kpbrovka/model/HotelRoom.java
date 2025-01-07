package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class HotelRoom {

    public String id;
    public String hotelId;
    public String photos;

    public String name;
    public String description;
    public String typeOfBed;

    public List<String> facilities = new ArrayList<>();
    public double costWithout;
    public double costWith;
    public int count;
    public int countOfPeople;


    public HotelRoom() {
    }

    public HotelRoom(String id, String hotelId, String photos, String name, String description, String typeOfBed, List<String> facilities, double costWithout, double costWith, int count, int countOfPeople) {
        this.id = id;
        this.hotelId = hotelId;
        this.photos = photos;
        this.name = name;
        this.description = description;
        this.typeOfBed = typeOfBed;
        this.facilities = facilities;
        this.costWithout = costWithout;
        this.costWith = costWith;
        this.count = count;
        this.countOfPeople = countOfPeople;
    }
}
