package com.mgke.kpbrovka.model;


public class User {
    public  String id;
    public UserType type;
    public String name;
    public String pass;
    public String email;
    public String photo;

    public User() {
    }

    public User(String id, UserType type, String name, String pass, String email, String photo) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.pass = pass;
        this.email = email;
        this.photo = photo;
    }
}
