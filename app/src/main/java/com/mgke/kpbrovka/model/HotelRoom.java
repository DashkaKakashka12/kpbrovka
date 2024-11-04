package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

public class HotelRoom {

    public String id;
    public String hotelId;
    public Timestamp dataCreation;
    public Timestamp dataEdit;
    public String importantInformation;
    public String photos;
    public String livingConditions;
    public String facilities;
    public String text;

    public HotelRoom() {
    }

    public HotelRoom(String id, String hotelId, Timestamp dataCreation, Timestamp dataEdit, String importantInformation, String photos, String livingConditions, String facilities, String text) {
        this.id = id;
        this.hotelId = hotelId;
        this.dataCreation = dataCreation;
        this.dataEdit = dataEdit;
        this.importantInformation = importantInformation;
        this.photos = photos;
        this.livingConditions = livingConditions;
        this.facilities = facilities;
        this.text = text;
    }
}
