package com.mgke.kpbrovka.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Reservation {
    public String id;
    public String userId;
    public String wishesForTheNumber;
    public StatusReservation status;
    public String hotelRoomId;
    public Date start;
    public Date end;
    public String userName;
    public String userSurname;
    public String userEmail;
    public String userPhone;
    public String numberOfCard;
    public String dateOfCard;
    public String nameOfCard;
    public String ccv;
    public boolean checkBoxBreakfast;
    public boolean checkBoxLunch;
    public boolean checkBoxDinner;
    public boolean switchPrepayment;
    public boolean switchAllCost;
    public int parking;


    public Reservation() {
    }

    public Reservation(String id, String userId, String wishesForTheNumber, StatusReservation status, String hotelRoomId, Date start, Date end, String userName, String userSurname, String userEmail, String userPhone, String numberOfCard, String dateOfCard, String nameOfCard, String ccv, boolean checkBoxBreakfast, boolean checkBoxLunch, boolean checkBoxDinner, boolean switchPrepayment, boolean switchAllCost, int parking) {
        this.id = id;
        this.userId = userId;
        this.wishesForTheNumber = wishesForTheNumber;
        this.status = status;
        this.hotelRoomId = hotelRoomId;
        this.start = start;
        this.end = end;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.numberOfCard = numberOfCard;
        this.dateOfCard = dateOfCard;
        this.nameOfCard = nameOfCard;
        this.ccv = ccv;
        this.checkBoxBreakfast = checkBoxBreakfast;
        this.checkBoxLunch = checkBoxLunch;
        this.checkBoxDinner = checkBoxDinner;
        this.switchPrepayment = switchPrepayment;
        this.switchAllCost = switchAllCost;
        this.parking = parking;
    }
}
