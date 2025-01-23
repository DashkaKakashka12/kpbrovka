package com.mgke.kpbrovka.model;

public class Like {
    public String id;
    public String hotelId;
    public String userId;

    public Like() {}

    public Like(String id, String hotelId, String userId) {
        this.id = id;
        this.hotelId = hotelId;
        this.userId = userId;
    }
}

