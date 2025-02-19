package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

public class Review {
    public String id;
    public String userId;
    public String reservationId;
    public Timestamp dataCreation;
    public int stars;
    public int valueForMoney;
    public int comfort;
    public int cleanness;
    public int staff;

    public int facilities;

    public String text;

    public int getStars() {
        return stars;
    }

    public Review() {
    }

    public Review(String id, String userId, String reservationId, Timestamp dataCreation, int stars, int valueForMoney, int comfort, int cleanness, int staff, int facilities, String text) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.dataCreation = dataCreation;
        this.stars = stars;
        this.valueForMoney = valueForMoney;
        this.comfort = comfort;
        this.cleanness = cleanness;
        this.staff = staff;
        this.facilities = facilities;
        this.text = text;
    }
}
