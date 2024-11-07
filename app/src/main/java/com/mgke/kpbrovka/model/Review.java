package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

public class Review {
    public String id;
    public String reservationId;
    public Timestamp dataCreation;
    public int stars;
    public double valueForMoney;
    public double comfort;
    public double cleanness;
    public double staff;

    public double facilities;

    public String text;

    public Review() {
    }

    public Review(String id, String reservationId, Timestamp dataCreation, int stars, double valueForMoney, double comfort, double cleanness, double staff, double facilities, String text) {
        this.id = id;
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
