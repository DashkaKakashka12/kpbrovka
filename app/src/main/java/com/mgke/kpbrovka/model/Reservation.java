package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

public class Reservation {
    public String id;
    public Timestamp dataCreation;
    public Timestamp dataEdit;
    public String userId;
    public String contactInformation;
    public String wishesForTheNumber;
    public String status;
    public String hotelRoomId;

    public Reservation() {
    }

    public Reservation(String id, Timestamp dataCreation, Timestamp dataEdit, String userId, String contactInformation, String wishesForTheNumber, String status, String hotelRoomId) {
        this.id = id;
        this.dataCreation = dataCreation;
        this.dataEdit = dataEdit;
        this.userId = userId;
        this.contactInformation = contactInformation;
        this.wishesForTheNumber = wishesForTheNumber;
        this.status = status;
        this.hotelRoomId = hotelRoomId;
    }
}
