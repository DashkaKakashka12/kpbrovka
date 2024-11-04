package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

public class Review {
    public String id;
    public String reservationId;
    public Timestamp dataCreation;
    public Timestamp dataEdit;
    public int valueForMoney;
    public int comfort;
    public int cleanness;
    public int staff;
    public int facilities;
    public String text;

    public Review() {
    }

    public Review(String id, String reservationId, Timestamp dataCreation, Timestamp dataEdit, int valueForMoney, int comfort, int cleanness, int staff, int facilities, String text) {
        this.id = id;
        this.reservationId = reservationId;
        this.dataCreation = dataCreation;
        this.dataEdit = dataEdit;
        this.valueForMoney = valueForMoney;
        this.comfort = comfort;
        this.cleanness = cleanness;
        this.staff = staff;
        this.facilities = facilities;
        this.text = text;
    }
}
