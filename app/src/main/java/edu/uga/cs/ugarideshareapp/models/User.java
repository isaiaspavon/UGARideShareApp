package edu.uga.cs.ugarideshareapp.models;

public class User {
    public String uid, name, email;
    public int points;

    public User() {} // Firebase needs empty constructor

    public User(String uid, String name, String email, int points) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.points = points;
    }
}
